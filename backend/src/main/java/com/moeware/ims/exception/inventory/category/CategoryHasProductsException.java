package com.moeware.ims.exception.inventory.category;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to delete a category that still has products assigned
 * to it.
 * Carries {@code productCount} so the response tells the client exactly how
 * many
 * products need to be reassigned first.
 *
 * @author MoeWare Team
 */
public class CategoryHasProductsException extends BaseAppException {

    private final Long categoryId;
    private final long productCount;

    public CategoryHasProductsException(Long categoryId, long productCount) {
        super(String.format(
                "Cannot delete category with id: %d. It has %d product(s). " +
                        "Please reassign or remove all products first.",
                categoryId, productCount));
        this.categoryId = categoryId;
        this.productCount = productCount;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Category Has Products";
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public long getProductCount() {
        return productCount;
    }
}