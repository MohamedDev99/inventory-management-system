-- V10: Add manager foreign key to departments table
-- This is done after employees table is created to avoid circular dependency

ALTER TABLE departments
ADD CONSTRAINT fk_departments_manager
    FOREIGN KEY (manager_id)
    REFERENCES employees(id)
    ON DELETE SET NULL;

CREATE INDEX idx_departments_manager ON departments(manager_id);

COMMENT ON COLUMN departments.manager_id IS 'Department manager (employee)';