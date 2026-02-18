package com.moeware.ims.exception.transaction.inventoryMovement;

/**
 * Exception thrown when an inventory transfer violates business rules
 * Examples: transferring to same warehouse, invalid quantity, unauthorized
 * transfer
 */
public class InvalidTransferException extends RuntimeException {

    public InvalidTransferException(String message) {
        super(message);
    }

    public InvalidTransferException(String message, Throwable cause) {
        super(message, cause);
    }
}