package com.moeware.ims.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Stock Adjustment Reason enumeration
 * Provides standardized reasons for inventory adjustments
 */
@Schema(description = "Reason for stock adjustment")
public enum AdjustmentReason {
    @Schema(description = "Items are damaged and cannot be sold")
    DAMAGED,

    @Schema(description = "Items have passed expiration date")
    EXPIRED,

    @Schema(description = "Items are missing due to theft")
    THEFT,

    @Schema(description = "Inventory count was incorrect")
    COUNT_ERROR,

    @Schema(description = "Items returned from customer")
    RETURN,

    @Schema(description = "Other reason not listed above")
    OTHER
}