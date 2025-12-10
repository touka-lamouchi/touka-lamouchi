package com.shophub.payment_service.controller;

import com.shophub.payment_service.entity.Payment;
import com.shophub.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    // GET - Récupérer tous les paiements
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    
    // GET - Récupérer un paiement par ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET - Récupérer les paiements par Order ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
    }
    
    // GET - Récupérer les paiements par statut
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }
    
    // GET - Récupérer un paiement par Transaction ID
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Payment> getPaymentByTransactionId(@PathVariable String transactionId) {
        return paymentService.getPaymentByTransactionId(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // POST - Créer un paiement
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        Payment created = paymentService.processPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // POST - Rembourser un paiement
    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> refundPayment(@PathVariable Long id) {
        try {
            Payment refunded = paymentService.refundPayment(id);
            return ResponseEntity.ok(refunded);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
