package com.moeware.ims.exception.transaction.invoice;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when any operation is attempted on an invoice that has already been
 * cancelled. CANCELLED is a terminal state — no further actions are permitted.
 *
 * @author MoeWare Team
 */
public class InvoiceAlreadyCancelledException extends BaseAppException {

    private final Long invoiceId;
    private final String invoiceNumber;

    public InvoiceAlreadyCancelledException(Long invoiceId, String invoiceNumber) {
        super(String.format(
                "Invoice %d (%s) has been cancelled and cannot be modified. "
                        + "Generate a new invoice if billing needs to continue.",
                invoiceId, invoiceNumber));
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Invoice Already Cancelled";
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }
}