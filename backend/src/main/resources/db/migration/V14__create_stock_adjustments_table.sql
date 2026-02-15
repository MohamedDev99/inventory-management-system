-- =========================================
-- V14: Create Stock Adjustments Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Added version field for VersionedEntity)
-- Description: Manual inventory adjustments with approval workflow and optimistic locking
-- =========================================

-- Create STOCK_ADJUSTMENTS table
CREATE TABLE stock_adjustments (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity_before INT NOT NULL CHECK (quantity_before >= 0),
    quantity_after INT NOT NULL CHECK (quantity_after >= 0),
    quantity_change INT NOT NULL,
    adjustment_type VARCHAR(20) NOT NULL CHECK (adjustment_type IN ('ADD', 'REMOVE', 'CORRECTION')),
    reason VARCHAR(20) NOT NULL CHECK (reason IN ('DAMAGED', 'EXPIRED', 'THEFT', 'COUNT_ERROR', 'RETURN', 'OTHER')),
    performed_by BIGINT NOT NULL,
    approved_by BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    notes TEXT,
    adjustment_date TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_sa_product FOREIGN KEY (product_id)
        REFERENCES products(id) ON DELETE RESTRICT,
    CONSTRAINT fk_sa_warehouse FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_sa_performed_by FOREIGN KEY (performed_by)
        REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_sa_approved_by FOREIGN KEY (approved_by)
        REFERENCES users(id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_sa_product ON stock_adjustments(product_id);

CREATE INDEX idx_sa_warehouse ON stock_adjustments(warehouse_id);

CREATE INDEX idx_sa_status ON stock_adjustments(status);

CREATE INDEX idx_sa_adjustment_date ON stock_adjustments(adjustment_date DESC);

CREATE INDEX idx_sa_pending ON stock_adjustments(status, adjustment_date DESC) WHERE status = 'PENDING';

CREATE INDEX idx_sa_performed_by ON stock_adjustments(performed_by);

CREATE INDEX idx_sa_approved_by ON stock_adjustments(approved_by);

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_stock_adjustments_updated_at
    BEFORE UPDATE ON stock_adjustments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE stock_adjustments IS 'Manual inventory adjustments with approval workflow';

COMMENT ON COLUMN stock_adjustments.adjustment_type IS 'Type: ADD (increase stock), REMOVE (decrease stock), CORRECTION (fix count error)';

COMMENT ON COLUMN stock_adjustments.reason IS 'Reason: DAMAGED, EXPIRED, THEFT, COUNT_ERROR, RETURN, OTHER';

COMMENT ON COLUMN stock_adjustments.status IS 'Approval status: PENDING (awaiting approval), APPROVED (applied to inventory), REJECTED (denied)';

COMMENT ON COLUMN stock_adjustments.quantity_change IS 'Calculated: quantity_after - quantity_before (positive for additions, negative for removals)';

COMMENT ON COLUMN stock_adjustments.version IS 'Optimistic lock version - CRITICAL for preventing conflicting approval decisions';

COMMENT ON COLUMN stock_adjustments.created_by IS 'Username of the user who created this adjustment request';

COMMENT ON COLUMN stock_adjustments.updated_by IS 'Username of the user who last updated this adjustment (typically the approver)';