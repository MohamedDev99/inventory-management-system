-- =========================================
-- V13: Create Inventory Movements Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Added version field for VersionedEntity)
-- Description: Track all inventory movements between warehouses and during transactions
-- =========================================

-- Create INVENTORY_MOVEMENTS table
CREATE TABLE inventory_movements (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    from_warehouse_id BIGINT,
    to_warehouse_id BIGINT,
    quantity INT NOT NULL CHECK (quantity > 0),
    movement_type VARCHAR(20) NOT NULL CHECK (movement_type IN ('TRANSFER', 'ADJUSTMENT', 'RECEIPT', 'SHIPMENT')),
    reason TEXT,
    reference_number VARCHAR(50),
    performed_by BIGINT NOT NULL,
    movement_date TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_im_product FOREIGN KEY (product_id)
        REFERENCES products(id) ON DELETE RESTRICT,
    CONSTRAINT fk_im_from_warehouse FOREIGN KEY (from_warehouse_id)
        REFERENCES warehouses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_im_to_warehouse FOREIGN KEY (to_warehouse_id)
        REFERENCES warehouses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_im_performed_by FOREIGN KEY (performed_by)
        REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_warehouse_movement CHECK (
        from_warehouse_id IS NOT NULL OR to_warehouse_id IS NOT NULL
    )
);

-- Indexes
CREATE INDEX idx_im_product ON inventory_movements(product_id);

CREATE INDEX idx_im_from_warehouse ON inventory_movements(from_warehouse_id);

CREATE INDEX idx_im_to_warehouse ON inventory_movements(to_warehouse_id);

CREATE INDEX idx_im_movement_date ON inventory_movements(movement_date DESC);

CREATE INDEX idx_im_movement_type ON inventory_movements(movement_type);

CREATE INDEX idx_im_reference ON inventory_movements(reference_number) WHERE reference_number IS NOT NULL;

CREATE INDEX idx_im_performed_by ON inventory_movements(performed_by);

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_inventory_movements_updated_at
    BEFORE UPDATE ON inventory_movements
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE inventory_movements IS 'Complete history of all inventory movements';

COMMENT ON COLUMN inventory_movements.movement_type IS 'Type: TRANSFER (between warehouses), RECEIPT (from supplier), SHIPMENT (to customer), ADJUSTMENT (manual)';

COMMENT ON COLUMN inventory_movements.from_warehouse_id IS 'Source warehouse (NULL for receipts from suppliers)';

COMMENT ON COLUMN inventory_movements.to_warehouse_id IS 'Destination warehouse (NULL for shipments to customers)';

COMMENT ON COLUMN inventory_movements.reference_number IS 'Related document reference (PO number, SO number, etc.)';

COMMENT ON COLUMN inventory_movements.version IS 'Optimistic lock version for audit trail integrity and movement corrections';

COMMENT ON COLUMN inventory_movements.created_by IS 'Username of the user who created this movement record';

COMMENT ON COLUMN inventory_movements.updated_by IS 'Username of the user who last updated this movement record';

COMMENT ON CONSTRAINT chk_warehouse_movement ON inventory_movements IS 'At least one warehouse (from or to) must be specified';