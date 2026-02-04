-- V4: Create products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id BIGINT NOT NULL,
    unit VARCHAR(20) NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL CHECK (unit_price > 0),
    cost_price DECIMAL(12,2) NOT NULL CHECK (cost_price > 0),
    reorder_level INTEGER NOT NULL DEFAULT 10 CHECK (reorder_level >= 0),
    min_stock_level INTEGER NOT NULL DEFAULT 5 CHECK (min_stock_level >= 0),
    barcode VARCHAR(50) UNIQUE,
    image_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE RESTRICT
);

-- Indexes
CREATE UNIQUE INDEX idx_products_sku ON products(sku);
CREATE UNIQUE INDEX idx_products_barcode ON products(barcode) WHERE barcode IS NOT NULL;
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_active_category ON products(is_active, category_id);
CREATE INDEX idx_products_name ON products(name);

-- Full-text search (PostgreSQL specific)
CREATE INDEX idx_products_name_gin ON products USING gin(to_tsvector('english', name));

-- Partial index for active products
CREATE INDEX idx_products_active_only ON products(id, name, category_id) WHERE is_active = true;

-- Comments
COMMENT ON TABLE products IS 'Product catalog with pricing and stock settings';
COMMENT ON COLUMN products.sku IS 'Stock Keeping Unit - unique identifier';
COMMENT ON COLUMN products.unit IS 'Unit of measure (PIECE, KG, LITER, etc.)';
COMMENT ON COLUMN products.reorder_level IS 'Threshold for low stock alerts';
COMMENT ON COLUMN products.min_stock_level IS 'Minimum stock before critical alert';