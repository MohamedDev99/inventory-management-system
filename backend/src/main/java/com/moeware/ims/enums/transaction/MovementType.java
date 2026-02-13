package com.moeware.ims.enums.transaction;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Inventory Movement Type enumeration
 * Defines the type of inventory movement operation
 */
@Schema(description = "Type of inventory movement operation")
public enum MovementType {
    @Schema(description = "Transfer of inventory between warehouses")
    TRANSFER,

    @Schema(description = "Manual adjustment of inventory levels")
    ADJUSTMENT,

    @Schema(description = "Receipt of inventory from purchase order")
    RECEIPT,

    @Schema(description = "Shipment of inventory for sales order")
    SHIPMENT
}