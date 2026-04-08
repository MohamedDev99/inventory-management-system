package com.moeware.ims.exception.transaction.stockAdjustment;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a {@code StockAdjustment} record cannot be found by its ID.
 *
 * @author MoeWare Team
 */
public class StockAdjustmentNotFoundException extends BaseAppException {

    private final Long adjustmentId;

    public StockAdjustmentNotFoundException(Long adjustmentId) {
        super(String.format("Stock adjustment with id %d not found", adjustmentId));
        this.adjustmentId = adjustmentId;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Stock Adjustment Not Found";
    }

    public Long getAdjustmentId() {
        return adjustmentId;
    }
}