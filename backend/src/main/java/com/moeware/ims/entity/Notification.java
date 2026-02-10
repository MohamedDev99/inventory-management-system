package com.moeware.ims.entity;

import com.moeware.ims.entity.AppendOnlyEntity;
import com.moeware.ims.enums.NotificationPriority;
import com.moeware.ims.enums.NotificationType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Notification entity for user alerts and system notifications
 *
 * Represents in-app notifications for users about various system events such
 * as:
 * - Low stock alerts
 * - Order approvals
 * - Shipment updates
 * - Stock adjustments
 * - System announcements
 *
 * Design Note: This is an append-only entity - notifications are never updated
 * after creation.
 * The is_read flag and read_at timestamp are managed separately to track read
 * status.
 *
 * @author MoeWare Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notifications_user_id", columnList = "user_id"),
        @Index(name = "idx_notifications_notification_type", columnList = "notification_type"),
        @Index(name = "idx_notifications_user_created", columnList = "user_id, created_at DESC"),
// Partial index for unread notifications (must be created via SQL migration)
// CREATE INDEX idx_notifications_unread ON notifications(user_id, created_at
// DESC) WHERE is_read = false;
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User notification entity for system alerts and messages")
public class Notification extends AppendOnlyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the notification", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_user"))
    @Schema(description = "User who will receive this notification", implementation = User.class)
    private User user;

    @Column(name = "notification_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Type of notification", example = "LOW_STOCK", allowableValues = { "LOW_STOCK",
            "ORDER_APPROVED", "ORDER_RECEIVED", "SHIPMENT", "STOCK_ADJUSTMENT", "SYSTEM" })
    private NotificationType notificationType;

    @Column(name = "title", nullable = false)
    @Schema(description = "Short notification title", example = "Low Stock Alert", maxLength = 255)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Detailed notification message", example = "Product LAP-001 (Dell Laptop XPS 15) is below reorder level in Warehouse WH001. Current stock: 3 units, Reorder level: 5 units.")
    private String message;

    @Column(name = "priority", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Schema(description = "Priority level of the notification", example = "HIGH", allowableValues = { "LOW", "MEDIUM",
            "HIGH", "CRITICAL" }, defaultValue = "MEDIUM")
    private NotificationPriority priority = NotificationPriority.MEDIUM;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the notification has been read by the user", example = "false", defaultValue = "false")
    private Boolean isRead = false;

    @Column(name = "reference_type", length = 50)
    @Schema(description = "Type of related entity (if applicable)", example = "PRODUCT", allowableValues = { "PRODUCT",
            "PURCHASE_ORDER", "SALES_ORDER", "STOCK_ADJUSTMENT", "SHIPMENT", "INVENTORY_ITEM" })
    private String referenceType;

    @Column(name = "reference_id")
    @Schema(description = "ID of related entity (if applicable)", example = "123")
    private Long referenceId;

    @Column(name = "read_at")
    @Schema(description = "Timestamp when the notification was marked as read", example = "2026-01-31T15:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime readAt;

    /**
     * Marks this notification as read and sets the read timestamp
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * Marks this notification as unread and clears the read timestamp
     */
    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
    }

    /**
     * Checks if this notification is for a specific entity
     *
     * @param entityType The entity type to check
     * @param entityId   The entity ID to check
     * @return true if this notification references the specified entity
     */
    public boolean isForEntity(String entityType, Long entityId) {
        return this.referenceType != null
                && this.referenceType.equals(entityType)
                && this.referenceId != null
                && this.referenceId.equals(entityId);
    }

    /**
     * Checks if this notification is unread
     *
     * @return true if the notification has not been read
     */
    public boolean isUnread() {
        return !this.isRead;
    }

    /**
     * Checks if this notification is critical priority
     *
     * @return true if priority is CRITICAL
     */
    public boolean isCritical() {
        return this.priority == NotificationPriority.CRITICAL;
    }

    /**
     * Gets a shortened version of the message for list displays
     *
     * @param maxLength Maximum length of the preview
     * @return Truncated message with ellipsis if needed
     */
    public String getMessagePreview(int maxLength) {
        if (message == null) {
            return null;
        }

        if (message.length() <= maxLength) {
            return message;
        }

        if (maxLength <= 3) {
            return message.substring(0, Math.max(0, maxLength));
        }
        return message.substring(0, maxLength - 3) + "...";
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (priority == null) {
            priority = NotificationPriority.MEDIUM;
        }
        if (isRead == null) {
            isRead = false;
        }
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", type=" + notificationType +
                ", title='" + title + '\'' +
                ", priority=" + priority +
                ", isRead=" + isRead +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}