package com.moeware.ims.exception.staff.warehouse;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to create or update a warehouse with a code or name
 * that already belongs to another warehouse.
 *
 * @author MoeWare Team
 */
public class WarehouseAlreadyExistsException extends BaseAppException {

    public enum ConflictField {
        CODE, NAME
    }

    private final String code;
    private final String name;

    public WarehouseAlreadyExistsException(ConflictField field, String value) {
        super(String.format("Warehouse already exists with %s: '%s'", field.name().toLowerCase(), value));
        this.code = field == ConflictField.CODE ? value : null;
        this.name = field == ConflictField.NAME ? value : null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Warehouse Already Exists";
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}