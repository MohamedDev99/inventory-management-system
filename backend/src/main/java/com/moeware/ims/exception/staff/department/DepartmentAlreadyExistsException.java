package com.moeware.ims.exception.staff.department;

/**
 * Exception thrown when a department with the same code or name already exists
 *
 * @author MoeWare Team
 */
public class DepartmentAlreadyExistsException extends RuntimeException {

    public DepartmentAlreadyExistsException(String message) {
        super(message);
    }

    public DepartmentAlreadyExistsException(String fieldName, String fieldValue) {
        super(String.format("Department already exists with %s: '%s'", fieldName, fieldValue));
    }
}