package com.moeware.ims.exception.staff.warehouse;

/**
 * Exception thrown when a requested warehouse is not found
 * More specific than generic ResourceNotFoundException for warehouse operations
 */
public class WarehouseNotFoundException extends RuntimeException {

    private final Long warehouseId;

    public WarehouseNotFoundException(Long warehouseId) {
        super("Warehouse not found with id: " + warehouseId);
        this.warehouseId = warehouseId;
    }

    public WarehouseNotFoundException(String code) {
        super("Warehouse not found with code: " + code);
        this.warehouseId = null;
    }

    public WarehouseNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.warehouseId = null;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }
}