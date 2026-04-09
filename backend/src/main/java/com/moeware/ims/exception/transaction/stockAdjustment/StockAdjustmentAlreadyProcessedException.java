package com.moeware.ims.exception.transaction.stockAdjustment;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;
import com.moeware.ims.enums.transaction.StockAdjustmentStatus;

/**
 * Thrown when an approve or reject action is attempted on a stock adjustment
 * that has already been processed (status is APPROVED or REJECTED).
 *
 * @author MoeWare Team
 */
public class StockAdjustmentAlreadyProcessedException extends BaseAppException {

    private final Long adjustmentId;
    private final StockAdjustmentStatus currentStatus;

    public StockAdjustmentAlreadyProcessedException(Long adjustmentId, StockAdjustmentStatus currentStatus) {
        super(String.format(
                "Stock adjustment with id %d has already been processed with status '%s' and cannot be modified",
                adjustmentId, currentStatus));
        this.adjustmentId = adjustmentId;
        this.currentStatus = currentStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Stock Adjustment Already Processed";
    }

    public Long getAdjustmentId() {
        return adjustmentId;
    }

    public StockAdjustmentStatus getCurrentStatus() {
        return currentStatus;
    }
}