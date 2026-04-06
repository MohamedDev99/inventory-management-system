package com.moeware.ims.exception.staff.employee;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an employee operation violates a domain rule,
 * e.g. assigning an employee as their own manager.
 *
 * Replaces raw {@link IllegalArgumentException} throws in the service,
 * giving the handler a typed domain exception instead of a generic one.
 *
 * @author MoeWare Team
 */
public class InvalidEmployeeOperationException extends BaseAppException {

    public InvalidEmployeeOperationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorTitle() {
        return "Invalid Employee Operation";
    }
}