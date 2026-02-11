package com.moeware.ims.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Notification priority levels
 */
@Schema(description = "Priority levels for notifications")
public enum NotificationPriority {
    @Schema(description = "Low priority - informational only")
    LOW,

    @Schema(description = "Medium priority - standard notification (default)")
    MEDIUM,

    @Schema(description = "High priority - requires attention")
    HIGH,

    @Schema(description = "Critical priority - urgent action required")
    CRITICAL
}