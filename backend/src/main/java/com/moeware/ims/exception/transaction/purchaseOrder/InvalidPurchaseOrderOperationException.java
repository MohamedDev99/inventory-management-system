package com.moeware.ims.exception.transaction.purchaseOrder;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a purchase order operation violates a domain rule,
 * e.g. submitting a PO that has no line items.
 *
 * @author MoeWare Team
 */
public class InvalidPurchaseOrderOperationException extends BaseAppException {

    public InvalidPurchaseOrderOperationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorTitle() {
        return "Invalid Purchase Order Operation";
    }
}