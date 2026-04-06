package com.moeware.ims.exception.inventory.supplier;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to perform an operation on a supplier that is
 * inactive.
 * Currently unused in the service — migrated to BaseAppException so it is
 * handler-ready when it gets wired in.
 *
 * @author MoeWare Team
 */
public class InactiveSupplierException extends BaseAppException {

    public InactiveSupplierException(Long supplierId) {
        super("Supplier with ID: " + supplierId + " is inactive. Cannot perform this operation.");
    }

    public InactiveSupplierException(String supplierCode) {
        super("Supplier with code: " + supplierCode + " is inactive. Cannot perform this operation.");
    }

    public InactiveSupplierException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Supplier Inactive";
    }
}