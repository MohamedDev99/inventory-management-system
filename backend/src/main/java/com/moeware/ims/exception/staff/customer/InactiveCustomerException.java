package com.moeware.ims.exception.staff.customer;

/**
 * Exception thrown when attempting to perform operations on an inactive
 * customer
 */
public class InactiveCustomerException extends RuntimeException {

    private final Long customerId;
    private final String customerCode;

    public InactiveCustomerException(Long customerId) {
        super("Customer with ID: " + customerId + " is inactive. Cannot perform this operation.");
        this.customerId = customerId;
        this.customerCode = null;
    }

    public InactiveCustomerException(String customerCode) {
        super("Customer with code: " + customerCode + " is inactive. Cannot perform this operation.");
        this.customerId = null;
        this.customerCode = customerCode;
    }

    public InactiveCustomerException(String message, Throwable cause) {
        super(message, cause);
        this.customerId = null;
        this.customerCode = null;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }
}