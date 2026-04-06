package com.moeware.ims.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for all application-specific exceptions.
 * Each subclass declares its own HTTP status and error title,
 * allowing a single generic handler to replace dozens of duplicate handlers.
 *
 * @author MoeWare Team
 */
public abstract class BaseAppException extends RuntimeException {

    protected BaseAppException(String message) {
        super(message);
    }

    protected BaseAppException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * The HTTP status this exception should produce.
     */
    public abstract HttpStatus getHttpStatus();

    /**
     * Short human-readable error title (used as the "error" field in
     * ErrorResponse).
     */
    public abstract String getErrorTitle();
}