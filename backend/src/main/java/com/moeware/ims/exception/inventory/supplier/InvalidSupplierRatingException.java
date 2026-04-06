package com.moeware.ims.exception.inventory.supplier;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an invalid supplier rating value is provided (valid range: 1–5).
 * Currently unused in the service — migrated to BaseAppException so it is
 * handler-ready when it gets wired in.
 *
 * @author MoeWare Team
 */
public class InvalidSupplierRatingException extends BaseAppException {

    private final Integer rating;

    public InvalidSupplierRatingException(Integer rating) {
        super("Invalid supplier rating: " + rating + ". Rating must be between 1 and 5.");
        this.rating = rating;
    }

    public InvalidSupplierRatingException(String message) {
        super(message);
        this.rating = null;
    }

    public InvalidSupplierRatingException(String message, Throwable cause) {
        super(message, cause);
        this.rating = null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorTitle() {
        return "Invalid Supplier Rating";
    }

    public Integer getRating() {
        return rating;
    }
}