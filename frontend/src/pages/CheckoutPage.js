import React, { useState } from 'react';
import { orderApi } from '../api/orderApi';
import { paymentSoapApi } from '../api/paymentSoapApi';
import { gql } from '@apollo/client';
import { apolloClient } from '../apolloClient';

function CheckoutPage({ cart, onSuccess, onCustomerCreated }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState('CREDIT_CARD');
  const [customerInfo, setCustomerInfo] = useState({
    name: '',
    email: '',
    phoneNumber: '',
    address: ''
  });

  const handleCheckout = async () => {
    try {
      setLoading(true);
      setError(null);

      if (cart.length === 0) {
        setError('Cart is empty');
        return;
      }

      // Validate customer info
      if (!customerInfo.name || !customerInfo.email || !customerInfo.address) {
        setError('Please fill in all customer information');
        setLoading(false);
        return;
      }

      // Step 1: Check if customer exists by email via GraphQL
      const GET_CUSTOMER_BY_EMAIL = gql`
        query GetCustomerByEmail($email: String!) {
          customerByEmail(email: $email) {
            id
            name
            email
          }
        }
      `;

      let customerId;
      
      try {
        // Try to find existing customer
        const existingCustomer = await apolloClient.query({
          query: GET_CUSTOMER_BY_EMAIL,
          variables: { email: customerInfo.email },
          fetchPolicy: 'network-only'
        });

        if (existingCustomer.data.customerByEmail) {
          // Customer exists, use existing ID
          customerId = existingCustomer.data.customerByEmail.id;
          console.log('Using existing customer:', existingCustomer.data.customerByEmail);
        }
      } catch (err) {
        // Customer not found, will create new one
        console.log('Customer not found, creating new one');
      }

      // If customer doesn't exist, create new customer
      if (!customerId) {
        const CREATE_CUSTOMER = gql`
          mutation CreateCustomer($name: String!, $email: String!, $phoneNumber: String!, $address: String!) {
            createCustomer(name: $name, email: $email, phoneNumber: $phoneNumber, address: $address) {
              id
              name
              email
            }
          }
        `;

        const customerResult = await apolloClient.mutate({
          mutation: CREATE_CUSTOMER,
          variables: customerInfo
        });

        customerId = customerResult.data.createCustomer.id;
        console.log('New customer created:', customerResult.data.createCustomer);
      }

      // Step 2: Create order via REST (Orchestrator)
      const firstItem = cart[0];
      const orderResponse = await orderApi.createOrder(customerId, firstItem.id, firstItem.quantity);
      
      // Step 3: Process payment via SOAP
      const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
      const paymentResponse = await paymentSoapApi.processPayment(
        orderResponse.id, 
        total, 
        paymentMethod
      );
      
      alert(`Order created for ${customerInfo.name}!\nOrder ID: ${orderResponse.id}\nPayment processed via SOAP! Transaction: ${paymentResponse.transactionId}`);
      onCustomerCreated(customerId);
      onSuccess();
    } catch (err) {
      setError('Error: ' + err.message);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);

  return (
    <div className="checkout-page">
      <h2>Checkout</h2>

      <div className="checkout-container">
        <div className="order-summary">
          <h3>Order Summary</h3>
          {cart.map(item => (
            <div key={item.id} className="summary-item">
              <span>{item.name} x{item.quantity}</span>
              <span>${(item.price * item.quantity).toFixed(2)}</span>
            </div>
          ))}
          <div className="summary-total">
            <strong>Total: ${total.toFixed(2)}</strong>
          </div>
        </div>

        <div className="payment-form">
          <h3>Customer & Payment Details</h3>
          <form>
            <div className="form-group">
              <label>Full Name *</label>
              <input 
                type="text" 
                placeholder="Your Name" 
                value={customerInfo.name}
                onChange={(e) => setCustomerInfo({...customerInfo, name: e.target.value})}
                required
              />
            </div>

            <div className="form-group">
              <label>Email *</label>
              <input 
                type="email" 
                placeholder="your@email.com" 
                value={customerInfo.email}
                onChange={(e) => setCustomerInfo({...customerInfo, email: e.target.value})}
                required
              />
            </div>

            <div className="form-group">
              <label>Phone Number</label>
              <input 
                type="tel" 
                placeholder="123-456-7890" 
                value={customerInfo.phoneNumber}
                onChange={(e) => setCustomerInfo({...customerInfo, phoneNumber: e.target.value})}
              />
            </div>

            <div className="form-group">
              <label>Delivery Address *</label>
              <input 
                type="text" 
                placeholder="123 Main Street, City" 
                value={customerInfo.address}
                onChange={(e) => setCustomerInfo({...customerInfo, address: e.target.value})}
                required
              />
            </div>

            <div className="form-group">
              <label>Payment Method</label>
              <select value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)}>
                <option value="CREDIT_CARD">Credit Card</option>
                <option value="DEBIT_CARD">Debit Card</option>
                <option value="PAYPAL">PayPal</option>
              </select>
            </div>

            <div className="form-group">
              <label>Card Number</label>
              <input type="text" placeholder="1234 5678 9012 3456" />
            </div>

            {error && <div className="error-message">{error}</div>}

            <button 
              type="button"
              onClick={handleCheckout}
              disabled={loading}
              className="pay-btn"
            >
              {loading ? 'Processing...' : 'Complete Payment'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default CheckoutPage;
