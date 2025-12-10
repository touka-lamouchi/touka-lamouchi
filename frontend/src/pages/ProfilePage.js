import React from 'react';
import { useQuery, gql } from '@apollo/client';

const GET_CUSTOMER_ORDERS = gql`
  query GetCustomer($id: ID!) {
    customer(id: $id) {
      id
      name
      email
      phoneNumber
      address
      orders {
        id
        customerId
        productId
        quantity
        totalAmount
        status
        createdAt
      }
    }
  }
`;

function ProfilePage({ customerId }) {
  const { loading, error, data } = useQuery(GET_CUSTOMER_ORDERS, {
    variables: { id: customerId.toString() },
  });

  const customer = data?.customer;
  const orders = customer?.orders || [];

  return (
    <div className="profile-page">
      <h2>My Profile & Orders</h2>

      <div className="profile-info">
        <h3>Customer: {customer?.name || `Customer #${customerId}`}</h3>
        <p>Email: {customer?.email || `customer${customerId}@example.com`}</p>
        <p>Phone: {customer?.phoneNumber || 'N/A'}</p>
        <p>Address: {customer?.address || 'N/A'}</p>
      </div>

      <div className="orders-section">
        <h3>Order History (via GraphQL)</h3>
        
        {loading && <p>Loading orders...</p>}
        {error && <p className="error">Error: {error.message}</p>}

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
                  <td>{order.createdAt ? new Date(order.createdAt).toLocaleDateString() : 'N/A'}</td>
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
