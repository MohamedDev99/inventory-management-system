package com.moeware.ims.exception.staff.warehouse;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to hard-delete a warehouse that still has inventory
 * items.
 *
 * @author MoeWare Team
 */
public class WarehouseHasInventoryException extends BaseAppException {

    private final Long warehouseId;
    private final int inventoryItemCount;

    public WarehouseHasInventoryException(Long warehouseId, int inventoryItemCount) {
        super(String.format(
                "Cannot delete warehouse with id: %d. It has %d inventory item(s). " +
                        "Please transfer or remove all inventory first.",
                warehouseId, inventoryItemCount));
        this.warehouseId = warehouseId;
        this.inventoryItemCount = inventoryItemCount;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Warehouse Has Inventory";
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public int getInventoryItemCount() {
        return inventoryItemCount;
    }
}