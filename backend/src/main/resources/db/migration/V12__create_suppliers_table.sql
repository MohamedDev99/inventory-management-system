-- V6: Create suppliers table
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
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_supplier_email ON suppliers(email);
CREATE INDEX idx_supplier_active ON suppliers(is_active) WHERE is_active = true;

-- Insert sample suppliers
INSERT INTO suppliers (name, code, email, payment_terms, rating) VALUES
('Tech Supplies Inc', 'SUP001', 'orders@techsupplies.com', 'Net 30', 5),
('Global Electronics', 'SUP002', 'sales@globalelec.com', 'Net 45', 4);