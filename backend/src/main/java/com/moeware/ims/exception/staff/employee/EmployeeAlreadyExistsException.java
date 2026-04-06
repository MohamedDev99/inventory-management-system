package com.moeware.ims.exception.staff.employee;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an employee uniqueness constraint is violated.
 *
 * {@link ConflictField#EMPLOYEE_CODE} — duplicate employee code.
 * {@link ConflictField#EMAIL} — duplicate email address.
 * {@link ConflictField#USER_LINKED} — the target user account is already
 * linked to another employee.
 *
 * @author MoeWare Team
 */
public class EmployeeAlreadyExistsException extends BaseAppException {

    public enum ConflictField {
        EMPLOYEE_CODE, EMAIL, USER_LINKED
    }

    private final ConflictField conflictField;
    private final String conflictValue;

    public EmployeeAlreadyExistsException(ConflictField field, String value) {
        super(buildMessage(field, value));
        this.conflictField = field;
        this.conflictValue = value;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Employee Already Exists";
    }

    public ConflictField getConflictField() {
        return conflictField;
    }

    public String getConflictValue() {
        return conflictValue;
    }

    private static String buildMessage(ConflictField field, String value) {
        return switch (field) {
            case EMPLOYEE_CODE -> String.format("Employee already exists with employee_code: '%s'", value);
            case EMAIL -> String.format("Employee already exists with email: '%s'", value);
            case USER_LINKED -> "An employee is already linked to user id: " + value;
        };
    }
}