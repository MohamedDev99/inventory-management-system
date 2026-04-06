package com.moeware.ims.exception.transaction.invoice;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an invoice cannot be found by ID or invoice number.
 *
 * @author MoeWare Team
 */
public class InvoiceNotFoundException extends BaseAppException {

    private final Long invoiceId;
    private final String invoiceNumber;

    public InvoiceNotFoundException(Long invoiceId) {
        super("Invoice not found with ID: " + invoiceId);
        this.invoiceId = invoiceId;
        this.invoiceNumber = null;
    }

    public InvoiceNotFoundException(String invoiceNumber) {
        super("Invoice not found with number: " + invoiceNumber);
        this.invoiceId = null;
        this.invoiceNumber = invoiceNumber;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Invoice Not Found";
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }
}