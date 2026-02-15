-- =========================================
-- V9: Add Department Manager Foreign Key
-- Created: 2026-01-28
-- Updated: 2026-02-14 (No changes - structural migration)
-- Description: Add manager_id foreign key to departments table after employees table exists
-- =========================================

-- Add manager foreign key to departments table
-- This is done after employees table is created to avoid circular dependency
ALTER TABLE departments
ADD CONSTRAINT fk_departments_manager
    FOREIGN KEY (manager_id)
    REFERENCES employees(id)
    ON DELETE SET NULL;

-- Create index on manager_id
CREATE INDEX idx_departments_manager ON departments(manager_id);

-- Update comment
COMMENT ON COLUMN departments.manager_id IS 'Department manager (employee)';