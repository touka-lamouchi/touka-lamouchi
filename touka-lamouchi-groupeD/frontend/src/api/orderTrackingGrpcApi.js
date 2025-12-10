import axios from 'axios';

// Simplified gRPC-Web client for order tracking
// Note: True gRPC-Web requires envoy proxy at http://localhost:8080/grpc
export const orderTrackingGrpcApi = {
  // Stream order status updates
  streamOrderStatus: async (orderId, onUpdate, onError, onComplete) => {
    try {
      // For browser compatibility, we'll use REST fallback
      // True gRPC-Web streaming requires envoy proxy or similar
      // This is a polling-based approach that simulates streaming
      
      const intervalId = setInterval(async () => {
        try {
          const response = await axios.get(`http://localhost:8080/api/orders/${orderId}`, {
            withCredentials: true
          });
          
          if (response.data && response.data.id) {
            onUpdate({
              status: response.data.status || 'PENDING',
              message: `Order ${response.data.status || 'PENDING'}`,
              timestamp: new Date().toISOString()
            });
          }
        } catch (err) {
          console.error('Polling error:', err);
          if (onError) onError(err);
        }
      }, 3000); // Poll every 3 seconds

      // Return cleanup function
      return () => {
        clearInterval(intervalId);
        if (onComplete) onComplete();
      };
    } catch (error) {
      console.error('gRPC-Web streaming error:', error);
      if (onError) onError(error);
      throw error;
    }
  },

  // Get single order status (non-streaming)
  getOrderStatus: async (orderId) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/orders/${orderId}`, {
        withCredentials: true
      });
      
      return {
        orderId: response.data.id,
        status: response.data.status || 'PENDING',
        message: `Order status: ${response.data.status || 'PENDING'}`,
        timestamp: response.data.createdAt || new Date().toISOString()
      };
    } catch (error) {
      console.error('gRPC order status error:', error);
      throw error;
    }
  }
};
