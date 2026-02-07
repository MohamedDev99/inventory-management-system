package com.moeware.ims.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Stock Adjustment Type enumeration
 * Indicates whether inventory is being added, removed, or corrected
 */
@Schema(description = "Type of stock adjustment operation")
public enum AdjustmentType {
    @Schema(description = "Add inventory to stock")
    ADD,

    @Schema(description = "Remove inventory from stock")
    REMOVE,

    @Schema(description = "Correct inventory count after physical verification")
    CORRECTION
}