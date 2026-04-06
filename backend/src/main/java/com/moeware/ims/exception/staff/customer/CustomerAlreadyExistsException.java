package com.moeware.ims.exception.staff.customer;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to create or update a customer with a code or email
 * that already belongs to another customer.
 *
 * @author MoeWare Team
 */
public class CustomerAlreadyExistsException extends BaseAppException {

    public enum ConflictField {
        CODE, EMAIL
    }

    private final ConflictField conflictField;
    private final String conflictValue;

    public CustomerAlreadyExistsException(ConflictField field, String value) {
        super(String.format("Customer with %s '%s' already exists", field.name().toLowerCase(), value));
        this.conflictField = field;
        this.conflictValue = value;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Customer Already Exists";
    }

    public ConflictField getConflictField() {
        return conflictField;
    }

    public String getConflictValue() {
        return conflictValue;
    }
}