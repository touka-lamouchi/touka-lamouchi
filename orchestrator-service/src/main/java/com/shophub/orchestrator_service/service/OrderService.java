package com.shophub.orchestrator_service.service;

import com.shophub.orchestrator_service.client.OrderTrackingClient; // 👈 New Import
import com.shophub.orchestrator_service.client.ProductClient;
import com.shophub.orchestrator_service.client.ProductDTO;
import com.shophub.orchestrator_service.client.SoapClient;
import com.shophub.orchestrator_service.entity.Order;
import com.shophub.orchestrator_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductClient productClient;
    private final SoapClient soapClient;
    private final OrderRepository orderRepository;
    private final OrderTrackingClient trackingClient; // 👈 1. INJECT THE NEW CLIENT

    public Order createOrder(Long customerId, Long productId, int quantity) {
        // 1. APPEL REST : Vérifier le produit
        System.out.println("---- Etape 1: Appel REST Product Service ----");
        ProductDTO product = productClient.getProduct(productId);
        
        if (product == null) {
            throw new RuntimeException("Produit introuvable ID: " + productId);
        }

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

        // 3. APPEL SOAP : Effectuer le paiement
        System.out.println("---- Etape 2: Appel SOAP Payment Service ----");
        boolean paymentSuccess = soapClient.processPayment(order.getId(), totalAmount);

        // 4. Mettre à jour le statut selon le résultat du paiement
        if (paymentSuccess) {
            order.setStatus("CONFIRMED");
            System.out.println("---- Paiement Succès : Commande CONFIRMED ----");

            // 👇 5. NOUVELLE ETAPE : NOTIFIER LE TRACKING SERVICE
            try {
                System.out.println("---- Etape 3: Notification Tracking Service ----");
                trackingClient.updateOrderStatus(
                    String.valueOf(order.getId()), 
                    "EN_PREPARATION", 
                    "Paiement validé. Votre commande est en préparation."
                );
                System.out.println("📢 Tracking Service notifié avec succès !");
            } catch (Exception e) {
                // On log juste l'erreur, on ne bloque pas la commande pour ça
                System.err.println("⚠️ Erreur notification Tracking: " + e.getMessage());
            }

        } else {
            order.setStatus("PAYMENT_FAILED");
            System.out.println("---- Paiement Echec ----");
        }

        return orderRepository.save(order);
    }
}