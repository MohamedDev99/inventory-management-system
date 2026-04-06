package com.moeware.ims.exception.transaction.shipment;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;
import com.moeware.ims.enums.transaction.ShipmentStatus;

/**
 * Thrown when an operation requires an active shipment (PENDING or IN_TRANSIT)
 * but the shipment is already in a terminal state (DELIVERED, RETURNED, or
 * FAILED).
 *
 * @author MoeWare Team
 */
public class ShipmentAlreadyTerminatedException extends BaseAppException {

    private final Long shipmentId;
    private final ShipmentStatus currentStatus;

    public ShipmentAlreadyTerminatedException(Long shipmentId, ShipmentStatus currentStatus) {
        super(String.format(
                "Shipment %d cannot be modified — it is already in a terminal state: %s.",
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
        return "Shipment Already Terminated";
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public ShipmentStatus getCurrentStatus() {
        return currentStatus;
    }
}