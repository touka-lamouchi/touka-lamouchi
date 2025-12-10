import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

// Configure axios defaults
axios.defaults.headers.common['Content-Type'] = 'application/json';

export const productApi = {
  getAll: async () => {
    try {
      console.log('Calling API:', `${API_BASE}/products`);
      const response = await axios.get(`${API_BASE}/products`, {
        timeout: 10000,
        withCredentials: true
      });
      console.log('API Response:', response.data);
      return response.data;
    } catch (error) {
      console.error('API Error:', error.response || error);
      throw error;
    }
  },

  getById: async (id) => {
    const response = await axios.get(`${API_BASE}/products/${id}`);
    return response.data;
  },

  searchByCategory: async (category) => {
    const response = await axios.get(`${API_BASE}/products/category/${category}`);
    return response.data;
  },

  checkStock: async (id, quantity) => {
    const response = await axios.get(`${API_BASE}/products/${id}/check-stock`, {
      params: { quantity }
    });
    return response.data;
  }
};
