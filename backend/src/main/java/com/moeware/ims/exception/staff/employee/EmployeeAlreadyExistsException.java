package com.moeware.ims.exception.staff.employee;

/**
 * Exception thrown when an employee with the same code or email already exists
 *
 * @author MoeWare Team
 */
public class EmployeeAlreadyExistsException extends RuntimeException {

    public EmployeeAlreadyExistsException(String message) {
        super(message);
    }

    public EmployeeAlreadyExistsException(String fieldName, String fieldValue) {
        super(String.format("Employee already exists with %s: '%s'", fieldName, fieldValue));
    }
}