package com.moeware.ims.exception.inventory.supplier;

/**
 * Exception thrown when attempting to perform operations on an inactive
 * supplier
 */
public class InactiveSupplierException extends RuntimeException {

    public InactiveSupplierException(Long supplierId) {
        super("Supplier with ID: " + supplierId + " is inactive. Cannot perform this operation.");
    }

    public InactiveSupplierException(String supplierCode) {
        super("Supplier with code: " + supplierCode + " is inactive. Cannot perform this operation.");
    }

    public InactiveSupplierException(String message, Throwable cause) {
        super(message, cause);
    }
}