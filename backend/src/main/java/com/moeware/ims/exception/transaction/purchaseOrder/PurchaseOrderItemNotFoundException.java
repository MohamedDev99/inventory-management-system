package com.moeware.ims.exception.transaction.purchaseOrder;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a purchase order line item cannot be found,
 * e.g. when processing a receipt for an item that does not belong to the PO.
 *
 * @author MoeWare Team
 */
public class PurchaseOrderItemNotFoundException extends BaseAppException {

    private final Long itemId;
    private final Long purchaseOrderId;

    public PurchaseOrderItemNotFoundException(Long itemId, Long purchaseOrderId) {
        super(String.format("Purchase order item with id: %d not found in purchase order id: %d",
                itemId, purchaseOrderId));
        this.itemId = itemId;
        this.purchaseOrderId = purchaseOrderId;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Purchase Order Item Not Found";
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }
}