package com.example.order_tracking_service.controller;

import com.example.order_tracking_service.service.TrackingServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
// This means all URLs will start with /api/orders
@RequestMapping("/api/orders") 
public class OrderController {

    private final TrackingServiceImpl trackingService;

    // We inject your Service so the Controller can talk to it
    public OrderController(TrackingServiceImpl trackingService) {
        this.trackingService = trackingService;
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