-- =========================================
-- V4: Create Warehouses Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Added version and audit fields for VersionedEntity)
-- Description: Physical warehouse locations with optimistic locking
-- =========================================

-- Create WAREHOUSES table
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
    capacity DECIMAL(12, 2),
    is_active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_warehouses_manager FOREIGN KEY (manager_id)
        REFERENCES users (id)
        ON DELETE SET NULL
);

-- Indexes
CREATE UNIQUE INDEX idx_warehouses_code ON warehouses (code);

CREATE INDEX idx_warehouses_manager ON warehouses (manager_id);

CREATE INDEX idx_warehouses_active ON warehouses (id, name)
WHERE
    is_active = true;

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_warehouses_updated_at
    BEFORE UPDATE ON warehouses
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE warehouses IS 'Physical warehouse locations';

COMMENT ON COLUMN warehouses.capacity IS 'Storage capacity in square feet';

COMMENT ON COLUMN warehouses.code IS 'Short warehouse code (e.g., WH001)';

COMMENT ON COLUMN warehouses.version IS 'Optimistic lock version for preventing concurrent capacity/configuration changes';

COMMENT ON COLUMN warehouses.created_by IS 'Username of the user who created this warehouse';

COMMENT ON COLUMN warehouses.updated_by IS 'Username of the user who last updated this warehouse';