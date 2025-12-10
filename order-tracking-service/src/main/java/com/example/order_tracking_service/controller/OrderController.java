package com.example.order_tracking_service.controller;

import com.example.order_tracking_service.entity.Order;
import com.example.order_tracking_service.repository.OrderRepository;
import com.example.order_tracking_service.service.TrackingServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
// This means all URLs will start with /api/orders
@RequestMapping("/api/orders") 
public class OrderController {

    private final TrackingServiceImpl trackingService;
    private final OrderRepository orderRepository;

    // We inject your Service and Repository so the Controller can talk to them
    public OrderController(TrackingServiceImpl trackingService, OrderRepository orderRepository) {
        this.trackingService = trackingService;
        this.orderRepository = orderRepository;
    }

    // GET http://localhost:8086/api/orders/1
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // URL to trigger an update:
    // POST http://localhost:8086/api/orders/3/update?status=SHIPPED&msg=Truck_Is_Moving
    @PostMapping("/{id}/update")
    public String updateOrder(@PathVariable String id, 
                              @RequestParam String status, 
                              @RequestParam String msg) {
        
        // Call the new method we added to your Service
        trackingService.updateOrderManually(id, status, msg);
        
        return "✅ Update sent to Order " + id + ": " + status;
    }
}