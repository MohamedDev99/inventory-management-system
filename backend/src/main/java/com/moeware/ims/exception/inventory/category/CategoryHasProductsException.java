package com.moeware.ims.exception.inventory.category;

/**
 * Exception thrown when attempting to delete a category that has products
 */
public class CategoryHasProductsException extends RuntimeException {

    private final Long categoryId;
    private final long productCount;

    public CategoryHasProductsException(Long categoryId, long productCount) {
        super(String.format("Cannot delete category with id: %d. It has %d products. " +
                "Please reassign or remove all products first.", categoryId, productCount));
        this.categoryId = categoryId;
        this.productCount = productCount;
    }

    public CategoryHasProductsException(String message) {
        super(message);
        this.categoryId = null;
        this.productCount = 0;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public long getProductCount() {
        return productCount;
    }
}