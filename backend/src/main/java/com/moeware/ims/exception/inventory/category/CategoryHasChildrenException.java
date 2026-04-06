package com.moeware.ims.exception.inventory.category;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to delete a category that still has child categories.
 *
 * @author MoeWare Team
 */
public class CategoryHasChildrenException extends BaseAppException {

    private final Long categoryId;

    public CategoryHasChildrenException(Long categoryId) {
        super(String.format(
                "Cannot delete category with id: %d. Please delete or reassign child categories first.",
                categoryId));
        this.categoryId = categoryId;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Category Has Children";
    }

    public Long getCategoryId() {
        return categoryId;
    }
}