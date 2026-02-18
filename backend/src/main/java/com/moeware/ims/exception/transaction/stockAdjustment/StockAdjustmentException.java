package com.moeware.ims.exception.transaction.stockAdjustment;

/**
 * Exception thrown when a stock adjustment violates business rules
 * Examples: adjustment would cause negative stock, unapproved adjustment
 * application,
 * invalid adjustment type or reason
 */
public class StockAdjustmentException extends RuntimeException {

    public StockAdjustmentException(String message) {
        super(message);
    }

    public StockAdjustmentException(String message, Throwable cause) {
        super(message, cause);
    }
}