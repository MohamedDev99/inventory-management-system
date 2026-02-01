package com.moeware.ims.exception.auth;

/**
 * Exception thrown when login credentials are invalid
 *
 * @author MoeWare Team
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}