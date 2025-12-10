import React, { useState, useEffect } from 'react';
import { productApi } from '../api/productApi';

function HomePage({ onAddToCart }) {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [quantities, setQuantities] = useState({});

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      setError(null);
      console.log('Fetching products from API...');
      const data = await productApi.getAll();
      console.log('Products received:', data);
      setProducts(data);
      // Initialize quantities
      const initialQuantities = {};
      data.forEach(product => {
        initialQuantities[product.id] = 1;
      });
      setQuantities(initialQuantities);
    } catch (error) {
      console.error('Error fetching products:', error);
      setError(error.message || 'Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async (product) => {
    const quantity = quantities[product.id] || 1;
    
    try {
      // Step 2: Check stock via REST API
      const stockCheck = await productApi.checkStock(product.id, quantity);
      
      if (stockCheck) {
        onAddToCart(product, quantity);
        alert(`${product.name} added to cart!`);
      } else {
        alert(`Sorry, insufficient stock for ${product.name}`);
      }
    } catch (error) {
      console.error('Stock check failed:', error);
      alert('Failed to check stock. Please try again.');
    }
  };

  const handleQuantityChange = (productId, qty) => {
    setQuantities({ ...quantities, [productId]: Math.max(1, qty) });
  };

  const categories = ['all', ...new Set(products.map(p => p.category))];
  const filteredProducts = selectedCategory === 'all' 
    ? products 
    : products.filter(p => p.category === selectedCategory);

  if (loading) return <div className="loading">Loading products...</div>;
  
  if (error) return (
    <div className="error">
      <h3>Error loading products</h3>
      <p>{error}</p>
      <button onClick={fetchProducts}>Retry</button>
    </div>
  );

  if (products.length === 0) return (
    <div className="no-products">
      <h3>No products available</h3>
      <button onClick={fetchProducts}>Refresh</button>
    </div>
  );

  return (
    <div className="home-page">
      <h2>Our Products</h2>

      <div className="filters">
        <label>Category:</label>
        <select value={selectedCategory} onChange={(e) => setSelectedCategory(e.target.value)}>
          {categories.map(cat => (
            <option key={cat} value={cat}>
              {cat.charAt(0).toUpperCase() + cat.slice(1)}
            </option>
          ))}
        </select>
      </div>

      <div className="products-grid">
        {filteredProducts.map(product => (
          <div key={product.id} className="product-card">
            <h3>{product.name}</h3>
            <p className="category">{product.category}</p>
            <p className="description">{product.description}</p>
            <p className="price">${product.price}</p>
            <p className="stock">Stock: {product.stock}</p>
            
            <div className="quantity-selector">
              <label>Qty:</label>
              <input 
                type="number" 
                min="1" 
                value={quantities[product.id] || 1}
                onChange={(e) => handleQuantityChange(product.id, parseInt(e.target.value))}
              />
            </div>

            <button 
              onClick={() => handleAddToCart(product)}
              disabled={product.stock === 0}
              className="add-to-cart-btn"
            >
              {product.stock === 0 ? 'Out of Stock' : 'Add to Cart'}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default HomePage;
