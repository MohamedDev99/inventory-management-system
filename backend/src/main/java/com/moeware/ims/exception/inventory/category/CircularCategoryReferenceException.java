package com.moeware.ims.exception.inventory.category;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a category update would create a circular reference in the
 * category hierarchy (e.g. setting a descendant as the new parent).
 *
 * @author MoeWare Team
 */
public class CircularCategoryReferenceException extends BaseAppException {

    private final Long categoryId;
    private final Long parentCategoryId;

    public CircularCategoryReferenceException(Long categoryId, Long parentCategoryId) {
        super(String.format(
                "Cannot set category %d as parent of category %d. " +
                        "This would create a circular reference in the category hierarchy.",
                parentCategoryId, categoryId));
        this.categoryId = categoryId;
        this.parentCategoryId = parentCategoryId;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorTitle() {
        return "Circular Category Reference";
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }
}