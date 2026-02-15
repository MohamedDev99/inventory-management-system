-- =========================================
-- V5: Create Inventory Items Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Added version and audit fields for VersionedEntity)
-- Description: Current stock levels per product per warehouse with optimistic locking
-- =========================================

-- Create INVENTORY_ITEMS table
CREATE TABLE inventory_items (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    location_code VARCHAR(50),
    last_stock_check TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

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

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_inventory_items_updated_at
    BEFORE UPDATE ON inventory_items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE inventory_items IS 'Current stock levels per product per warehouse';

COMMENT ON COLUMN inventory_items.location_code IS 'Rack/shelf location (e.g., A-12-3)';

COMMENT ON COLUMN inventory_items.version IS 'Optimistic lock version - CRITICAL for preventing overselling and stock corruption';

COMMENT ON COLUMN inventory_items.created_by IS 'Username of the user who created this inventory record';

COMMENT ON COLUMN inventory_items.updated_by IS 'Username of the user who last updated this inventory record';

COMMENT ON CONSTRAINT uk_inventory_product_warehouse ON inventory_items
    IS 'One record per product per warehouse';