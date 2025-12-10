package com.shophub.orchestrator_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


// Configuration Feign : on pointe vers l'URL définie dans application.properties
@FeignClient(name = "product-service", url = "${service.product.url}")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductDTO getProduct(@PathVariable("id") Long id);
    
    @GetMapping("/api/products/{id}/check-stock")
    Boolean checkStock(@PathVariable("id") Long id, @org.springframework.web.bind.annotation.RequestParam("quantity") Integer quantity);
    
    @org.springframework.web.bind.annotation.PostMapping("/api/products/{id}/reduce-stock")
    void reduceStock(@PathVariable("id") Long id, @org.springframework.web.bind.annotation.RequestParam("quantity") Integer quantity);
}