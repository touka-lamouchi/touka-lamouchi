package com.shophub.orchestrator_service.controller;

import com.shophub.orchestrator_service.entity.Order;
import com.shophub.orchestrator_service.service.OrderService;
import com.shophub.orchestrator_service.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// DTO pour la requête de création
record OrderRequest(Long customerId, Long productId, int quantity) {}

@RestController
@RequestMapping("/api/orchestrator")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository; // On ajoute le repository

    // On injecte les deux services dans le constructeur
    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    // 1. Pour CRÉER une commande (POST)
    @PostMapping("/create")
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(
            request.customerId(), 
            request.productId(), 
            request.quantity()
        );
    }

    // 2. Pour LIRE les commandes d'un client (GET) - C'est ce qui manquait !
    @GetMapping("/customer/{customerId}")
    public List<Order> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}