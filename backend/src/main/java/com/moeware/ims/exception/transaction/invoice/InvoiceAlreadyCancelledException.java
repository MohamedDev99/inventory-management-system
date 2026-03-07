package com.moeware.ims.exception.transaction.invoice;

import lombok.Getter;

/**
 * Thrown when any operation (send, status update, payment recording) is
 * attempted
 * on an invoice that has already been cancelled.
 *
 * A CANCELLED invoice is a terminal state — no further actions are permitted on
 * it.
 * A new invoice must be generated if billing needs to continue.
 */
@Getter
public class InvoiceAlreadyCancelledException extends RuntimeException {

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
}