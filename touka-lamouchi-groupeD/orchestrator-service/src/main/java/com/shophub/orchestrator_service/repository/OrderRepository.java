package com.shophub.orchestrator_service.repository;

import com.shophub.orchestrator_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Cette ligne permet de chercher toutes les commandes d'un client spécifique
    List<Order> findByCustomerId(Long customerId);
}