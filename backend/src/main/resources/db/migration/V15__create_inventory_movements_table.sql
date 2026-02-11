-- V9: Create inventory_movements table
CREATE TABLE inventory_movements (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    from_warehouse_id BIGINT REFERENCES warehouses(id) ON DELETE RESTRICT,
    to_warehouse_id BIGINT REFERENCES warehouses(id) ON DELETE RESTRICT,
    quantity INT NOT NULL CHECK (quantity > 0),
    movement_type VARCHAR(20) NOT NULL CHECK (movement_type IN ('TRANSFER', 'ADJUSTMENT', 'RECEIPT', 'SHIPMENT')),
    reason TEXT,
    reference_number VARCHAR(50),
    performed_by BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    movement_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT chk_warehouse_movement CHECK (
        from_warehouse_id IS NOT NULL OR to_warehouse_id IS NOT NULL
    )
);

CREATE INDEX idx_im_product ON inventory_movements(product_id);
CREATE INDEX idx_im_from_warehouse ON inventory_movements(from_warehouse_id);
CREATE INDEX idx_im_to_warehouse ON inventory_movements(to_warehouse_id);
CREATE INDEX idx_im_movement_date ON inventory_movements(movement_date DESC);
CREATE INDEX idx_im_movement_type ON inventory_movements(movement_type);
CREATE INDEX idx_im_reference ON inventory_movements(reference_number) WHERE reference_number IS NOT NULL;