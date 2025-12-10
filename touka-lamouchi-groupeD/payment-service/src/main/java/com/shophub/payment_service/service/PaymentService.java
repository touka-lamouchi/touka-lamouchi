package com.shophub.payment_service.service;

import com.shophub.payment_service.entity.Payment;
import com.shophub.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    public Payment processPayment(Payment payment) {
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setStatus("PENDING");
        
        boolean paymentSuccessful = simulatePaymentGateway();
        
        if (paymentSuccessful) {
            payment.setStatus("COMPLETED");
        } else {
            payment.setStatus("FAILED");
        }
        
        return paymentRepository.save(payment);
    }
    
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
    
    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }
    
    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
    
    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (!"COMPLETED".equals(payment.getStatus())) {
            throw new RuntimeException("Only completed payments can be refunded");
        }
        
        payment.setStatus("REFUNDED");
        return paymentRepository.save(payment);
    }
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }
    
    private boolean simulatePaymentGateway() {
        return Math.random() > 0.1;
    }
}