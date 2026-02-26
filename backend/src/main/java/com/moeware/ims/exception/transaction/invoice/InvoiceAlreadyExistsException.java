package com.moeware.ims.exception.transaction.invoice;

import lombok.Getter;

/**
 * Thrown when an invoice is generated for a sales order that already has one.
 *
 * Each sales order may have at most one invoice. If a correction is needed,
 * the existing invoice should be cancelled and a new one generated.
 */
@Getter
public class InvoiceAlreadyExistsException extends RuntimeException {

    private final Long salesOrderId;
    private final String soNumber;
    private final String existingInvoiceNumber;

    public InvoiceAlreadyExistsException(Long salesOrderId, String soNumber,
            String existingInvoiceNumber) {
        super(String.format(
                "Sales order '%s' already has an invoice (%s). "
                        + "Cancel the existing invoice before generating a new one.",
                soNumber, existingInvoiceNumber));
        this.salesOrderId = salesOrderId;
        this.soNumber = soNumber;
        this.existingInvoiceNumber = existingInvoiceNumber;
    }
}