import React, { useState } from 'react';

function TrackingPage() {
  const [orderId, setOrderId] = useState('');
  const [trackingStatus, setTrackingStatus] = useState(null);

  const handleTrack = () => {
    // Mock tracking data for demo
    const statuses = ['PENDING', 'CONFIRMED', 'EN_PREPARATION', 'SHIPPED', 'DELIVERED'];
    const randomStatus = statuses[Math.floor(Math.random() * statuses.length)];
    
    setTrackingStatus({
      orderId,
      status: randomStatus,
      estimatedDelivery: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000).toLocaleDateString(),
      lastUpdate: new Date().toLocaleString()
    });
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
      <h2>Track Your Order</h2>

      <div className="tracking-input">
        <input
          type="text"
          placeholder="Enter Order ID"
          value={orderId}
          onChange={(e) => setOrderId(e.target.value)}
        />
        <button onClick={handleTrack} disabled={!orderId}>
          Track Order
        </button>
      </div>

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
