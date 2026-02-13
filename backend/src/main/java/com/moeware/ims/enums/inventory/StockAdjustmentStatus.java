package com.moeware.ims.enums.inventory;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Stock Adjustment Status enumeration
 * Tracks the approval status of a stock adjustment
 */
@Schema(description = "Approval status of the adjustment")
public enum StockAdjustmentStatus {
    @Schema(description = "Adjustment is awaiting approval")
    PENDING,

    @Schema(description = "Adjustment has been approved")
    APPROVED,

    @Schema(description = "Adjustment has been rejected")
    REJECTED
}