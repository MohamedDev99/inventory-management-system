package com.moeware.ims.exception.auth;

/**
 * Exception thrown when JWT token is invalid or expired
 *
 * @author MoeWare Team
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}