package com.moeware.ims.exception.inventory.supplier;

/**
 * Exception thrown when attempting to create a supplier that already exists
 */
public class SupplierAlreadyExistsException extends RuntimeException {

    public SupplierAlreadyExistsException(String field, String value) {
        super("Supplier with " + field + " '" + value + "' already exists");
    }

    public SupplierAlreadyExistsException(String message) {
        super(message);
    }

    public SupplierAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}