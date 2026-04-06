package com.moeware.ims.exception.transaction.shipment;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a caller attempts to set shipment status to DELIVERED via the
 * generic status-update endpoint instead of the dedicated /deliver endpoint.
 *
 * Replaces the raw {@link IllegalArgumentException} in
 * {@code ShipmentService.updateShipmentStatus()}.
 *
 * @author MoeWare Team
 */
public class ShipmentDeliveryEndpointRequiredException extends BaseAppException {

    private final Long shipmentId;

    public ShipmentDeliveryEndpointRequiredException(Long shipmentId) {
        super(String.format(
                "Cannot set shipment %d to DELIVERED via this endpoint. "
                        + "Use the dedicated /deliver endpoint to mark a shipment as delivered.",
                shipmentId));
        this.shipmentId = shipmentId;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorTitle() {
        return "Use Deliver Endpoint";
    }

    public Long getShipmentId() {
        return shipmentId;
    }
}