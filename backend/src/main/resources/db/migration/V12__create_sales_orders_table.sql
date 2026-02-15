-- =========================================
-- V12: Create Sales Orders Tables
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Added version fields for VersionedEntity)
-- Description: Sales orders and line items with customer information and optimistic locking
-- =========================================

-- Create SALES_ORDERS table
CREATE TABLE sales_orders (
    id BIGSERIAL PRIMARY KEY,
    so_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50),
    shipping_address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    warehouse_id BIGINT NOT NULL,
    created_by_user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'FULFILLED', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    order_date DATE NOT NULL,
    fulfillment_date DATE,
    shipping_date DATE,
    delivery_date DATE,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    shipping_cost DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    notes TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_so_customer FOREIGN KEY (customer_id)
        REFERENCES customers(id) ON DELETE RESTRICT,
    CONSTRAINT fk_so_warehouse FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_so_created_by FOREIGN KEY (created_by_user_id)
        REFERENCES users(id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_so_customer ON sales_orders(customer_id);

CREATE INDEX idx_so_warehouse ON sales_orders(warehouse_id);

CREATE INDEX idx_so_status ON sales_orders(status);

CREATE INDEX idx_so_order_date ON sales_orders(order_date DESC);

CREATE INDEX idx_so_status_date ON sales_orders(status, order_date DESC);

CREATE INDEX idx_so_customer_date ON sales_orders(customer_id, order_date DESC);

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_sales_orders_updated_at
    BEFORE UPDATE ON sales_orders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE sales_orders IS 'Sales order headers with customer and shipping information';

COMMENT ON COLUMN sales_orders.so_number IS 'Unique SO number (format: SO-YYYYMMDD-XXXX)';

COMMENT ON COLUMN sales_orders.customer_name IS 'Denormalized customer name for historical accuracy';

COMMENT ON COLUMN sales_orders.customer_email IS 'Denormalized customer email for historical accuracy';

COMMENT ON COLUMN sales_orders.customer_phone IS 'Denormalized customer phone for historical accuracy';

COMMENT ON COLUMN sales_orders.status IS 'Order status: PENDING, CONFIRMED, FULFILLED, SHIPPED, DELIVERED, CANCELLED';

COMMENT ON COLUMN sales_orders.version IS 'Optimistic lock version - CRITICAL for financial data and preventing double fulfillment';

COMMENT ON COLUMN sales_orders.created_by IS 'Username of the user who created this sales order';

COMMENT ON COLUMN sales_orders.updated_by IS 'Username of the user who last updated this sales order';

-- Create SALES_ORDER_ITEMS table
CREATE TABLE sales_order_items (
    id BIGSERIAL PRIMARY KEY,
    sales_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(12,2) NOT NULL,
    line_total DECIMAL(12,2) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_soi_sales_order FOREIGN KEY (sales_order_id)
        REFERENCES sales_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_soi_product FOREIGN KEY (product_id)
        REFERENCES products(id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_soi_sales_order ON sales_order_items(sales_order_id);

CREATE INDEX idx_soi_product ON sales_order_items(product_id);

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_sales_order_items_updated_at
    BEFORE UPDATE ON sales_order_items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE sales_order_items IS 'Line items for sales orders';

COMMENT ON COLUMN sales_order_items.unit_price IS 'Selling price at time of order (denormalized for historical accuracy)';

COMMENT ON COLUMN sales_order_items.line_total IS 'Line total = quantity × unit_price';

COMMENT ON COLUMN sales_order_items.version IS 'Optimistic lock version - CRITICAL for preventing shipping quantity corruption';

COMMENT ON COLUMN sales_order_items.created_by IS 'Username of the user who created this line item';

COMMENT ON COLUMN sales_order_items.updated_by IS 'Username of the user who last updated this line item';