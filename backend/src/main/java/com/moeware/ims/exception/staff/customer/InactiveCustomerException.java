package com.moeware.ims.exception.staff.customer;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to perform an operation on a customer that is
 * inactive.
 *
 * @author MoeWare Team
 */
public class InactiveCustomerException extends BaseAppException {

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

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Customer Inactive";
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }
}