-- V10: Create stock_adjustments table
CREATE TABLE stock_adjustments (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    warehouse_id BIGINT NOT NULL REFERENCES warehouses(id) ON DELETE RESTRICT,
    quantity_before INT NOT NULL,
    quantity_after INT NOT NULL,
    quantity_change INT NOT NULL,
    adjustment_type VARCHAR(20) NOT NULL CHECK (adjustment_type IN ('ADD', 'REMOVE', 'CORRECTION')),
    reason VARCHAR(20) NOT NULL CHECK (reason IN ('DAMAGED', 'EXPIRED', 'THEFT', 'COUNT_ERROR', 'RETURN', 'OTHER')),
    performed_by BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    approved_by BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    notes TEXT,
    adjustment_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_sa_product ON stock_adjustments(product_id);
CREATE INDEX idx_sa_warehouse ON stock_adjustments(warehouse_id);
CREATE INDEX idx_sa_status ON stock_adjustments(status);
CREATE INDEX idx_sa_adjustment_date ON stock_adjustments(adjustment_date DESC);
CREATE INDEX idx_sa_pending ON stock_adjustments(status, adjustment_date DESC) WHERE status = 'PENDING';