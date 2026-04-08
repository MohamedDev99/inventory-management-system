package com.moeware.ims.exception.transaction.inventoryMovement;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an {@code InventoryMovement} record cannot be found by its ID.
 *
 * @author MoeWare Team
 */
public class InventoryMovementNotFoundException extends BaseAppException {

    private final Long movementId;

    public InventoryMovementNotFoundException(Long movementId) {
        super(String.format("Inventory movement with id %d not found", movementId));
        this.movementId = movementId;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Inventory Movement Not Found";
    }

    public Long getMovementId() {
        return movementId;
    }
}