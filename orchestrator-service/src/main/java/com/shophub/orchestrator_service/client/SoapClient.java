package com.shophub.orchestrator_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SoapClient {

    @Value("${service.payment.url}") // Lit http://localhost:8082/ws/payments
    private String paymentUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean processPayment(Long orderId, Double amount) {
        // Construction du XML (Exactement comme dans ton test Postman SOAP)
        // Note: Assure-toi que les namespaces (xmlns) correspondent à ton WSDL
        String soapRequest = 
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:pay=\"http://shophub.com/payment\">" +
            "   <soapenv:Header/>" +
            "   <soapenv:Body>" +
            "      <pay:processPaymentRequest>" +
            "         <pay:orderId>" + orderId + "</pay:orderId>" +
            "         <pay:amount>" + amount + "</pay:amount>" +
            "         <pay:paymentMethod>CREDIT_CARD</pay:paymentMethod>" +
            "      </pay:processPaymentRequest>" +
            "   </soapenv:Body>" +
            "</soapenv:Envelope>";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);

        HttpEntity<String> request = new HttpEntity<>(soapRequest, headers);

        try {
            // Envoi de la requête SOAP
            String response = restTemplate.postForObject(paymentUrl, request, String.class);
            
            // Analyse simple : Si la réponse contient "SUCCESS", c'est bon
            // Adapte "Payment processed successfully" selon ce que ton SOAP renvoie vraiment
            return response != null && response.contains("processed successfully");
        } catch (Exception e) {
            System.err.println("Erreur appel SOAP: " + e.getMessage());
            return false;
        }
    }
}