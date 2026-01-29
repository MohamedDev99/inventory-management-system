package com.moeware.ims.exception.user;

/**
 * Exception thrown when attempting to create a user that already exists
 *
 * @author MoeWare Team
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}