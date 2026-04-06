package com.moeware.ims.exception.inventory.category;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to create or update a category with a code or name
 * that already belongs to another category.
 *
 * Uses a {@link ConflictField} enum instead of a raw string field name
 * to eliminate typo risk and make call sites self-documenting.
 *
 * @author MoeWare Team
 */
public class CategoryAlreadyExistsException extends BaseAppException {

    public enum ConflictField {
        CODE, NAME
    }

    private final String code;
    private final String name;

    public CategoryAlreadyExistsException(ConflictField field, String value) {
        super(String.format("Category already exists with %s: '%s'", field.name().toLowerCase(), value));
        this.code = field == ConflictField.CODE ? value : null;
        this.name = field == ConflictField.NAME ? value : null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Category Already Exists";
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}