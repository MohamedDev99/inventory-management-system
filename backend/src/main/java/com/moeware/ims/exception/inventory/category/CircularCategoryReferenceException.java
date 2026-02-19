package com.moeware.ims.exception.inventory.category;

/**
 * Exception thrown when attempting to create a circular reference in category
 * hierarchy
 */
public class CircularCategoryReferenceException extends RuntimeException {

    private final Long categoryId;
    private final Long parentCategoryId;

    public CircularCategoryReferenceException(Long categoryId, Long parentCategoryId) {
        super(String.format("Cannot set category %d as parent of category %d. " +
                "This would create a circular reference in the category hierarchy.",
                parentCategoryId, categoryId));
        this.categoryId = categoryId;
        this.parentCategoryId = parentCategoryId;
    }

    public CircularCategoryReferenceException(String message) {
        super(message);
        this.categoryId = null;
        this.parentCategoryId = null;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }
}