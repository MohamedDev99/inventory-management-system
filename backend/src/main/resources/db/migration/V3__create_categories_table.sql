-- V3: Create categories table with hierarchical structure
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    parent_category_id BIGINT,
    level INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_categories_parent
        FOREIGN KEY (parent_category_id)
        REFERENCES categories(id)
        ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_categories_code ON categories(code);
CREATE INDEX idx_categories_parent ON categories(parent_category_id);
CREATE INDEX idx_categories_level_parent ON categories(level, parent_category_id);

-- Comments
COMMENT ON TABLE categories IS 'Hierarchical product categorization';
COMMENT ON COLUMN categories.level IS 'Tree depth level (0 for root categories)';
COMMENT ON COLUMN categories.parent_category_id IS 'NULL for root categories';