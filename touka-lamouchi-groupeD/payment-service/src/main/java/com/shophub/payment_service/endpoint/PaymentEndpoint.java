package com.shophub.payment_service.endpoint;

import com.shophub.payment_service.entity.Payment;
import com.shophub.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.math.BigDecimal;

@Endpoint
@RequiredArgsConstructor
public class PaymentEndpoint {
    
    private static final String NAMESPACE_URI = "http://shophub.com/payment";
    
    private final PaymentService paymentService;
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "processPaymentRequest")
    @ResponsePayload
    public Element processPayment(@RequestPayload Element request) throws Exception {
        // Extraire les données de la requête XML
        Long orderId = Long.parseLong(getTextContent(request, "orderId"));
        BigDecimal amount = new BigDecimal(getTextContent(request, "amount"));
        String paymentMethod = getTextContent(request, "paymentMethod");
        
        // Créer et traiter le paiement
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        
        Payment processed = paymentService.processPayment(payment);
        
        // Créer la réponse XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        
        Element response = doc.createElementNS(NAMESPACE_URI, "processPaymentResponse");
        
        Element transactionId = doc.createElementNS(NAMESPACE_URI, "transactionId");
        transactionId.setTextContent(processed.getTransactionId());
        response.appendChild(transactionId);
        
        Element status = doc.createElementNS(NAMESPACE_URI, "status");
        status.setTextContent(processed.getStatus());
        response.appendChild(status);
        
        Element message = doc.createElementNS(NAMESPACE_URI, "message");
        message.setTextContent("Payment processed successfully");
        response.appendChild(message);
        
        return response;
    }
    
    private String getTextContent(Element parent, String tagName) {
        return parent.getElementsByTagNameNS(NAMESPACE_URI, tagName)
                .item(0)
                .getTextContent();
    }
}