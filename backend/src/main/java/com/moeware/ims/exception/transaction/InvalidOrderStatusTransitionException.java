package com.moeware.ims.exception.transaction;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an order status transition is not allowed by the workflow rules.
 * e.g. trying to SHIP an order that is still PENDING.
 *
 * @author MoeWare Team
 */
public class InvalidOrderStatusTransitionException extends BaseAppException {

    private final String orderType;
    private final String currentStatus;
    private final String targetStatus;

    public InvalidOrderStatusTransitionException(String orderType, String currentStatus, String targetStatus) {
        super(String.format("Cannot transition %s from '%s' to '%s'", orderType, currentStatus, targetStatus));
        this.orderType = orderType;
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Invalid Order Status Transition";
    }

    public String getOrderType() {
        return orderType;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getTargetStatus() {
        return targetStatus;
    }
}