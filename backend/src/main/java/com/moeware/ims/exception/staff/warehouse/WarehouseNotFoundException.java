package com.moeware.ims.exception.staff.warehouse;

/**
 * Exception thrown when a requested warehouse is not found
 * More specific than generic ResourceNotFoundException for warehouse operations
 */
public class WarehouseNotFoundException extends RuntimeException {

    private final Long warehouseId;
    private final String code;

    public WarehouseNotFoundException(Long warehouseId) {
        super("Warehouse not found with id: " + warehouseId);
        this.warehouseId = warehouseId;
        this.code = null;
    }

    public WarehouseNotFoundException(String code) {
        super("Warehouse not found with code: " + code);
        this.warehouseId = null;
        this.code = code;
    }

    public WarehouseNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.warehouseId = null;
        this.code = null;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public String getCode() {
        return code;
    }
}