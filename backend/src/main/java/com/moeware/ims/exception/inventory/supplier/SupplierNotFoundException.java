package com.moeware.ims.exception.inventory.supplier;

/**
 * Exception thrown when a supplier is not found
 */
public class SupplierNotFoundException extends RuntimeException {

    public SupplierNotFoundException(Long id) {
        super("Supplier not found with ID: " + id);
    }

    public SupplierNotFoundException(String code) {
        super("Supplier not found with code: " + code);
    }

    public SupplierNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}