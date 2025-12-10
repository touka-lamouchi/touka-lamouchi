package com.shophub.orchestrator_service.service;

import com.shophub.orchestrator_service.client.OrderTrackingClient;
import com.shophub.orchestrator_service.client.ProductClient;
import com.shophub.orchestrator_service.client.ProductDTO;
import com.shophub.orchestrator_service.client.SoapClient;
import com.shophub.orchestrator_service.entity.Order;
import com.shophub.orchestrator_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductClient productClient;
    private final SoapClient soapClient;
    private final OrderRepository orderRepository;
    private final OrderTrackingClient trackingClient;
    private final RestTemplate restTemplate = new RestTemplate();

    public Order createOrder(Long customerId, Long productId, int quantity) {
        // 1. APPEL REST : Vérifier le produit et stock final
        System.out.println("---- Etape 1: Vérifier stock final (REST) ----");
        ProductDTO product = productClient.getProduct(productId);
        
        if (product == null) {
            throw new RuntimeException("Produit introuvable ID: " + productId);
        }
        
        // Vérifier stock disponible
        Boolean stockAvailable = productClient.checkStock(productId, quantity);
        if (!stockAvailable) {
            throw new RuntimeException("Stock insuffisant pour le produit ID: " + productId);
        }
        System.out.println("✅ Stock disponible pour " + quantity + " unité(s)");

        double totalAmount = product.price() * quantity;

        // 2. Créer la commande en base (Statut PENDING)
        Order order = Order.builder()
                .customerId(customerId)
                .productId(productId)
                .quantity(quantity)
                .totalAmount(totalAmount)
                .status("PENDING_PAYMENT")
                .orderDate(LocalDateTime.now())
                .build();
        
        order = orderRepository.save(order);
        System.out.println("---- Commande créée (PENDING) ID: " + order.getId() + " ----");

        // 3. APPEL SOAP : Traiter le paiement
        System.out.println("---- Etape 2: Traite paiement (SOAP) ----");
        boolean paymentSuccess = soapClient.processPayment(order.getId(), totalAmount);

        // 4. Mettre à jour le statut selon le résultat du paiement
        if (paymentSuccess) {
            order.setStatus("CONFIRMED");
            System.out.println("✅ Paiement Succès : Commande CONFIRMED");

            // 5. APPEL REST : Réduire le stock
            try {
                System.out.println("---- Etape 3: Réduit stock (REST) ----");
                productClient.reduceStock(productId, quantity);
                System.out.println("✅ Stock réduit de " + quantity + " unité(s)");
            } catch (Exception e) {
                System.err.println("⚠️ Erreur réduction stock: " + e.getMessage());
                // Continue même si réduction échoue (à améliorer avec compensation)
            }

            // 6. APPEL REST : Active tracking (gRPC notification via REST)
            try {
                System.out.println("---- Etape 4: Active tracking (gRPC) ----");
                trackingClient.updateOrderStatus(
                    String.valueOf(order.getId()), 
                    "CONFIRMED", 
                    "Commande confirmée. Paiement validé."
                );
                System.out.println("✅ Tracking activé - Client recevra notifications en temps réel");
            } catch (Exception e) {
                System.err.println("⚠️ Erreur activation tracking: " + e.getMessage());
            }
            
            // 7. Save order to customer-portal database for GraphQL queries
            try {
                System.out.println("---- Etape 5: Enregistre commande pour GraphQL ----");
                Map<String, Object> portalOrder = new HashMap<>();
                portalOrder.put("customerId", order.getCustomerId());
                portalOrder.put("productId", order.getProductId());
                portalOrder.put("quantity", order.getQuantity());
                portalOrder.put("totalAmount", order.getTotalAmount());
                portalOrder.put("status", order.getStatus());
                portalOrder.put("createdAt", order.getOrderDate());
                
                restTemplate.postForObject(
                    "http://customer-portal-service:8083/api/orders",
                    portalOrder,
                    Object.class
                );
                System.out.println("✅ Commande disponible pour consultation profil (GraphQL)");
            } catch (Exception e) {
                System.err.println("⚠️ Erreur saving to portal: " + e.getMessage());
            }

        } else {
            order.setStatus("PAYMENT_FAILED");
            System.out.println("---- Paiement Echec ----");
        }

        return orderRepository.save(order);
    }
}