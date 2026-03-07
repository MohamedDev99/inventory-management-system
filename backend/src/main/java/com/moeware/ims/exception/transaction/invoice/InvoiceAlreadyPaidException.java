package com.moeware.ims.exception.transaction.invoice;

import lombok.Getter;

/**
 * Thrown when a payment is recorded against an invoice that is already fully
 * paid,
 * or when a send is attempted on a paid invoice.
 *
 * Once an invoice reaches PAID status its balance is zero — further payments
 * would result in an overpayment which requires a dedicated credit/refund flow
 * instead.
 */
@Getter
public class InvoiceAlreadyPaidException extends RuntimeException {

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
}