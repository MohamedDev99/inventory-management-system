package com.moeware.ims.exception.staff.customer;

/**
 * Exception thrown when an invalid customer type is provided
 */
public class InvalidCustomerTypeException extends RuntimeException {

    public InvalidCustomerTypeException(String customerType) {
        super("Invalid customer type: '" + customerType + "'. Must be RETAIL, WHOLESALE, or CORPORATE.");
    }

    public InvalidCustomerTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}