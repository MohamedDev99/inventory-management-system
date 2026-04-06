package com.moeware.ims.exception.transaction.purchaseOrder;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a purchase order receipt would exceed the ordered quantity
 * for one or more line items.
 *
 * @author MoeWare Team
 */
public class PurchaseOrderReceiptException extends BaseAppException {

    private final Long itemId;
    private final int alreadyReceived;
    private final int newReceiptQuantity;
    private final int quantityOrdered;

    public PurchaseOrderReceiptException(Long itemId, int alreadyReceived, int newReceiptQuantity,
            int quantityOrdered) {
        super(String.format(
                "Total received quantity (%d) exceeds ordered quantity (%d) for PO item id: %d.",
                alreadyReceived + newReceiptQuantity, quantityOrdered, itemId));
        this.itemId = itemId;
        this.alreadyReceived = alreadyReceived;
        this.newReceiptQuantity = newReceiptQuantity;
        this.quantityOrdered = quantityOrdered;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorTitle() {
        return "Purchase Order Receipt Error";
    }

    public Long getItemId() {
        return itemId;
    }

    public int getAlreadyReceived() {
        return alreadyReceived;
    }

    public int getNewReceiptQuantity() {
        return newReceiptQuantity;
    }

    public int getQuantityOrdered() {
        return quantityOrdered;
    }

    public int getTotalReceived() {
        return alreadyReceived + newReceiptQuantity;
    }
}