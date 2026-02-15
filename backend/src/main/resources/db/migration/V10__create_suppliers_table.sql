-- =========================================
-- V10: Create Suppliers Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Added version field for VersionedEntity)
-- Description: Supplier information with contact details and optimistic locking
-- =========================================

-- Create SUPPLIERS table
CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    contact_person VARCHAR(100),
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    address TEXT,
    city VARCHAR(100),
    country VARCHAR(100),
    payment_terms VARCHAR(100),
    rating INT CHECK (rating >= 1 AND rating <= 5),
    is_active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Indexes
CREATE INDEX idx_supplier_email ON suppliers(email);

CREATE INDEX idx_supplier_active ON suppliers(is_active) WHERE is_active = true;

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_suppliers_updated_at
    BEFORE UPDATE ON suppliers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE suppliers IS 'Supplier information and vendor details';

COMMENT ON COLUMN suppliers.rating IS 'Supplier performance rating from 1 (poor) to 5 (excellent)';

COMMENT ON COLUMN suppliers.payment_terms IS 'Payment terms with supplier (Net 30, Net 45, etc.)';

COMMENT ON COLUMN suppliers.version IS 'Optimistic lock version - suppliers are static reference data per schema v1.2';

COMMENT ON COLUMN suppliers.created_by IS 'Username of the user who created this supplier record';

COMMENT ON COLUMN suppliers.updated_by IS 'Username of the user who last updated this supplier record';

-- Insert sample suppliers
INSERT INTO suppliers (name, code, email, payment_terms, rating) VALUES
('Tech Supplies Inc', 'SUP001', 'orders@techsupplies.com', 'Net 30', 5),
('Global Electronics', 'SUP002', 'sales@globalelec.com', 'Net 45', 4);