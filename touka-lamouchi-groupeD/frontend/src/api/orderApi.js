import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

export const orderApi = {
  createOrder: async (customerId, productId, quantity) => {
    const response = await axios.post(`${API_BASE}/orchestrator/create`, {
      customerId,
      productId,
      quantity
    });
    return response.data;
  },

  getOrdersByCustomer: async (customerId) => {
    const response = await axios.get(`${API_BASE}/orchestrator/customer/${customerId}`);
    return response.data;
  }
};
