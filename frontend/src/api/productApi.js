import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

export const productApi = {
  getAll: async () => {
    const response = await axios.get(`${API_BASE}/products`);
    return response.data;
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
