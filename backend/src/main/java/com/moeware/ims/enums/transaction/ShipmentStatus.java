package com.moeware.ims.enums.transaction;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Shipment Status enumeration
 * Tracks the current status of a shipment
 */
@Schema(description = "Current status of a shipment")
public enum ShipmentStatus {
    @Schema(description = "Shipment is being prepared")
    PENDING,

    @Schema(description = "Shipment is currently in transit")
    IN_TRANSIT,

    @Schema(description = "Shipment has been delivered successfully")
    DELIVERED,

    @Schema(description = "Shipment was returned by carrier or customer")
    RETURNED,

    @Schema(description = "Shipment delivery failed")
    FAILED
}