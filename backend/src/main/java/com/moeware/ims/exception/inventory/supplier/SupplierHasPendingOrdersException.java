package com.moeware.ims.exception.inventory.supplier;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to delete a supplier that has pending purchase orders.
 * Currently unused in the service (noted as TODO) — migrated to
 * BaseAppException
 * so it is handler-ready when it gets wired in.
 *
 * @author MoeWare Team
 */
public class SupplierHasPendingOrdersException extends BaseAppException {

    private final Long supplierId;
    private final int pendingOrderCount;

    public SupplierHasPendingOrdersException(Long supplierId) {
        super("Cannot delete supplier with ID: " + supplierId + ". Supplier has pending purchase orders.");
        this.supplierId = supplierId;
        this.pendingOrderCount = 0;
    }

    public SupplierHasPendingOrdersException(Long supplierId, int pendingOrderCount) {
        super(String.format(
                "Cannot delete supplier with ID: %d. Supplier has %d pending purchase order(s).",
                supplierId, pendingOrderCount));
        this.supplierId = supplierId;
        this.pendingOrderCount = pendingOrderCount;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Supplier Has Pending Orders";
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public int getPendingOrderCount() {
        return pendingOrderCount;
    }
}