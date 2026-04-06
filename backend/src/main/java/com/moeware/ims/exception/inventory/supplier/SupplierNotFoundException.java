package com.moeware.ims.exception.inventory.supplier;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a supplier cannot be found by ID or code.
 *
 * @author MoeWare Team
 */
public class SupplierNotFoundException extends BaseAppException {

    private final Long supplierId;
    private final String code;

    public SupplierNotFoundException(Long supplierId) {
        super("Supplier not found with ID: " + supplierId);
        this.supplierId = supplierId;
        this.code = null;
    }

    public SupplierNotFoundException(String code) {
        super("Supplier not found with code: " + code);
        this.supplierId = null;
        this.code = code;
    }

    public SupplierNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.supplierId = null;
        this.code = null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Supplier Not Found";
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public String getCode() {
        return code;
    }
}