-- =========================================
-- V7: Create Departments Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Aligned with AuditableEntity - no version field)
-- Description: Organizational departments with hierarchical structure
-- =========================================

-- Create DEPARTMENTS table
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
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_departments_parent
        FOREIGN KEY (parent_department_id)
        REFERENCES departments(id)
        ON DELETE SET NULL
);

-- Note: manager_id foreign key will be added in V10 after employees table is created

-- Indexes
CREATE UNIQUE INDEX idx_departments_code ON departments(department_code);

CREATE INDEX idx_departments_parent ON departments(parent_department_id);

CREATE INDEX idx_departments_active ON departments(id) WHERE is_active = true;

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_departments_updated_at
    BEFORE UPDATE ON departments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE departments IS 'Organizational departments';

COMMENT ON COLUMN departments.cost_center IS 'Cost center code for accounting';

COMMENT ON COLUMN departments.manager_id IS 'Department manager (foreign key added in V10)';