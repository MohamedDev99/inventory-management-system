-- V5: Create warehouses table
CREATE TABLE warehouses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    country VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    manager_id BIGINT,
    capacity DECIMAL(12,2),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_warehouses_manager
        FOREIGN KEY (manager_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

-- Indexes
CREATE UNIQUE INDEX idx_warehouses_code ON warehouses(code);
CREATE INDEX idx_warehouses_manager ON warehouses(manager_id);
CREATE INDEX idx_warehouses_active ON warehouses(id, name) WHERE is_active = true;

-- Comments
COMMENT ON TABLE warehouses IS 'Physical warehouse locations';
COMMENT ON COLUMN warehouses.capacity IS 'Storage capacity in square feet';
COMMENT ON COLUMN warehouses.code IS 'Short warehouse code (e.g., WH001)';