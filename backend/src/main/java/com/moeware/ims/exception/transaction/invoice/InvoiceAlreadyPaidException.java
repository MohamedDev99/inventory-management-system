package com.moeware.ims.exception.transaction.invoice;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a payment is recorded against an invoice that is already fully
 * paid, or when a send is attempted on a paid invoice.
 *
 * @author MoeWare Team
 */
public class InvoiceAlreadyPaidException extends BaseAppException {

    private final Long invoiceId;
    private final String invoiceNumber;

    public InvoiceAlreadyPaidException(Long invoiceId, String invoiceNumber) {
        super(String.format(
                "Invoice %d (%s) is already fully paid. "
                        + "No further payments can be recorded against it.",
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
        return "Invoice Already Paid";
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }
}