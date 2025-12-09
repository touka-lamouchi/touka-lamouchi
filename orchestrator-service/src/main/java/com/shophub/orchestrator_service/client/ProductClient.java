package com.shophub.orchestrator_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


// Configuration Feign : on pointe vers l'URL définie dans application.properties
@FeignClient(name = "product-service", url = "${service.product.url}")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductDTO getProduct(@PathVariable("id") Long id);
}