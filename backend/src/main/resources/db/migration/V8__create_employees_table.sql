-- =========================================
-- V8: Create Employees Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Added version and audit fields for VersionedEntity)
-- Description: Employee records with job information, hierarchy, and optimistic locking
-- =========================================

-- Create EMPLOYEES table
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    job_title VARCHAR(100),
    department_id BIGINT,
    manager_id BIGINT,
    hire_date DATE NOT NULL,
    termination_date DATE,
    salary DECIMAL(12,2),
    user_id BIGINT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_employees_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_employees_manager
        FOREIGN KEY (manager_id)
        REFERENCES employees(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_employees_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

-- Indexes
CREATE UNIQUE INDEX idx_employees_code ON employees(employee_code);

CREATE UNIQUE INDEX idx_employees_email ON employees(email);

CREATE INDEX idx_employees_department ON employees(department_id);

CREATE INDEX idx_employees_manager ON employees(manager_id);

CREATE INDEX idx_employees_user ON employees(user_id);

CREATE INDEX idx_employees_name ON employees(last_name, first_name);

CREATE INDEX idx_employees_active ON employees(id) WHERE is_active = true;

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_employees_updated_at
    BEFORE UPDATE ON employees
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE employees IS 'Employee records';

COMMENT ON COLUMN employees.user_id IS 'Link to system user account (optional)';

COMMENT ON COLUMN employees.salary IS 'Annual salary - financial data protected by version field';

COMMENT ON COLUMN employees.version IS 'Optimistic lock version for protecting personnel records and salary changes';

COMMENT ON COLUMN employees.created_by IS 'Username of the user who created this employee record';

COMMENT ON COLUMN employees.updated_by IS 'Username of the user who last updated this employee record';