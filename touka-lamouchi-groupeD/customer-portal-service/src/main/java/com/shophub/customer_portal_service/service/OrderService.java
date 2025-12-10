package com.shophub.customer_portal_service.service;

import com.shophub.customer_portal_service.entity.Order;
import com.shophub.customer_portal_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // 1. Added import
import java.util.Arrays; // 2. Added import
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;

    // Configuration to talk to Orchestrator via Docker network
    private final RestTemplate restTemplate = new RestTemplate();
    private final String ORCHESTRATOR_URL = "http://orchestrator-service:8085/api/orchestrator/customer/";
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    // --- 4. MODIFIED: Now fetches from Orchestrator instead of local DB ---
    public List<Order> getOrdersByCustomerId(Long customerId) {
        try {
            // Try to get data from the Orchestrator
            Order[] orders = restTemplate.getForObject(ORCHESTRATOR_URL + customerId, Order[].class);
            if (orders != null) {
                return Arrays.asList(orders);
            }
        } catch (Exception e) {
            System.err.println("Orchestrator not reachable, using local DB: " + e.getMessage());
        }
        // Fallback: If Orchestrator fails, return local data
        return orderRepository.findByCustomerId(customerId);
    }
    
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
    
    public Order createOrder(Order order) {
        order.setStatus("PENDING");
        return orderRepository.save(order);
    }
    
    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }
}