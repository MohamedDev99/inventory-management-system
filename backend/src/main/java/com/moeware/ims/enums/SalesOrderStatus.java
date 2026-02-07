package com.moeware.ims.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Sales Order Status enumeration
 * Tracks the fulfillment and delivery status of sales orders
 */
@Schema(description = "Status of a sales order from creation to delivery")
public enum SalesOrderStatus {
    @Schema(description = "Order has been placed but not yet confirmed")
    PENDING,

    @Schema(description = "Order has been confirmed and inventory has been reserved")
    CONFIRMED,

    @Schema(description = "Order has been picked and packed, ready for shipment")
    FULFILLED,

    @Schema(description = "Order has been shipped to the customer")
    SHIPPED,

    @Schema(description = "Order has been delivered to the customer")
    DELIVERED,

    @Schema(description = "Order has been cancelled and inventory has been released")
    CANCELLED
}