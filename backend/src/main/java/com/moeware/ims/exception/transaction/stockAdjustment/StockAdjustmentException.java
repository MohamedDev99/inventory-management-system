package com.moeware.ims.exception.transaction.stockAdjustment;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a stock adjustment violates business rules —
 * e.g. applying an unapproved adjustment, invalid adjustment type, or reason.
 *
 * Currently not thrown in the service (TODO sites exist). Migrated to
 * {@link BaseAppException} so it is handler-ready when wired in.
 *
 * @author MoeWare Team
 */
public class StockAdjustmentException extends BaseAppException {

    public StockAdjustmentException(String message) {
        super(message);
    }

    public StockAdjustmentException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Stock Adjustment Error";
    }
}