package com.moeware.ims.exception.inventory.category;

/**
 * Exception thrown when a requested category is not found
 * More specific than generic ResourceNotFoundException for category operations
 */
public class CategoryNotFoundException extends RuntimeException {

    private final Long categoryId;
    private final String code;

    public CategoryNotFoundException(Long categoryId) {
        super("Category not found with id: " + categoryId);
        this.categoryId = categoryId;
        this.code = null;
    }

    public CategoryNotFoundException(String code) {
        super("Category not found with code: " + code);
        this.categoryId = null;
        this.code = code;
    }

    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.categoryId = null;
        this.code = null;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCode() {
        return code;
    }
}