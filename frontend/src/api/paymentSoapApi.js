import axios from 'axios';

// SOAP endpoint through API Gateway
const SOAP_ENDPOINT = 'http://localhost:8080/ws';

// Helper to create SOAP envelope
const createSOAPEnvelope = (body) => {
  return `<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:pay="http://shophub.com/payment">
  <soap:Body>
    ${body}
  </soap:Body>
</soap:Envelope>`;
};

// Parse SOAP response
const parseSOAPResponse = (xmlString) => {
  const parser = new DOMParser();
  const xmlDoc = parser.parseFromString(xmlString, 'text/xml');
  
  // Check for SOAP fault
  const fault = xmlDoc.getElementsByTagName('soap:Fault')[0];
  if (fault) {
    const faultString = fault.getElementsByTagName('faultstring')[0]?.textContent;
    throw new Error(`SOAP Fault: ${faultString || 'Unknown error'}`);
  }
  
  return xmlDoc;
};

export const paymentSoapApi = {
  // Process payment via SOAP
  processPayment: async (orderId, amount, paymentMethod) => {
    const soapBody = `
    <pay:processPaymentRequest>
      <pay:orderId>${orderId}</pay:orderId>
      <pay:amount>${amount}</pay:amount>
      <pay:paymentMethod>${paymentMethod}</pay:paymentMethod>
    </pay:processPaymentRequest>`;

    try {
      const response = await axios.post(SOAP_ENDPOINT, createSOAPEnvelope(soapBody), {
        headers: {
          'Content-Type': 'text/xml; charset=utf-8',
          'SOAPAction': ''
        }
      });

      const xmlDoc = parseSOAPResponse(response.data);
      const transactionId = xmlDoc.getElementsByTagName('transactionId')[0]?.textContent;
      const status = xmlDoc.getElementsByTagName('status')[0]?.textContent;

      return {
        transactionId,
        status,
        orderId,
        amount
      };
    } catch (error) {
      console.error('SOAP Payment Error:', error);
      throw error;
    }
  },

  // Validate payment via SOAP
  validatePayment: async (transactionId) => {
    const soapBody = `
    <pay:ValidatePaymentRequest>
      <pay:transactionId>${transactionId}</pay:transactionId>
    </pay:ValidatePaymentRequest>`;

    try {
      const response = await axios.post(SOAP_ENDPOINT, createSOAPEnvelope(soapBody), {
        headers: {
          'Content-Type': 'text/xml; charset=utf-8',
          'SOAPAction': 'validatePayment'
        }
      });

      const xmlDoc = parseSOAPResponse(response.data);
      const isValid = xmlDoc.getElementsByTagName('isValid')[0]?.textContent === 'true';
      const status = xmlDoc.getElementsByTagName('status')[0]?.textContent;

      return {
        transactionId,
        isValid,
        status
      };
    } catch (error) {
      console.error('SOAP Validation Error:', error);
      throw error;
    }
  },

  // Get payment status via SOAP
  getPaymentStatus: async (transactionId) => {
    const soapBody = `
    <pay:GetPaymentStatusRequest>
      <pay:transactionId>${transactionId}</pay:transactionId>
    </pay:GetPaymentStatusRequest>`;

    try {
      const response = await axios.post(SOAP_ENDPOINT, createSOAPEnvelope(soapBody), {
        headers: {
          'Content-Type': 'text/xml; charset=utf-8',
          'SOAPAction': 'getPaymentStatus'
        }
      });

      const xmlDoc = parseSOAPResponse(response.data);
      const status = xmlDoc.getElementsByTagName('status')[0]?.textContent;
      const amount = parseFloat(xmlDoc.getElementsByTagName('amount')[0]?.textContent || '0');

      return {
        transactionId,
        status,
        amount
      };
    } catch (error) {
      console.error('SOAP Status Error:', error);
      throw error;
    }
  }
};
