package com.moeware.ims.exception.auth;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a JWT token is invalid, expired, or cannot be parsed.
 *
 * @author MoeWare Team
 */
public class InvalidTokenException extends BaseAppException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getErrorTitle() {
        return "Invalid Token";
    }
}