package com.moeware.ims.exception.transaction;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an attempt is made to edit or cancel an order that is in a
 * non-editable status (e.g. already SHIPPED, DELIVERED, or RECEIVED).
 *
 * @author MoeWare Team
 */
public class OrderNotEditableException extends BaseAppException {

    private final String orderType;
    private final Long orderId;
    private final String currentStatus;

    public OrderNotEditableException(String orderType, Long orderId, String currentStatus) {
        super(String.format("%s with id '%d' cannot be edited in status '%s'", orderType, orderId, currentStatus));
        this.orderType = orderType;
        this.orderId = orderId;
        this.currentStatus = currentStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Order Not Editable";
    }

    public String getOrderType() {
        return orderType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }
}