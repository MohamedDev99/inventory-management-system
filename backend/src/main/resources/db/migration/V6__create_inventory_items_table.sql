-- V6: Create inventory_items table
CREATE TABLE inventory_items (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    location_code VARCHAR(50),
    last_stock_check TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_inventory_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_inventory_product_warehouse
        UNIQUE (product_id, warehouse_id)
);

-- Indexes
CREATE INDEX idx_inventory_product ON inventory_items(product_id);
CREATE INDEX idx_inventory_warehouse ON inventory_items(warehouse_id);
CREATE INDEX idx_inventory_warehouse_quantity ON inventory_items(warehouse_id, quantity);

-- Comments
COMMENT ON TABLE inventory_items IS 'Current stock levels per product per warehouse';
COMMENT ON COLUMN inventory_items.location_code IS 'Rack/shelf location (e.g., A-12-3)';
COMMENT ON CONSTRAINT uk_inventory_product_warehouse ON inventory_items
    IS 'One record per product per warehouse';