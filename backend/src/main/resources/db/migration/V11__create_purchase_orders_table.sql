-- =========================================
-- V11: Create Purchase Orders Tables
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Added version fields for VersionedEntity)
-- Description: Purchase orders and line items with approval workflow and optimistic locking
-- =========================================

-- Create PURCHASE_ORDERS table
CREATE TABLE purchase_orders (
    id BIGSERIAL PRIMARY KEY,
    po_number VARCHAR(50) NOT NULL UNIQUE,
    supplier_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    created_by_user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'RECEIVED', 'CANCELLED')),
    order_date DATE NOT NULL,
    expected_delivery_date DATE,
    actual_delivery_date DATE,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    notes TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_po_supplier FOREIGN KEY (supplier_id)
        REFERENCES suppliers(id) ON DELETE RESTRICT,
    CONSTRAINT fk_po_warehouse FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_po_created_by FOREIGN KEY (created_by_user_id)
        REFERENCES users(id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_po_supplier ON purchase_orders(supplier_id);

CREATE INDEX idx_po_warehouse ON purchase_orders(warehouse_id);

CREATE INDEX idx_po_status ON purchase_orders(status);

CREATE INDEX idx_po_order_date ON purchase_orders(order_date DESC);

CREATE INDEX idx_po_status_date ON purchase_orders(status, order_date DESC);

CREATE INDEX idx_po_supplier_date ON purchase_orders(supplier_id, order_date DESC);

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_purchase_orders_updated_at
    BEFORE UPDATE ON purchase_orders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE purchase_orders IS 'Purchase order headers with workflow status';

COMMENT ON COLUMN purchase_orders.po_number IS 'Unique PO number (format: PO-YYYYMMDD-XXXX)';

COMMENT ON COLUMN purchase_orders.status IS 'Order status: DRAFT, SUBMITTED, APPROVED, RECEIVED, CANCELLED';

COMMENT ON COLUMN purchase_orders.version IS 'Optimistic lock version - CRITICAL for financial data and preventing duplicate approvals';

COMMENT ON COLUMN purchase_orders.created_by IS 'Username of the user who created this purchase order';

COMMENT ON COLUMN purchase_orders.updated_by IS 'Username of the user who last updated this purchase order';

-- Create PURCHASE_ORDER_ITEMS table
CREATE TABLE purchase_order_items (
    id BIGSERIAL PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity_ordered INT NOT NULL CHECK (quantity_ordered > 0),
    quantity_received INT NOT NULL DEFAULT 0 CHECK (quantity_received >= 0),
    unit_price DECIMAL(12,2) NOT NULL,
    line_total DECIMAL(12,2) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_poi_purchase_order FOREIGN KEY (purchase_order_id)
        REFERENCES purchase_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_poi_product FOREIGN KEY (product_id)
        REFERENCES products(id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_poi_purchase_order ON purchase_order_items(purchase_order_id);

CREATE INDEX idx_poi_product ON purchase_order_items(product_id);

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_purchase_order_items_updated_at
    BEFORE UPDATE ON purchase_order_items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE purchase_order_items IS 'Line items for purchase orders';

COMMENT ON COLUMN purchase_order_items.quantity_received IS 'Quantity actually received (updated during receiving process)';

COMMENT ON COLUMN purchase_order_items.line_total IS 'Line total = quantity_ordered × unit_price';

COMMENT ON COLUMN purchase_order_items.version IS 'Optimistic lock version - CRITICAL for preventing receiving quantity corruption';

COMMENT ON COLUMN purchase_order_items.created_by IS 'Username of the user who created this line item';

COMMENT ON COLUMN purchase_order_items.updated_by IS 'Username of the user who last updated this line item';