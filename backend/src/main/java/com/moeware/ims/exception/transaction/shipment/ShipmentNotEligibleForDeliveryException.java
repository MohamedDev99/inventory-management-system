package com.moeware.ims.exception.transaction.shipment;

import com.moeware.ims.enums.transaction.ShipmentStatus;
import lombok.Getter;

/**
 * Thrown when the /deliver endpoint is called on a shipment that cannot
 * transition to DELIVERED from its current status.
 *
 * Valid path to DELIVERED: PENDING → IN_TRANSIT → DELIVERED
 * Invalid: RETURNED or FAILED shipments cannot be delivered.
 */
@Getter
public class ShipmentNotEligibleForDeliveryException extends RuntimeException {

    private final Long shipmentId;
    private final ShipmentStatus currentStatus;

    public ShipmentNotEligibleForDeliveryException(Long shipmentId, ShipmentStatus currentStatus) {
        super(String.format(
                "Shipment %d cannot be marked as delivered — current status '%s' does not allow this transition. "
                        + "Only PENDING or IN_TRANSIT shipments can be delivered.",
                shipmentId, currentStatus));
        this.shipmentId = shipmentId;
        this.currentStatus = currentStatus;
    }
}