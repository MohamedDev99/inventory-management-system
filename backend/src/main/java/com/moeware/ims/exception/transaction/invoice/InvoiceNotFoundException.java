package com.moeware.ims.exception.transaction.invoice;

import lombok.Getter;

@Getter
public class InvoiceNotFoundException extends RuntimeException {

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
}