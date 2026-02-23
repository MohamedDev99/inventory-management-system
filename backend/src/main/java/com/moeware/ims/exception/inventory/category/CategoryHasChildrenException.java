package com.moeware.ims.exception.inventory.category;

/**
 * Exception thrown when attempting to delete a category that has child
 * categories
 */
public class CategoryHasChildrenException extends RuntimeException {

    private final Long categoryId;
    // private final long childCount;

    public CategoryHasChildrenException(Long categoryId) {
        super(String.format("Cannot delete category with id: %d. " +
                "Please delete or reassign child categories first.", categoryId));
        this.categoryId = categoryId;
        // this.childCount = childCount;
    }

    public CategoryHasChildrenException(String message) {
        super(message);
        this.categoryId = null;
        // this.childCount = 0;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    // public long getChildCount() {
    // return childCount;
    // }
}