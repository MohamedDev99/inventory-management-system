-- =========================================
-- V15: Create Shipments Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Added version field for VersionedEntity)
-- Description: Shipment tracking with carrier details and optimistic locking
-- =========================================

-- Create SHIPMENTS table
CREATE TABLE shipments (
    id BIGSERIAL PRIMARY KEY,
    shipment_number VARCHAR(50) NOT NULL UNIQUE,
    sales_order_id BIGINT NOT NULL,
    carrier VARCHAR(100) NOT NULL,
    tracking_number VARCHAR(100),
    shipping_method VARCHAR(50) NOT NULL CHECK (shipping_method IN ('STANDARD', 'EXPRESS', 'OVERNIGHT', 'GROUND')),
    estimated_delivery_date DATE,
    actual_delivery_date DATE,
    shipping_cost DECIMAL(12,2) NOT NULL DEFAULT 0,
    weight DECIMAL(10,2),
    dimensions VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'IN_TRANSIT', 'DELIVERED', 'RETURNED', 'FAILED')),
    shipped_from_warehouse_id BIGINT NOT NULL,
    shipped_by BIGINT NOT NULL,
    notes TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_shipment_sales_order FOREIGN KEY (sales_order_id)
        REFERENCES sales_orders(id) ON DELETE RESTRICT,
    CONSTRAINT fk_shipment_warehouse FOREIGN KEY (shipped_from_warehouse_id)
        REFERENCES warehouses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_shipment_shipped_by FOREIGN KEY (shipped_by)
        REFERENCES users(id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_shipment_sales_order ON shipments(sales_order_id);

CREATE INDEX idx_shipment_tracking ON shipments(tracking_number);

CREATE INDEX idx_shipment_status ON shipments(status);

CREATE INDEX idx_shipment_estimated_delivery ON shipments(estimated_delivery_date);

CREATE INDEX idx_shipment_warehouse ON shipments(shipped_from_warehouse_id);

CREATE INDEX idx_shipment_shipped_by ON shipments(shipped_by);

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_shipments_updated_at
    BEFORE UPDATE ON shipments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE shipments IS 'Shipment tracking information for sales order delivery';

COMMENT ON COLUMN shipments.shipment_number IS 'Unique shipment number (format: SHIP-YYYYMMDD-XXXX)';

COMMENT ON COLUMN shipments.shipping_method IS 'Service level: STANDARD, EXPRESS, OVERNIGHT, GROUND';

COMMENT ON COLUMN shipments.status IS 'Shipment status: PENDING, IN_TRANSIT, DELIVERED, RETURNED, FAILED';

COMMENT ON COLUMN shipments.tracking_number IS 'Carrier-provided tracking number for customer tracking';

COMMENT ON COLUMN shipments.dimensions IS 'Package dimensions in format: Length x Width x Height (cm)';

COMMENT ON COLUMN shipments.version IS 'Optimistic lock version for tracking and delivery status consistency';

COMMENT ON COLUMN shipments.created_by IS 'Username of the user who created this shipment record';

COMMENT ON COLUMN shipments.updated_by IS 'Username of the user who last updated this shipment (e.g., delivery confirmation)';