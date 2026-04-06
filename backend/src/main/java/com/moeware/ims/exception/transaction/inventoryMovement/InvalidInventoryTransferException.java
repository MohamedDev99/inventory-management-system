package com.moeware.ims.exception.transaction.inventoryMovement;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an inventory transfer request violates a domain rule,
 * e.g. source and destination warehouse are the same.
 *
 * @author MoeWare Team
 */
public class InvalidInventoryTransferException extends BaseAppException {

    private final Long fromWarehouseId;
    private final Long toWarehouseId;

    public InvalidInventoryTransferException(Long fromWarehouseId, Long toWarehouseId) {
        super(String.format(
                "Cannot transfer inventory: source and destination warehouse are the same (id: %d).",
                fromWarehouseId));
        this.fromWarehouseId = fromWarehouseId;
        this.toWarehouseId = toWarehouseId;
    }

    public InvalidInventoryTransferException(String message) {
        super(message);
        this.fromWarehouseId = null;
        this.toWarehouseId = null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorTitle() {
        return "Invalid Inventory Transfer";
    }

    public Long getFromWarehouseId() {
        return fromWarehouseId;
    }

    public Long getToWarehouseId() {
        return toWarehouseId;
    }
}