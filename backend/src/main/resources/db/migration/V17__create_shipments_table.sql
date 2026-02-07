-- V11: Create shipments table
CREATE TABLE shipments (
    id BIGSERIAL PRIMARY KEY,
    shipment_number VARCHAR(50) NOT NULL UNIQUE,
    sales_order_id BIGINT NOT NULL REFERENCES sales_orders(id) ON DELETE RESTRICT,
    carrier VARCHAR(100) NOT NULL,
    tracking_number VARCHAR(100),
    shipping_method VARCHAR(50) NOT NULL CHECK (shipping_method IN ('STANDARD', 'EXPRESS', 'OVERNIGHT', 'GROUND')),
    estimated_delivery_date DATE,
    actual_delivery_date DATE,
    shipping_cost DECIMAL(12,2) NOT NULL DEFAULT 0,
    weight DECIMAL(10,2),
    dimensions VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'IN_TRANSIT', 'DELIVERED', 'RETURNED', 'FAILED')),
    shipped_from_warehouse_id BIGINT NOT NULL REFERENCES warehouses(id) ON DELETE RESTRICT,
    shipped_by BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_shipment_sales_order ON shipments(sales_order_id);
CREATE INDEX idx_shipment_tracking ON shipments(tracking_number);
CREATE INDEX idx_shipment_status ON shipments(status);
CREATE INDEX idx_shipment_estimated_delivery ON shipments(estimated_delivery_date);