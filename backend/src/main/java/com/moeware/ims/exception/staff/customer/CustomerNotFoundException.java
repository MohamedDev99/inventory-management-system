package com.moeware.ims.exception.staff.customer;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a customer cannot be found by ID or customer code.
 *
 * @author MoeWare Team
 */
public class CustomerNotFoundException extends BaseAppException {

    private final Long customerId;
    private final String customerCode;

    public CustomerNotFoundException(Long customerId) {
        super("Customer not found with ID: " + customerId);
        this.customerId = customerId;
        this.customerCode = null;
    }

    public CustomerNotFoundException(String customerCode) {
        super("Customer not found with code: " + customerCode);
        this.customerId = null;
        this.customerCode = customerCode;
    }

    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.customerId = null;
        this.customerCode = null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Customer Not Found";
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }
}