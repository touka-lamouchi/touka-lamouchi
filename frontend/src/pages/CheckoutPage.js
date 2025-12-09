import React, { useState } from 'react';
import { orderApi } from '../api/orderApi';

function CheckoutPage({ cart, customerId, onSuccess }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleCheckout = async () => {
    try {
      setLoading(true);
      setError(null);

      // For simplicity, create one order for the first product
      // In production, you'd handle this differently
      if (cart.length === 0) {
        setError('Cart is empty');
        return;
      }

      const firstItem = cart[0];
      const response = await orderApi.createOrder(customerId, firstItem.id, firstItem.quantity);
      
      alert(`Order created successfully! Order ID: ${response.id}`);
      onSuccess();
    } catch (err) {
      setError('Error creating order: ' + err.message);
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
          <h3>Payment Details</h3>
          <form>
            <div className="form-group">
              <label>Full Name</label>
              <input type="text" placeholder="John Doe" />
            </div>

            <div className="form-group">
              <label>Email</label>
              <input type="email" placeholder="john@example.com" />
            </div>

            <div className="form-group">
              <label>Address</label>
              <input type="text" placeholder="123 Main Street" />
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
