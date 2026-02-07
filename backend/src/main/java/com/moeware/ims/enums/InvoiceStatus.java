package com.moeware.ims.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Invoice Status enumeration
 * Tracks the lifecycle and payment status of invoices
 */
@Schema(description = "Current status of an invoice")
public enum InvoiceStatus {
    @Schema(description = "Invoice is being created and not yet finalized")
    DRAFT,

    @Schema(description = "Invoice has been sent to customer")
    SENT,

    @Schema(description = "Invoice has been fully paid")
    PAID,

    @Schema(description = "Invoice has been partially paid")
    PARTIAL,

    @Schema(description = "Invoice payment is past due date")
    OVERDUE,

    @Schema(description = "Invoice has been cancelled")
    CANCELLED
}