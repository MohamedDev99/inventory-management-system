package com.moeware.ims.exception.transaction;

/**
 * Exception thrown when an invalid order status transition is attempted
 */
public class InvalidOrderStatusTransitionException extends RuntimeException {

    private final String currentStatus;
    private final String targetStatus;
    private final String orderType;

    public InvalidOrderStatusTransitionException(String orderType, String currentStatus, String targetStatus) {
        super(String.format("Cannot transition %s from '%s' to '%s'", orderType, currentStatus, targetStatus));
        this.orderType = orderType;
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getTargetStatus() {
        return targetStatus;
    }

    public String getOrderType() {
        return orderType;
    }
}