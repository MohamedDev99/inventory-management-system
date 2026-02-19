package com.moeware.ims.exception.inventory.category;

/**
 * Exception thrown when attempting to create a category that already exists
 */
public class CategoryAlreadyExistsException extends RuntimeException {

    private final Long categoryId;
    private final String code;
    private final String name;

    public CategoryAlreadyExistsException(Long categoryId) {
        super("Category already exists with id: " + categoryId);
        this.categoryId = categoryId;
        this.code = null;
        this.name = null;
    }

    public CategoryAlreadyExistsException(String fieldName, String fieldValue) {
        super(String.format("Category already exists with %s: '%s'", fieldName, fieldValue));
        if ("code".equalsIgnoreCase(fieldName)) {
            this.code = fieldValue;
            this.name = null;
        } else if ("name".equalsIgnoreCase(fieldName)) {
            this.name = fieldValue;
            this.code = null;
        } else {
            this.code = null;
            this.name = null;
        }

        this.categoryId = null;
    }

    public CategoryAlreadyExistsException(String message) {
        super(message);
        this.code = null;
        this.name = null;
        this.categoryId = null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Long getCategoryId() {
        return categoryId;
    }
}