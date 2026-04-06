package com.moeware.ims.exception.transaction.purchaseOrder;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a purchase order cannot be found by ID or PO number.
 *
 * @author MoeWare Team
 */
public class PurchaseOrderNotFoundException extends BaseAppException {

    private final Long purchaseOrderId;
    private final String poNumber;

    public PurchaseOrderNotFoundException(Long id) {
        super("Purchase order not found with id: " + id);
        this.purchaseOrderId = id;
        this.poNumber = null;
    }

    public PurchaseOrderNotFoundException(String poNumber) {
        super("Purchase order not found with PO number: " + poNumber);
        this.purchaseOrderId = null;
        this.poNumber = poNumber;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Purchase Order Not Found";
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public String getPoNumber() {
        return poNumber;
    }
}