package com.example.order_tracking_service.service;

import com.shophub.ordertracking.grpc.OrderRequest;
import com.shophub.ordertracking.grpc.OrderStatusUpdate;
import com.shophub.ordertracking.grpc.OrderTrackingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service; // Use @Service instead of @Component

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@GrpcService
public class TrackingServiceImpl extends OrderTrackingServiceGrpc.OrderTrackingServiceImplBase {

    // 1. STATIC MAP (Shared memory for all parts of the app)
    private static final Map<String, StreamObserver<OrderStatusUpdate>> activeObservers = new ConcurrentHashMap<>();

    @Override
    public void streamOrderStatus(OrderRequest request, StreamObserver<OrderStatusUpdate> responseObserver) {
        String orderId = request.getOrderId();
        System.out.println("✅ Client connected for Order ID: " + orderId);

        // 2. SAVE THE CONNECTION (Crucial Step!)
        activeObservers.put(orderId, responseObserver);

        // 3. Send a Welcome Message and KEEP OPEN (No onCompleted yet)
        envoyerStatut(responseObserver, "EN_ATTENTE", "Connexion établie. En attente du magasin...");
    }

    // This is called by Postman
    public void updateOrderManually(String orderId, String status, String message) {
        if (activeObservers.containsKey(orderId)) {
            StreamObserver<OrderStatusUpdate> observer = activeObservers.get(orderId);
            
            envoyerStatut(observer, status, message);
            System.out.println("📤 Sent update for Order " + orderId + ": " + status);

            // Only close if Delivered/Cancelled
            if ("LIVREE".equals(status) || "ANNULEE".equals(status)) {
                observer.onCompleted();
                activeObservers.remove(orderId);
                System.out.println("❌ Connection closed for Order " + orderId);
            }
        } else {
            System.err.println("⚠️ No active client found for Order ID: " + orderId);
            System.out.println("   (Available IDs in memory: " + activeObservers.keySet() + ")");
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