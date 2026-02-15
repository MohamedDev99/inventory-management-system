-- =========================================
-- V18: Create Audit Logs Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Aligned with AppendOnlyEntity - no version, no updates)
-- Description: Append-only audit trail for all critical operations
-- =========================================

-- Create AUDIT_LOGS table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL CHECK (action IN ('CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'APPROVE', 'REJECT')),
    old_values JSONB,
    new_values JSONB,
    performed_by BIGINT NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),

    CONSTRAINT fk_audit_performed_by FOREIGN KEY (performed_by)
        REFERENCES users(id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);

CREATE INDEX idx_audit_performed_by ON audit_logs(performed_by);

CREATE INDEX idx_audit_action ON audit_logs(action);

CREATE INDEX idx_audit_created_at ON audit_logs(created_at DESC);

CREATE INDEX idx_audit_entity_created ON audit_logs(entity_type, entity_id, created_at DESC);

-- Comments
COMMENT ON TABLE audit_logs IS 'Append-only audit trail for all critical operations (never updated or deleted)';

COMMENT ON COLUMN audit_logs.entity_type IS 'Type of entity modified (e.g., PRODUCT, USER, ORDER)';

COMMENT ON COLUMN audit_logs.entity_id IS 'ID of the entity that was modified';

COMMENT ON COLUMN audit_logs.action IS 'Action performed: CREATE, UPDATE, DELETE, LOGIN, LOGOUT, APPROVE, REJECT';

COMMENT ON COLUMN audit_logs.old_values IS 'Previous values before change (JSONB format)';

COMMENT ON COLUMN audit_logs.new_values IS 'New values after change (JSONB format)';

COMMENT ON COLUMN audit_logs.ip_address IS 'IP address from which action was performed';

COMMENT ON COLUMN audit_logs.user_agent IS 'Browser user agent string';

COMMENT ON COLUMN audit_logs.created_at IS 'Timestamp when action occurred (immutable)';

COMMENT ON COLUMN audit_logs.created_by IS 'Username who performed the action (for audit correlation)';