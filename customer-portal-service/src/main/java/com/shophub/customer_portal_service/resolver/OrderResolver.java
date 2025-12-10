package com.shophub.customer_portal_service.resolver;

import com.shophub.customer_portal_service.entity.Customer;
import com.shophub.customer_portal_service.entity.Order;
import com.shophub.customer_portal_service.service.CustomerService;
import com.shophub.customer_portal_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderResolver {
    
    private final OrderService orderService;
    private final CustomerService customerService;
    
    // Queries
    @QueryMapping
    public List<Order> orders() {
        return orderService.getAllOrders();
    }
    
    @QueryMapping
    public Order order(@Argument Long id) {
        return orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    @QueryMapping
    public List<Order> ordersByCustomer(@Argument Long customerId) {
        return orderService.getOrdersByCustomerId(customerId);
    }
    
    // Mutations
    @MutationMapping
    public Order createOrder(@Argument Long customerId,
                            @Argument Long productId,
                            @Argument Integer quantity,
                            @Argument BigDecimal totalAmount) {
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalAmount(totalAmount);
        
        return orderService.createOrder(order);
    }
    
    @MutationMapping
    public Order updateOrderStatus(@Argument Long id, @Argument String status) {
        return orderService.updateOrderStatus(id, status);
    }
    
    // Field resolver - Get customer for an order
    @SchemaMapping(typeName = "Order", field = "customer")
    public Customer customer(Order order) {
        return customerService.getCustomerById(order.getCustomerId())
                .orElse(null);
    }
}