-- Connect to shophub_products database and populate with sample products
\c shophub_products;

-- Insert products (columns: name, description, price, stock, category, image_url)
INSERT INTO products (name, description, price, stock, category, image_url) VALUES
('Laptop Pro 15', 'High-performance laptop with 16GB RAM and 512GB SSD', 1299.99, 50, 'Electronics', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400'),
('Wireless Mouse', 'Ergonomic wireless mouse with precision tracking', 29.99, 200, 'Accessories', 'https://images.unsplash.com/photo-1527814050087-3793815479db?w=400'),
('Mechanical Keyboard', 'RGB backlit mechanical keyboard with blue switches', 89.99, 100, 'Accessories', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400'),
('USB-C Hub', '7-in-1 USB-C hub with HDMI, USB 3.0, and SD card reader', 49.99, 150, 'Accessories', 'https://images.unsplash.com/photo-1625948515291-69613efd103f?w=400'),
('Webcam HD', '1080p HD webcam with built-in microphone', 79.99, 75, 'Electronics', 'https://images.unsplash.com/photo-1585152968232-5d3a2f6b0c18?w=400'),
('Noise Cancelling Headphones', 'Premium over-ear headphones with active noise cancellation', 249.99, 60, 'Audio', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400'),
('Portable SSD 1TB', 'Ultra-fast portable SSD with USB 3.2 Gen 2', 149.99, 80, 'Storage', 'https://images.unsplash.com/photo-1531492746076-161ca9bcad58?w=400'),
('Monitor 27"', '4K UHD monitor with HDR support', 399.99, 40, 'Electronics', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400'),
('Desk Lamp LED', 'Adjustable LED desk lamp with touch control', 39.99, 120, 'Office', 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400'),
('Gaming Chair', 'Ergonomic gaming chair with lumbar support', 299.99, 30, 'Furniture', 'https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=400'),
('Smartphone Stand', 'Adjustable aluminum smartphone stand', 19.99, 250, 'Accessories', 'https://images.unsplash.com/photo-1601784551446-20c9e07cdbdb?w=400'),
('Wireless Charger', 'Fast wireless charging pad for smartphones', 34.99, 180, 'Accessories', 'https://images.unsplash.com/photo-1591290619762-c588e7f91321?w=400'),
('Bluetooth Speaker', 'Portable waterproof Bluetooth speaker', 59.99, 90, 'Audio', 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400'),
('Tablet 10"', '10-inch tablet with 128GB storage', 399.99, 45, 'Electronics', 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400'),
('Backpack Laptop', 'Water-resistant laptop backpack with USB charging port', 69.99, 110, 'Accessories', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400');

-- Connect to shophub_customers database and populate with sample customers
\c shophub_customers;

-- Insert customers (columns: name, email, phone_number, address, created_at)
INSERT INTO customers (name, email, phone_number, address, created_at) VALUES
('John Doe', 'john.doe@email.com', '+1-555-0101', '123 Main St, New York, USA 10001', CURRENT_TIMESTAMP),
('Jane Smith', 'jane.smith@email.com', '+1-555-0102', '456 Oak Ave, Los Angeles, USA 90001', CURRENT_TIMESTAMP),
('Michael Johnson', 'michael.j@email.com', '+1-555-0103', '789 Pine Rd, Chicago, USA 60601', CURRENT_TIMESTAMP),
('Emily Davis', 'emily.davis@email.com', '+1-555-0104', '321 Elm St, Houston, USA 77001', CURRENT_TIMESTAMP),
('David Wilson', 'david.w@email.com', '+1-555-0105', '654 Maple Dr, Phoenix, USA 85001', CURRENT_TIMESTAMP),
('Sarah Brown', 'sarah.brown@email.com', '+1-555-0106', '987 Cedar Ln, Philadelphia, USA 19101', CURRENT_TIMESTAMP),
('James Taylor', 'james.taylor@email.com', '+1-555-0107', '147 Birch Way, San Antonio, USA 78201', CURRENT_TIMESTAMP),
('Lisa Anderson', 'lisa.a@email.com', '+1-555-0108', '258 Spruce Ct, San Diego, USA 92101', CURRENT_TIMESTAMP),
('Robert Thomas', 'robert.thomas@email.com', '+1-555-0109', '369 Willow Ave, Dallas, USA 75201', CURRENT_TIMESTAMP),
('Maria Martinez', 'maria.m@email.com', '+1-555-0110', '741 Ash Blvd, San Jose, USA 95101', CURRENT_TIMESTAMP);

-- Connect to shophub_orders database
\c shophub_orders;

-- Insert orders (columns: customer_id, product_id, quantity, status, total_amount, order_date)
INSERT INTO orders (customer_id, product_id, quantity, status, total_amount, order_date) VALUES
(1, 1, 1, 'DELIVERED', 1329.98, CURRENT_TIMESTAMP - INTERVAL '5 days'),
(2, 6, 1, 'SHIPPED', 249.99, CURRENT_TIMESTAMP - INTERVAL '3 days'),
(3, 8, 1, 'PROCESSING', 539.97, CURRENT_TIMESTAMP - INTERVAL '2 days'),
(4, 7, 1, 'DELIVERED', 149.99, CURRENT_TIMESTAMP - INTERVAL '7 days'),
(5, 3, 1, 'PENDING', 89.99, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(1, 2, 3, 'DELIVERED', 89.97, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(6, 5, 1, 'SHIPPED', 79.99, CURRENT_TIMESTAMP - INTERVAL '4 days'),
(7, 11, 2, 'PROCESSING', 39.98, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(8, 14, 1, 'DELIVERED', 449.98, CURRENT_TIMESTAMP - INTERVAL '6 days'),
(9, 10, 1, 'PENDING', 299.99, CURRENT_TIMESTAMP);

-- Insert order_items (order_id, product_id, product_name, quantity, unit_price, subtotal)
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal) VALUES
-- Order 1
(1, 1, 'Laptop Pro 15', 1, 1299.99, 1299.99),
(1, 2, 'Wireless Mouse', 1, 29.99, 29.99),
-- Order 2
(2, 6, 'Noise Cancelling Headphones', 1, 249.99, 249.99),
-- Order 3
(3, 8, 'Monitor 27"', 1, 399.99, 399.99),
(3, 3, 'Mechanical Keyboard', 1, 89.99, 89.99),
(3, 4, 'USB-C Hub', 1, 49.99, 49.99),
-- Order 4
(4, 7, 'Portable SSD 1TB', 1, 149.99, 149.99),
-- Order 5
(5, 3, 'Mechanical Keyboard', 1, 89.99, 89.99),
-- Order 6
(6, 5, 'Webcam HD', 1, 79.99, 79.99),
-- Order 7
(7, 11, 'Smartphone Stand', 2, 19.99, 39.98),
-- Order 8
(8, 14, 'Tablet 10"', 1, 399.99, 399.99),
(8, 4, 'USB-C Hub', 1, 49.99, 49.99),
-- Order 9
(9, 10, 'Gaming Chair', 1, 299.99, 299.99),
-- Order 10
(10, 15, 'Backpack Laptop', 2, 69.99, 139.98);

-- Connect to shophub_payments database
\c shophub_payments;

-- Insert payments (columns: order_id, payment_method, status, amount, transaction_id, created_at, updated_at)
INSERT INTO payments (order_id, payment_method, status, amount, transaction_id, created_at, updated_at) VALUES
(1, 'CREDIT_CARD', 'COMPLETED', 1329.98, 'TXN-001-' || TO_CHAR(NOW(), 'YYYYMMDD'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'PAYPAL', 'COMPLETED', 249.99, 'TXN-002-' || TO_CHAR(NOW(), 'YYYYMMDD'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'CREDIT_CARD', 'PROCESSING', 539.97, 'TXN-003-' || TO_CHAR(NOW(), 'YYYYMMDD'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'DEBIT_CARD', 'COMPLETED', 149.99, 'TXN-004-' || TO_CHAR(NOW(), 'YYYYMMDD'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'CREDIT_CARD', 'PENDING', 89.99, 'TXN-005-' || TO_CHAR(NOW(), 'YYYYMMDD'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'PAYPAL', 'COMPLETED', 89.97, 'TXN-006-' || TO_CHAR(NOW(), 'YYYYMMDD'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'CREDIT_CARD', 'COMPLETED', 79.99, 'TXN-007-' || TO_CHAR(NOW(), 'YYYYMMDD'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'DEBIT_CARD', 'PROCESSING', 39.98, 'TXN-008-' || TO_CHAR(NOW(), 'YYYYMMDD'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'CREDIT_CARD', 'COMPLETED', 449.98, 'TXN-009-' || TO_CHAR(NOW(), 'YYYYMMDD'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'PAYPAL', 'PENDING', 299.99, 'TXN-010-' || TO_CHAR(NOW(), 'YYYYMMDD'), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
