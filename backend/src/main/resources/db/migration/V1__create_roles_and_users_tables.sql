-- =========================================
-- V1: Create Roles and Users Tables
-- Created: 2026-01-28
-- Description: Initial schema for authentication
-- =========================================

-- Create ROLES table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on role name
CREATE INDEX idx_roles_name ON roles (name);

-- Insert default roles
INSERT INTO
    roles (
        id,
        name,
        description,
        created_at
    )
VALUES (
        1,
        'ADMIN',
        'Full system access',
        CURRENT_TIMESTAMP
    ),
    (
        2,
        'MANAGER',
        'Warehouse and order management',
        CURRENT_TIMESTAMP
    ),
    (
        3,
        'WAREHOUSE_STAFF',
        'Inventory operations only',
        CURRENT_TIMESTAMP
    ),
    (
        4,
        'VIEWER',
        'Read-only access',
        CURRENT_TIMESTAMP
    );

-- Reset sequence to start after seeded data
SELECT setval('roles_id_seq', 4, true);

-- Create USERS table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

-- Foreign key constraint
CONSTRAINT fk_users_role FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE RESTRICT
);

-- Create indexes for performance
CREATE INDEX idx_users_username ON users (username);

CREATE INDEX idx_users_email ON users (email);

CREATE INDEX idx_users_role_id ON users (role_id);

CREATE INDEX idx_users_is_active_role ON users (is_active, role_id);

-- Partial index for active users only
CREATE INDEX idx_users_active_only ON users (id)
WHERE
    is_active = true;

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments for documentation
COMMENT ON TABLE roles IS 'System roles for role-based access control (RBAC)';

COMMENT ON TABLE users IS 'User accounts with authentication details';

COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';

COMMENT ON COLUMN users.is_active IS 'Account active status (soft delete)';