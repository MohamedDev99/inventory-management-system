package com.moeware.ims.enums.transaction;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Payment Status enumeration
 * Tracks the current status of a payment transaction
 */
@Schema(description = "Current status of a payment")
public enum PaymentStatus {
    @Schema(description = "Payment is awaiting processing")
    PENDING,

    @Schema(description = "Payment has been successfully processed")
    COMPLETED,

    @Schema(description = "Payment processing failed")
    FAILED,

    @Schema(description = "Payment has been refunded to customer")
    REFUNDED
}