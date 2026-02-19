package com.moeware.ims.exception.inventory.supplier;

/**
 * Exception thrown when an invalid supplier rating is provided
 */
public class InvalidSupplierRatingException extends RuntimeException {

    public InvalidSupplierRatingException(Integer rating) {
        super("Invalid supplier rating: " + rating + ". Rating must be between 1 and 5.");
    }

    public InvalidSupplierRatingException(String message) {
        super(message);
    }

    public InvalidSupplierRatingException(String message, Throwable cause) {
        super(message, cause);
    }
}