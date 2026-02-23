package com.moeware.ims.exception.staff.warehouse;

/**
 * Exception thrown when attempting to delete a warehouse that has inventory
 * items
 */
public class WarehouseHasInventoryException extends RuntimeException {

    private final Long warehouseId;
    private final int inventoryItemCount;

    public WarehouseHasInventoryException(Long warehouseId, int inventoryItemCount) {
        super(String.format("Cannot delete warehouse with id: %d. It has %d inventory items. " +
                "Please transfer or remove all inventory first.", warehouseId, inventoryItemCount));
        this.warehouseId = warehouseId;
        this.inventoryItemCount = inventoryItemCount;
    }

    public WarehouseHasInventoryException(String message) {
        super(message);
        this.warehouseId = null;
        this.inventoryItemCount = 0;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public int getInventoryItemCount() {
        return inventoryItemCount;
    }
}