package com.moeware.ims.exception.transaction.shipment;

import com.moeware.ims.enums.transaction.ShipmentStatus;
import lombok.Getter;

/**
 * Thrown when an operation requires an active shipment (PENDING or IN_TRANSIT)
 * but the shipment is already in a terminal state (DELIVERED, RETURNED, or
 * FAILED).
 *
 * Examples:
 * - Trying to mark a DELIVERED shipment as delivered again
 * - Trying to update the status of a RETURNED shipment
 * - Trying to update the status of a FAILED shipment
 */
@Getter
public class ShipmentAlreadyTerminatedException extends RuntimeException {

    private final Long shipmentId;
    private final ShipmentStatus currentStatus;

    public ShipmentAlreadyTerminatedException(Long shipmentId, ShipmentStatus currentStatus) {
        super(String.format(
                "Shipment %d cannot be modified — it is already in a terminal state: %s.",
                shipmentId, currentStatus));
        this.shipmentId = shipmentId;
        this.currentStatus = currentStatus;
    }
}