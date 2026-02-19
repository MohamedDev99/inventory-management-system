package com.moeware.ims.exception.staff.customer;

/**
 * Exception thrown when a customer is not found
 */
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
        super("Customer not found with ID: " + id);
    }

    public CustomerNotFoundException(String customerCode) {
        super("Customer not found with code: " + customerCode);
    }

    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}