-- =========================================
-- V2: Create Categories Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Aligned with AuditableEntity - no version field)
-- Description: Hierarchical product categorization with parent-child relationships
-- =========================================

-- Create CATEGORIES table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    parent_category_id BIGINT,
    level INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    -- Foreign key constraint
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_category_id)
        REFERENCES categories (id)
        ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_categories_code ON categories (code);

CREATE INDEX idx_categories_parent ON categories (parent_category_id);

CREATE INDEX idx_categories_level_parent ON categories (level, parent_category_id);

-- Create function to update updated_at timestamp (if not already created)
-- CREATE OR REPLACE FUNCTION update_updated_at_column()
-- RETURNS TRIGGER AS $$
-- BEGIN
--     NEW.updated_at = CURRENT_TIMESTAMP;
--     RETURN NEW;
-- END;
-- $$ language 'plpgsql';

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_categories_updated_at
    BEFORE UPDATE ON categories
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE categories IS 'Hierarchical product categorization';

COMMENT ON COLUMN categories.level IS 'Tree depth level (0 for root categories)';

COMMENT ON COLUMN categories.parent_category_id IS 'NULL for root categories';

COMMENT ON COLUMN categories.code IS 'Unique category code for identification';