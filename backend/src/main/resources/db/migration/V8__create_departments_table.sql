-- V8: Create departments table
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    department_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    manager_id BIGINT,
    parent_department_id BIGINT,
    cost_center VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_departments_parent
        FOREIGN KEY (parent_department_id)
        REFERENCES departments(id)
        ON DELETE SET NULL
);

-- Note: manager_id foreign key will be added after employees table is created

-- Indexes
CREATE UNIQUE INDEX idx_departments_code ON departments(department_code);
CREATE INDEX idx_departments_parent ON departments(parent_department_id);
CREATE INDEX idx_departments_active ON departments(id) WHERE is_active = true;

-- Comments
COMMENT ON TABLE departments IS 'Organizational departments';
COMMENT ON COLUMN departments.cost_center IS 'Cost center code for accounting';