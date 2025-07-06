-- 创建订单表
CREATE TABLE IF NOT EXISTS t_order (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL,
    total_currency VARCHAR(10) NOT NULL,
    shipping_province VARCHAR(50) NOT NULL,
    shipping_city VARCHAR(50) NOT NULL,
    shipping_district VARCHAR(50) NOT NULL,
    shipping_street VARCHAR(100) NOT NULL,
    shipping_detail VARCHAR(200),
    shipping_zip_code VARCHAR(20),
    recipient_name VARCHAR(50) NOT NULL,
    recipient_phone VARCHAR(20) NOT NULL,
    created_time TIMESTAMP NOT NULL,
    payment_time TIMESTAMP,
    shipping_time TIMESTAMP,
    completion_time TIMESTAMP,
    cancellation_time TIMESTAMP
);

-- 创建订单项表
CREATE TABLE IF NOT EXISTS t_order_item (
    id VARCHAR(50) PRIMARY KEY,
    order_id VARCHAR(50) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    product_image VARCHAR(200),
    unit_price_amount DECIMAL(19, 2) NOT NULL,
    unit_price_currency VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    subtotal_amount DECIMAL(19, 2) NOT NULL,
    subtotal_currency VARCHAR(10) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES t_order(id)
);