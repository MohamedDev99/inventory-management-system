package com.moeware.ims.exception.inventory.category;

/**
 * Exception thrown when a parent category is not found during category creation
 * or update
 */
public class ParentCategoryNotFoundException extends RuntimeException {

    private final Long parentCategoryId;

    public ParentCategoryNotFoundException(Long parentCategoryId) {
        super("Parent category not found with id: " + parentCategoryId);
        this.parentCategoryId = parentCategoryId;
    }

    public ParentCategoryNotFoundException(String message) {
        super(message);
        this.parentCategoryId = null;
    }

    public ParentCategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.parentCategoryId = null;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }
}