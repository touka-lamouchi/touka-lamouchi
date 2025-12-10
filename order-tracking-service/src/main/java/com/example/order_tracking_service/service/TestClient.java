package com.example.order_tracking_service.service;

import com.shophub.ordertracking.grpc.OrderRequest;
import com.shophub.ordertracking.grpc.OrderStatusUpdate;
import com.shophub.ordertracking.grpc.OrderTrackingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Iterator;

// This simulates a User connecting via the Customer Portal
@Component 
public class TestClient implements CommandLineRunner {

    @Override
    public void run(String... args) {
        new Thread(() -> {
            try {
                // Wait for the gRPC server (Port 8084) to be ready
                Thread.sleep(3000);

                System.out.println("🔹 SHOPHUB TEST: Connecting to gRPC Stream on Port 8084...");

                ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8084)
                        .usePlaintext()
                        .build();

                var stub = OrderTrackingServiceGrpc.newBlockingStub(channel);
                
                // We simulate a Customer asking to track Order #3
                OrderRequest request = OrderRequest.newBuilder().setOrderId("5").build();
                
                System.out.println("🔹 SHOPHUB TEST: Listening for updates on Order #3...");
                
                // This line keeps the connection OPEN
                Iterator<OrderStatusUpdate> updates = stub.streamOrderStatus(request);

                while (updates.hasNext()) {
                    System.out.println("📡 UPDATE RECEIVED: " + updates.next().getStatus());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}