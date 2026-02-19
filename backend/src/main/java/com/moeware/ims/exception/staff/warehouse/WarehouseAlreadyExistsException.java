package com.moeware.ims.exception.staff.warehouse;

/**
 * Exception thrown when attempting to create a warehouse that already exists
 */
public class WarehouseAlreadyExistsException extends RuntimeException {

    private final String code;
    private final String name;

    public WarehouseAlreadyExistsException(String fieldName, String fieldValue) {
        super(String.format("Warehouse already exists with %s: '%s'", fieldName, fieldValue));
        if ("code".equalsIgnoreCase(fieldName)) {
            this.code = fieldValue;
            this.name = null;
        } else if ("name".equalsIgnoreCase(fieldName)) {
            this.name = fieldValue;
            this.code = null;
        } else {
            this.code = null;
            this.name = null;
        }
    }

    public WarehouseAlreadyExistsException(String message) {
        super(message);
        this.code = null;
        this.name = null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}