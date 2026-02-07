package com.moeware.ims.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Purchase Order Status enumeration
 * Represents the lifecycle states of a purchase order
 */
@Schema(description = "Status of a purchase order throughout its lifecycle")
public enum PurchaseOrderStatus {
    @Schema(description = "Order is being created and can be edited freely")
    DRAFT,

    @Schema(description = "Order has been submitted for approval")
    SUBMITTED,

    @Schema(description = "Order has been approved and is ready for processing")
    APPROVED,

    @Schema(description = "Order items have been received at the warehouse")
    RECEIVED,

    @Schema(description = "Order has been cancelled and will not be processed")
    CANCELLED
}