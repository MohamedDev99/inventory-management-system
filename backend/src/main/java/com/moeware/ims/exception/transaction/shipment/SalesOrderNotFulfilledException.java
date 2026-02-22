package com.moeware.ims.exception.transaction.shipment;

import com.moeware.ims.enums.transaction.SalesOrderStatus;
import lombok.Getter;

/**
 * Thrown when a shipment creation is attempted for a sales order
 * that has not yet been fulfilled.
 *
 * A sales order must be in FULFILLED status before a shipment can be created —
 * meaning inventory has already been picked, packed, and deducted.
 */
@Getter
public class SalesOrderNotFulfilledException extends RuntimeException {

    private final Long salesOrderId;
    private final String soNumber;
    private final SalesOrderStatus currentStatus;

    public SalesOrderNotFulfilledException(Long salesOrderId, String soNumber,
            SalesOrderStatus currentStatus) {
        super(String.format(
                "Cannot create shipment for sales order '%s' — order must be in FULFILLED status "
                        + "before shipping, but current status is: %s.",
                soNumber, currentStatus));
        this.salesOrderId = salesOrderId;
        this.soNumber = soNumber;
        this.currentStatus = currentStatus;
    }
}