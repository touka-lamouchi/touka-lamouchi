import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

export const paymentApi = {
  getAll: async () => {
    const response = await axios.get(`${API_BASE}/payments`);
    return response.data;
  },

  getById: async (id) => {
    const response = await axios.get(`${API_BASE}/payments/${id}`);
    return response.data;
  },

  getByOrderId: async (orderId) => {
    const response = await axios.get(`${API_BASE}/payments/order/${orderId}`);
    return response.data;
  },

  getByStatus: async (status) => {
    const response = await axios.get(`${API_BASE}/payments/status/${status}`);
    return response.data;
  }
};
