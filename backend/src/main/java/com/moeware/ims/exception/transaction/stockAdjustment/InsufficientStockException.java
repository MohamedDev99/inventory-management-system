package com.moeware.ims.exception.transaction.stockAdjustment;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an operation (transfer, adjustment, fulfilment) cannot proceed
 * because the available stock is less than the requested quantity.
 *
 * Carries structured fields so the handler can return a rich response body
 * without parsing the message string.
 *
 * @author MoeWare Team
 */
public class InsufficientStockException extends BaseAppException {

    private final Long productId;
    private final Long warehouseId;
    private final String warehouseName;
    private final int availableQuantity;
    private final int requestedQuantity;

    /**
     * Typed constructor — preferred for all new call sites.
     *
     * @param productId         ID of the product with insufficient stock
     * @param warehouseId       ID of the warehouse
     * @param warehouseName     Display name of the warehouse (for the message)
     * @param availableQuantity Current stock level
     * @param requestedQuantity Amount that was requested
     */
    public InsufficientStockException(Long productId, Long warehouseId, String warehouseName,
            int availableQuantity, int requestedQuantity) {
        super(String.format("Insufficient stock in %s. Available: %d, Requested: %d",
                warehouseName, availableQuantity, requestedQuantity));
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.availableQuantity = availableQuantity;
        this.requestedQuantity = requestedQuantity;
    }

    /**
     * Adjustment-context constructor — when there is no "warehouse" being
     * transferred from, only the product and the result.
     *
     * @param productId         ID of the product
     * @param warehouseId       ID of the warehouse where the adjustment is
     *                          happening
     * @param availableQuantity Current stock level
     * @param requestedQuantity Absolute quantity after adjustment (used to signal
     *                          the result would be negative)
     */
    public InsufficientStockException(Long productId, Long warehouseId,
            int availableQuantity, int requestedQuantity) {
        super(String.format(
                "Stock adjustment would result in negative stock. Available: %d, Resulting quantity: %d",
                availableQuantity, requestedQuantity));
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.warehouseName = null;
        this.availableQuantity = availableQuantity;
        this.requestedQuantity = requestedQuantity;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Insufficient Stock";
    }

    public Long getProductId() {
        return productId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }
}