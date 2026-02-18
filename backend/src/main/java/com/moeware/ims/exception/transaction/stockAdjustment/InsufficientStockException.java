package com.moeware.ims.exception.transaction.stockAdjustment;

/**
 * Exception thrown when attempting to deduct more stock than available
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}