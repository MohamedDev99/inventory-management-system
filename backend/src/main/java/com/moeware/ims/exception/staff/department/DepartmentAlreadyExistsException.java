package com.moeware.ims.exception.staff.department;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a department uniqueness constraint is violated.
 *
 * @author MoeWare Team
 */
public class DepartmentAlreadyExistsException extends BaseAppException {

    public enum ConflictField {
        DEPARTMENT_CODE, NAME
    }

    private final ConflictField conflictField;
    private final String conflictValue;

    public DepartmentAlreadyExistsException(ConflictField field, String value) {
        super(String.format("Department already exists with %s: '%s'", field.name().toLowerCase(), value));
        this.conflictField = field;
        this.conflictValue = value;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Department Already Exists";
    }

    public ConflictField getConflictField() {
        return conflictField;
    }

    public String getConflictValue() {
        return conflictValue;
    }
}