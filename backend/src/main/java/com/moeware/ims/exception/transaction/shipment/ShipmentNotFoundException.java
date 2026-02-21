package com.moeware.ims.exception.transaction.shipment;

import lombok.Getter;

@Getter
public class ShipmentNotFoundException extends RuntimeException {

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
}