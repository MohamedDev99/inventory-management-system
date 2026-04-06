package com.moeware.ims.exception.transaction.shipment;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;
import com.moeware.ims.enums.transaction.ShipmentStatus;

/**
 * Thrown when the /deliver endpoint is called on a shipment that cannot
 * transition to DELIVERED from its current status.
 *
 * Valid path to DELIVERED: PENDING → IN_TRANSIT → DELIVERED.
 * RETURNED and FAILED shipments cannot be delivered.
 *
 * @author MoeWare Team
 */
public class ShipmentNotEligibleForDeliveryException extends BaseAppException {

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

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Shipment Not Eligible For Delivery";
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public ShipmentStatus getCurrentStatus() {
        return currentStatus;
    }
}