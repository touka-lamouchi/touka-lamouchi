package com.example.order_tracking_service.service;

import com.example.order_tracking_service.entity.Order;
import com.example.order_tracking_service.repository.OrderRepository;
import com.shophub.ordertracking.grpc.OrderRequest;
import com.shophub.ordertracking.grpc.OrderStatusUpdate;
import com.shophub.ordertracking.grpc.OrderTrackingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@GrpcService
public class TrackingServiceImpl extends OrderTrackingServiceGrpc.OrderTrackingServiceImplBase {

    private final OrderRepository orderRepository;
    
    // 1. STATIC MAP (Shared memory for all parts of the app)
    private static final Map<String, StreamObserver<OrderStatusUpdate>> activeObservers = new ConcurrentHashMap<>();
    
    public TrackingServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void streamOrderStatus(OrderRequest request, StreamObserver<OrderStatusUpdate> responseObserver) {
        String orderId = request.getOrderId();
        System.out.println("✅ Client connected for Order ID: " + orderId);

        // 2. SAVE THE CONNECTION (Crucial Step!)
        activeObservers.put(orderId, responseObserver);

        // 3. Send a Welcome Message and KEEP OPEN (No onCompleted yet)
        envoyerStatut(responseObserver, "EN_ATTENTE", "Connexion établie. En attente du magasin...");
    }

    // This is called by the REST endpoint
    public void updateOrderManually(String orderId, String status, String message) {
        // Update database first
        try {
            Long id = Long.parseLong(orderId);
            orderRepository.findById(id).ifPresent(order -> {
                order.setStatus(status);
                orderRepository.save(order);
                System.out.println("✅ Database updated: Order " + orderId + " -> " + status);
            });
        } catch (Exception e) {
            System.err.println("⚠️ Failed to update database: " + e.getMessage());
        }
        
        // Then notify gRPC stream if active
        if (activeObservers.containsKey(orderId)) {
            StreamObserver<OrderStatusUpdate> observer = activeObservers.get(orderId);
            
            envoyerStatut(observer, status, message);
            System.out.println("📤 Sent gRPC update for Order " + orderId + ": " + status);

            // Only close if Delivered/Cancelled
            if ("DELIVERED".equals(status) || "CANCELLED".equals(status)) {
                observer.onCompleted();
                activeObservers.remove(orderId);
                System.out.println("❌ Connection closed for Order " + orderId);
            }
        } else {
            System.out.println("ℹ️ No active gRPC stream for Order " + orderId + " (database still updated)");
        }
    }

    private void envoyerStatut(StreamObserver<OrderStatusUpdate> observer, String status, String msg) {
        try {
            OrderStatusUpdate update = OrderStatusUpdate.newBuilder()
                    .setStatus(status)
                    .setMessage(msg)
                    .setTimestamp(LocalDateTime.now().toString())
                    .build();
            observer.onNext(update);
        } catch (Exception e) {
            System.err.println("Client disconnected.");
            activeObservers.values().remove(observer);
        }
    }
}