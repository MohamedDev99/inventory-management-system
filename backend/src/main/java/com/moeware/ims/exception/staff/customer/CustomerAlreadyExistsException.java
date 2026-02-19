package com.moeware.ims.exception.staff.customer;

/**
 * Exception thrown when attempting to create a customer that already exists
 */
public class CustomerAlreadyExistsException extends RuntimeException {

    public CustomerAlreadyExistsException(String field, String value) {
        super("Customer with " + field + " '" + value + "' already exists");
    }

    public CustomerAlreadyExistsException(String message) {
        super(message);
    }

    public CustomerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}