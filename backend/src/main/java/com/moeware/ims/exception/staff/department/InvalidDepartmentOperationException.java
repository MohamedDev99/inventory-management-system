package com.moeware.ims.exception.staff.department;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a department operation violates a domain rule,
 * e.g. assigning a department as its own parent.
 *
 * @author MoeWare Team
 */
public class InvalidDepartmentOperationException extends BaseAppException {

    public InvalidDepartmentOperationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorTitle() {
        return "Invalid Department Operation";
    }
}