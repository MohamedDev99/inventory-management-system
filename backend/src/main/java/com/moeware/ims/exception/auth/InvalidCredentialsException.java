package com.moeware.ims.exception.auth;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when login credentials are invalid or no authenticated
 * user is found in the security context.
 *
 * @author MoeWare Team
 */
public class InvalidCredentialsException extends BaseAppException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getErrorTitle() {
        return "Unauthorized";
    }
}