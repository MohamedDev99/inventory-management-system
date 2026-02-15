-- =========================================
-- V6: Create Customers Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Added version and audit fields for VersionedEntity)
-- Description: Customer information with billing/shipping addresses and optimistic locking
-- =========================================

-- Create CUSTOMERS table
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    customer_code VARCHAR(50) NOT NULL UNIQUE,
    company_name VARCHAR(255),
    contact_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    mobile VARCHAR(50),

    billing_address TEXT,
    billing_city VARCHAR(100),
    billing_state VARCHAR(100),
    billing_country VARCHAR(100),
    billing_postal_code VARCHAR(20),

    shipping_address TEXT,
    shipping_city VARCHAR(100),
    shipping_state VARCHAR(100),
    shipping_country VARCHAR(100),
    shipping_postal_code VARCHAR(20),

    credit_limit DECIMAL(12,2),
    payment_terms VARCHAR(100),
    customer_type VARCHAR(20) NOT NULL CHECK (customer_type IN ('RETAIL', 'WHOLESALE', 'CORPORATE')),
    tax_id VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Indexes
CREATE UNIQUE INDEX idx_customers_code ON customers(customer_code);

CREATE UNIQUE INDEX idx_customers_email ON customers(email);

CREATE INDEX idx_customers_company ON customers(company_name);

CREATE INDEX idx_customers_type ON customers(customer_type);

CREATE INDEX idx_customers_active ON customers(id, contact_name) WHERE is_active = true;

-- Full-text search on contact name
CREATE INDEX idx_customers_contact_name_gin ON customers USING gin(to_tsvector('english', contact_name));

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_customers_updated_at
    BEFORE UPDATE ON customers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE customers IS 'Customer information and details';

COMMENT ON COLUMN customers.customer_type IS 'Type: RETAIL, WHOLESALE, or CORPORATE';

COMMENT ON COLUMN customers.payment_terms IS 'Payment terms (Net 30, COD, etc.)';

COMMENT ON COLUMN customers.version IS 'Optimistic lock version for protecting customer data and credit limits';

COMMENT ON COLUMN customers.created_by IS 'Username of the user who created this customer record';

COMMENT ON COLUMN customers.updated_by IS 'Username of the user who last updated this customer record';