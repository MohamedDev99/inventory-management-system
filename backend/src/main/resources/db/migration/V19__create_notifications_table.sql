-- =========================================
-- V19: Create Notifications Table
-- Created: 2026-02-15
-- Description: User notifications and alerts (append-only entity)
-- =========================================

-- Create NOTIFICATIONS table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_type VARCHAR(50) NOT NULL CHECK (notification_type IN ('LOW_STOCK', 'ORDER_APPROVED', 'ORDER_RECEIVED', 'SHIPMENT', 'STOCK_ADJUSTMENT', 'SYSTEM')),
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    is_read BOOLEAN NOT NULL DEFAULT false,
    reference_type VARCHAR(50),
    reference_id BIGINT,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),

    CONSTRAINT fk_notification_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_notifications_user_id ON notifications(user_id);

CREATE INDEX idx_notifications_notification_type ON notifications(notification_type);

CREATE INDEX idx_notifications_user_created ON notifications(user_id, created_at DESC);

-- Partial index for unread notifications (performance optimization)
CREATE INDEX idx_notifications_unread ON notifications(user_id, created_at DESC)
    WHERE is_read = false;

-- Comments
COMMENT ON TABLE notifications IS 'User notifications and system alerts (append-only entity - never updated after creation)';

COMMENT ON COLUMN notifications.user_id IS 'User who will receive this notification';

COMMENT ON COLUMN notifications.notification_type IS 'Type of notification: LOW_STOCK, ORDER_APPROVED, ORDER_RECEIVED, SHIPMENT, STOCK_ADJUSTMENT, SYSTEM';

COMMENT ON COLUMN notifications.title IS 'Short notification title for display in notification center';

COMMENT ON COLUMN notifications.message IS 'Detailed notification message with full context';

COMMENT ON COLUMN notifications.priority IS 'Priority level: LOW, MEDIUM, HIGH, CRITICAL';

COMMENT ON COLUMN notifications.is_read IS 'Read status flag - updated when user views notification';

COMMENT ON COLUMN notifications.reference_type IS 'Type of related entity (e.g., PRODUCT, PURCHASE_ORDER, SALES_ORDER)';

COMMENT ON COLUMN notifications.reference_id IS 'ID of the related entity for linking';

COMMENT ON COLUMN notifications.read_at IS 'Timestamp when notification was marked as read';

COMMENT ON COLUMN notifications.created_at IS 'Notification creation timestamp (immutable)';

COMMENT ON COLUMN notifications.created_by IS 'System or user who triggered this notification';

COMMENT ON CONSTRAINT fk_notification_user ON notifications IS 'CASCADE delete: Remove notifications when user is deleted';