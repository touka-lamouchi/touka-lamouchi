package com.shophub.orchestrator_service.client;

// AJOUTE "public" DEVANT record
public record ProductDTO(Long id, String name, Double price, Integer stock) {}