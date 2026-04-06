package com.moeware.ims.exception.inventory.category;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a parent category referenced during category creation or update
 * does not exist.
 *
 * Kept separate from {@link CategoryNotFoundException} so the handler and the
 * API consumer can distinguish "the category you're looking for" from
 * "the parent you tried to assign".
 *
 * @author MoeWare Team
 */
public class ParentCategoryNotFoundException extends BaseAppException {

    private final Long parentCategoryId;

    public ParentCategoryNotFoundException(Long parentCategoryId) {
        super("Parent category not found with id: " + parentCategoryId);
        this.parentCategoryId = parentCategoryId;
    }

    public ParentCategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.parentCategoryId = null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Parent Category Not Found";
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }
}