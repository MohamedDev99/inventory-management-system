package com.moeware.ims.exception.transaction.shipment;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a shipment cannot be found by ID or shipment number.
 *
 * @author MoeWare Team
 */
public class ShipmentNotFoundException extends BaseAppException {

    private final Long shipmentId;
    private final String shipmentNumber;

    public ShipmentNotFoundException(Long shipmentId) {
        super("Shipment not found with ID: " + shipmentId);
        this.shipmentId = shipmentId;
        this.shipmentNumber = null;
    }

    public ShipmentNotFoundException(String shipmentNumber) {
        super("Shipment not found with number: " + shipmentNumber);
        this.shipmentId = null;
        this.shipmentNumber = shipmentNumber;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Shipment Not Found";
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public String getShipmentNumber() {
        return shipmentNumber;
    }
}