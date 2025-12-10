import React from 'react';

function CartPage({ cart, onRemove, onCheckout }) {
  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);

  if (cart.length === 0) {
    return (
      <div className="cart-page">
        <h2>Shopping Cart</h2>
        <p>Your cart is empty</p>
      </div>
    );
  }

  return (
    <div className="cart-page">
      <h2>Shopping Cart</h2>

      <table className="cart-table">
        <thead>
          <tr>
            <th>Product</th>
            <th>Price</th>
            <th>Quantity</th>
            <th>Total</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {cart.map(item => (
            <tr key={item.id}>
              <td>{item.name}</td>
              <td>${item.price}</td>
              <td>{item.quantity}</td>
              <td>${(item.price * item.quantity).toFixed(2)}</td>
              <td>
                <button onClick={() => onRemove(item.id)} className="remove-btn">
                  Remove
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="cart-summary">
        <h3>Order Summary</h3>
        <p>Subtotal: ${total.toFixed(2)}</p>
        <p>Shipping: $0.00</p>
        <p className="total">Total: ${total.toFixed(2)}</p>

        <button onClick={onCheckout} className="checkout-btn">
          Proceed to Checkout
        </button>
      </div>
    </div>
  );
}

export default CartPage;
