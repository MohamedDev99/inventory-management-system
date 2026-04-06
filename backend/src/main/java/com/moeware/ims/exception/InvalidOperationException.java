package com.moeware.ims.exception;

import org.springframework.http.HttpStatus;

/**
 * Generic fallback for invalid business operations that don't belong to a more
 * specific domain exception. Prefer a domain-specific exception (e.g.,
 * {@link com.moeware.ims.exception.staff.employee.InvalidEmployeeOperationException})
 * wherever possible — use this only when no typed alternative exists.
 *
 * @author MoeWare Team
 */
public class InvalidOperationException extends BaseAppException {

    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpStatus() { return HttpStatus.BAD_REQUEST; }

    @Override
    public String getErrorTitle() { return "Invalid Operation"; }
}