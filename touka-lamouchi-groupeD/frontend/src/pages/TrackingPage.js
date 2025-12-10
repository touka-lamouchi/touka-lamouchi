import React, { useState, useEffect } from 'react';
import { orderTrackingGrpcApi } from '../api/orderTrackingGrpcApi';

function TrackingPage() {
  const [orderId, setOrderId] = useState('');
  const [trackingStatus, setTrackingStatus] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [isStreaming, setIsStreaming] = useState(false);
  const [streamCleanup, setStreamCleanup] = useState(null);

  useEffect(() => {
    // Cleanup stream on unmount
    return () => {
      if (streamCleanup) {
        streamCleanup();
      }
    };
  }, [streamCleanup]);

  const handleTrack = async () => {
    setLoading(true);
    setError(null);
    setTrackingStatus(null);
    
    try {
      // Get initial status via gRPC-Web
      const status = await orderTrackingGrpcApi.getOrderStatus(orderId);
      
      setTrackingStatus({
        orderId: status.orderId,
        status: status.status,
        estimatedDelivery: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000).toLocaleDateString(),
        lastUpdate: new Date().toLocaleString()
      });
      
      // Start streaming updates
      setIsStreaming(true);
      const cleanup = await orderTrackingGrpcApi.streamOrderStatus(
        orderId,
        (update) => {
          console.log('gRPC stream update:', update);
          setTrackingStatus(prev => ({
            ...prev,
            status: update.status,
            lastUpdate: new Date().toLocaleString()
          }));
        },
        (err) => {
          console.error('gRPC stream error:', err);
          setIsStreaming(false);
        },
        () => {
          console.log('gRPC stream completed');
          setIsStreaming(false);
        }
      );
      
      setStreamCleanup(() => cleanup);
    } catch (err) {
      console.error('Tracking error:', err);
      if (err.response && err.response.status === 404) {
        setError(`Order #${orderId} not found via gRPC.`);
      } else {
        setError('Could not fetch order status via gRPC-Web.');
      }
    } finally {
      setLoading(false);
    }
  };

  const stopStreaming = () => {
    if (streamCleanup) {
      streamCleanup();
      setStreamCleanup(null);
      setIsStreaming(false);
    }
  };

  const getStatusSteps = () => {
    const steps = [
      { status: 'PENDING', label: 'Order Placed', icon: '📦' },
      { status: 'CONFIRMED', label: 'Order Confirmed', icon: '✅' },
      { status: 'EN_PREPARATION', label: 'Preparing', icon: '🔄' },
      { status: 'SHIPPED', label: 'Shipped', icon: '🚚' },
      { status: 'DELIVERED', label: 'Delivered', icon: '✨' }
    ];

    return steps;
  };

  return (
    <div className="tracking-page">
      <h2>Track Your Order (via gRPC-Web)</h2>

      <div className="tracking-input">
        <input
          type="text"
          placeholder="Enter Order ID"
          value={orderId}
          onChange={(e) => setOrderId(e.target.value)}
        />
        <button onClick={handleTrack} disabled={!orderId || loading || isStreaming}>
          {loading ? 'Tracking...' : 'Track Order'}
        </button>
        {isStreaming && (
          <button onClick={stopStreaming} style={{marginLeft: '10px', backgroundColor: '#dc3545'}}>
            Stop Streaming
          </button>
        )}
        {isStreaming && <span style={{marginLeft: '10px', color: 'green'}}>🔴 Live streaming...</span>}
      </div>

      {error && (
        <div className="error-message" style={{color: 'red', margin: '10px 0'}}>
          {error}
        </div>
      )}

      {trackingStatus && (
        <div className="tracking-result">
          <h3>Order #{trackingStatus.orderId}</h3>

          <div className="status-timeline">
            {getStatusSteps().map((step, index) => (
              <div 
                key={step.status}
                className={`timeline-item ${step.status === trackingStatus.status ? 'active' : ''}`}
              >
                <div className="timeline-marker">{step.icon}</div>
                <div className="timeline-label">{step.label}</div>
              </div>
            ))}
          </div>

          <div className="tracking-details">
            <p><strong>Current Status:</strong> {trackingStatus.status}</p>
            <p><strong>Estimated Delivery:</strong> {trackingStatus.estimatedDelivery}</p>
            <p><strong>Last Update:</strong> {trackingStatus.lastUpdate}</p>
          </div>
        </div>
      )}
    </div>
  );
}

export default TrackingPage;
