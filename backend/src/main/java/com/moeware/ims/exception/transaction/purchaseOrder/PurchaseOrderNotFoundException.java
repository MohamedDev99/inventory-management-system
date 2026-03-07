package com.moeware.ims.exception.transaction.purchaseOrder;

/**
 * Exception thrown when a purchase order is not found
 */
public class PurchaseOrderNotFoundException extends RuntimeException {

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

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public String getPoNumber() {
        return poNumber;
    }
}