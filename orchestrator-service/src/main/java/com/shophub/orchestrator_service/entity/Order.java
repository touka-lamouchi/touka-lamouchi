package com.shophub.orchestrator_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // "orders" car "order" est interdit en SQL
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long productId;
    private int quantity;
    private Double totalAmount;
    
    // Sera: PENDING_PAYMENT, CONFIRMED, ou FAILED
    private String status; 
    
    private LocalDateTime orderDate;
}