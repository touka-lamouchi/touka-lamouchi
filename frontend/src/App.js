import React, { useState } from 'react';
import './App.css';
import HomePage from './pages/HomePage';
import CartPage from './pages/CartPage';
import CheckoutPage from './pages/CheckoutPage';
import ProfilePage from './pages/ProfilePage';
import TrackingPage from './pages/TrackingPage';

function App() {
  const [currentPage, setCurrentPage] = useState('home');
  const [cart, setCart] = useState([]);
  const [customerId, setCustomerId] = useState(1);

  const addToCart = (product, quantity) => {
    const existingItem = cart.find(item => item.id === product.id);
    if (existingItem) {
      setCart(cart.map(item =>
        item.id === product.id
          ? { ...item, quantity: item.quantity + quantity }
          : item
      ));
    } else {
      setCart([...cart, { ...product, quantity }]);
    }
  };

  const removeFromCart = (productId) => {
    setCart(cart.filter(item => item.id !== productId));
  };

  const renderPage = () => {
    switch (currentPage) {
      case 'home':
        return <HomePage onAddToCart={addToCart} />;
      case 'cart':
        return <CartPage cart={cart} onRemove={removeFromCart} onCheckout={() => setCurrentPage('checkout')} />;
      case 'checkout':
        return <CheckoutPage cart={cart} customerId={customerId} onSuccess={() => setCurrentPage('profile')} />;
      case 'profile':
        return <ProfilePage customerId={customerId} />;
      case 'tracking':
        return <TrackingPage />;
      default:
        return <HomePage onAddToCart={addToCart} />;
    }
  };

  return (
    <div className="App">
      <nav className="navbar">
        <h1>ShopHub - Fitness Supplements</h1>
        <ul>
          <li><button onClick={() => setCurrentPage('home')}>Home</button></li>
          <li><button onClick={() => setCurrentPage('cart')}>Cart ({cart.length})</button></li>
          <li><button onClick={() => setCurrentPage('profile')}>Profile</button></li>
          <li><button onClick={() => setCurrentPage('tracking')}>Track Order</button></li>
        </ul>
      </nav>

      <div className="content">
        {renderPage()}
      </div>

      <footer className="footer">
        <p>&copy; 2025 ShopHub. All rights reserved.</p>
      </footer>
    </div>
  );
}

export default App;
