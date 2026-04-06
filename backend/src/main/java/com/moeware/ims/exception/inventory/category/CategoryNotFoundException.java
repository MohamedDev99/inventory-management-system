package com.moeware.ims.exception.inventory.category;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a category cannot be found by ID or code.
 *
 * @author MoeWare Team
 */
public class CategoryNotFoundException extends BaseAppException {

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

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Category Not Found";
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCode() {
        return code;
    }
}