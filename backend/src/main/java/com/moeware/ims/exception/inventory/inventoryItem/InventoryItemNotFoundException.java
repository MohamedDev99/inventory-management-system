package com.moeware.ims.exception.inventory.inventoryItem;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an inventory item cannot be found — either by its own ID,
 * or when a product has no stock record in a specific warehouse.
 *
 * @author MoeWare Team
 */
public class InventoryItemNotFoundException extends BaseAppException {

    private final Long inventoryItemId;
    private final Long productId;
    private final Long warehouseId;

    /** Lookup by inventory item's own primary key. */
    public InventoryItemNotFoundException(Long inventoryItemId) {
        super("Inventory item not found with id: " + inventoryItemId);
        this.inventoryItemId = inventoryItemId;
        this.productId = null;
        this.warehouseId = null;
    }

    /** Lookup by product + warehouse combination (no stock record exists). */
    public InventoryItemNotFoundException(Long productId, Long warehouseId) {
        super(String.format("No inventory record found for product id: %d in warehouse id: %d",
                productId, warehouseId));
        this.inventoryItemId = null;
        this.productId = productId;
        this.warehouseId = warehouseId;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Inventory Item Not Found";
    }

    public Long getInventoryItemId() {
        return inventoryItemId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }
}