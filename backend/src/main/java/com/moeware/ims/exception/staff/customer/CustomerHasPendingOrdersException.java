package com.moeware.ims.exception.staff.customer;

/**
 * Exception thrown when attempting to delete a customer with pending sales
 * orders
 */
public class CustomerHasPendingOrdersException extends RuntimeException {

    public CustomerHasPendingOrdersException(Long customerId) {
        super("Cannot delete customer with ID: " + customerId + ". Customer has pending sales orders.");
    }

    public CustomerHasPendingOrdersException(Long customerId, int pendingOrderCount) {
        super("Cannot delete customer with ID: " + customerId +
                ". Customer has " + pendingOrderCount + " pending sales order(s).");
    }

    public CustomerHasPendingOrdersException(String message) {
        super(message);
    }

    public CustomerHasPendingOrdersException(String message, Throwable cause) {
        super(message, cause);
    }
}