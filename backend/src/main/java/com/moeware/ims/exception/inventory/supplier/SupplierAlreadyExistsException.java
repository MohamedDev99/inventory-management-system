package com.moeware.ims.exception.inventory.supplier;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to create or update a supplier with a code or email
 * that already belongs to another supplier.
 *
 * @author MoeWare Team
 */
public class SupplierAlreadyExistsException extends BaseAppException {

    public enum ConflictField {
        CODE, EMAIL
    }

    private final ConflictField conflictField;
    private final String conflictValue;

    public SupplierAlreadyExistsException(ConflictField field, String value) {
        super(String.format("Supplier with %s '%s' already exists", field.name().toLowerCase(), value));
        this.conflictField = field;
        this.conflictValue = value;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Supplier Already Exists";
    }

    public ConflictField getConflictField() {
        return conflictField;
    }

    public String getConflictValue() {
        return conflictValue;
    }
}