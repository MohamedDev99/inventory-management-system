package com.moeware.ims.exception.transaction;

/**
 * Exception thrown when an attempt is made to edit an order that is not in an
 * editable state
 */
public class OrderNotEditableException extends RuntimeException {

    private final String orderType;
    private final Long orderId;
    private final String currentStatus;

    public OrderNotEditableException(String orderType, Long orderId, String currentStatus) {
        super(String.format("%s with id '%d' cannot be edited in status '%s'", orderType, orderId, currentStatus));
        this.orderType = orderType;
        this.orderId = orderId;
        this.currentStatus = currentStatus;
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