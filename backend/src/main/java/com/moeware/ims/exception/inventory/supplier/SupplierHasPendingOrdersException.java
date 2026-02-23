package com.moeware.ims.exception.inventory.supplier;

/**
 * Exception thrown when attempting to delete a supplier with pending purchase
 * orders
 */
public class SupplierHasPendingOrdersException extends RuntimeException {

    public SupplierHasPendingOrdersException(Long supplierId) {
        super("Cannot delete supplier with ID: " + supplierId + ". Supplier has pending purchase orders.");
    }

    public SupplierHasPendingOrdersException(Long supplierId, int pendingOrderCount) {
        super("Cannot delete supplier with ID: " + supplierId +
                ". Supplier has " + pendingOrderCount + " pending purchase order(s).");
    }

    public SupplierHasPendingOrdersException(String message) {
        super(message);
    }

    public SupplierHasPendingOrdersException(String message, Throwable cause) {
        super(message, cause);
    }
}