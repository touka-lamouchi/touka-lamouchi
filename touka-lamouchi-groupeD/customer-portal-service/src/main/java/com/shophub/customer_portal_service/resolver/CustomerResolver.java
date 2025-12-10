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

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerResolver {
    
    private final CustomerService customerService;
    private final OrderService orderService;
    
    @QueryMapping
    public List<Customer> customers() {
        return customerService.getAllCustomers();
    }
    
    @QueryMapping
    public Customer customer(@Argument Long id) {
        return customerService.getCustomerById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
    
    @QueryMapping
    public Customer customerByEmail(@Argument String email) {
        return customerService.getCustomerByEmail(email)
                .orElse(null); // Return null if not found, don't throw exception
    }
    
    @MutationMapping
    public Customer createCustomer(
            @Argument String name, 
            @Argument String email,
            @Argument String phoneNumber,
            @Argument String address) {
        
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);
        
        return customerService.createCustomer(customer);
    }
    
    @MutationMapping
    public Customer updateCustomer(
            @Argument Long id,
            @Argument String name,
            @Argument String email,
            @Argument String phoneNumber,
            @Argument String address) {
        
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);
        
        return customerService.updateCustomer(id, customer);
    }
    
    @SchemaMapping(typeName = "Customer", field = "orders")
    public List<Order> orders(Customer customer) {
        return orderService.getOrdersByCustomerId(customer.getId());
    }
}