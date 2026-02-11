package com.moeware.ims.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Notification type enumeration
 */
@Schema(description = "Notification type categories")
public enum NotificationType {
    @Schema(description = "Alert for products below reorder level")
    LOW_STOCK,

    @Schema(description = "Notification when a purchase order is approved")
    ORDER_APPROVED,

    @Schema(description = "Notification when a purchase order is received")
    ORDER_RECEIVED,

    @Schema(description = "Notification about shipment status updates")
    SHIPMENT,

    @Schema(description = "Notification about stock adjustment approvals or rejections")
    STOCK_ADJUSTMENT,

    @Schema(description = "General system announcements and notifications")
    SYSTEM
}