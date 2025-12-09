import React, { useState, useEffect } from 'react';
import { orderApi } from '../api/orderApi';

function ProfilePage({ customerId }) {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchOrders();
  }, [customerId]);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await orderApi.getOrdersByCustomer(customerId);
      setOrders(Array.isArray(data) ? data : []);
    } catch (err) {
      setError('Error fetching orders: ' + err.message);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="profile-page">
      <h2>My Profile & Orders</h2>

      <div className="profile-info">
        <h3>Customer ID: {customerId}</h3>
        <p>Email: customer{customerId}@example.com</p>
        <p>Member since: 2025</p>
      </div>

      <div className="orders-section">
        <h3>Order History</h3>
        
        {loading && <p>Loading orders...</p>}
        {error && <p className="error">{error}</p>}

        {orders.length === 0 && !loading && (
          <p>No orders found</p>
        )}

        {orders.length > 0 && (
          <table className="orders-table">
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Product</th>
                <th>Quantity</th>
                <th>Total</th>
                <th>Status</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {orders.map(order => (
                <tr key={order.id}>
                  <td>#{order.id}</td>
                  <td>Product {order.productId}</td>
                  <td>{order.quantity}</td>
                  <td>${order.totalAmount?.toFixed(2) || '0.00'}</td>
                  <td>
                    <span className={`status ${order.status?.toLowerCase()}`}>
                      {order.status || 'PENDING'}
                    </span>
                  </td>
                  <td>{order.orderDate ? new Date(order.orderDate).toLocaleDateString() : 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

export default ProfilePage;
