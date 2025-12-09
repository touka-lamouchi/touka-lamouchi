package com.shophub.orchestrator_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// This acts as the "remote control" for your Order Tracking Service (Port 8086)
@FeignClient(name = "order-tracking-service", url = "http://localhost:8086")
public interface OrderTrackingClient {

    // Matches the Controller we tested in Postman: 
    // POST /api/orders/{id}/update?status=X&msg=Y
    @PostMapping("/api/orders/{id}/update")
    void updateOrderStatus(@PathVariable("id") String orderId,
                           @RequestParam("status") String status,
                           @RequestParam("msg") String message);
}