package com.moeware.ims.exception.staff.customer;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to delete a customer that has pending sales orders.
 * Currently unused in the service (noted as TODO) — migrated to
 * BaseAppException
 * so it is handler-ready when it gets wired in.
 *
 * @author MoeWare Team
 */
public class CustomerHasPendingOrdersException extends BaseAppException {

    private final Long customerId;
    private final int pendingOrderCount;

    public CustomerHasPendingOrdersException(Long customerId) {
        super("Cannot delete customer with ID: " + customerId + ". Customer has pending sales orders.");
        this.customerId = customerId;
        this.pendingOrderCount = 0;
    }

    public CustomerHasPendingOrdersException(Long customerId, int pendingOrderCount) {
        super(String.format(
                "Cannot delete customer with ID: %d. Customer has %d pending sales order(s).",
                customerId, pendingOrderCount));
        this.customerId = customerId;
        this.pendingOrderCount = pendingOrderCount;
    }

    public CustomerHasPendingOrdersException(String message, Throwable cause) {
        super(message, cause);
        this.customerId = null;
        this.pendingOrderCount = 0;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Customer Has Pending Orders";
    }

    public Long getCustomerId() {
        return customerId;
    }

    public int getPendingOrderCount() {
        return pendingOrderCount;
    }
}