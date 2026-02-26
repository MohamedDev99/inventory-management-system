package com.moeware.ims.exception.transaction.invoice;

import java.math.BigDecimal;
import lombok.Getter;

/**
 * Thrown when a payment recorded against an invoice exceeds the remaining
 * balance due.
 *
 * Payments must be less than or equal to the current balance due.
 * Overpayments are not handled through the standard invoice payment flow —
 * they require a separate credit or refund process.
 */
@Getter
public class InvoicePaymentExceedsBalanceException extends RuntimeException {

    private final Long invoiceId;
    private final String invoiceNumber;
    private final BigDecimal paymentAmount;
    private final BigDecimal balanceDue;

    public InvoicePaymentExceedsBalanceException(Long invoiceId, String invoiceNumber,
            BigDecimal paymentAmount, BigDecimal balanceDue) {
        super(String.format(
                "Payment amount (%s) for invoice %d (%s) exceeds the remaining balance due (%s). "
                        + "Overpayments are not permitted through this endpoint.",
                paymentAmount.toPlainString(), invoiceId, invoiceNumber,
                balanceDue.toPlainString()));
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.paymentAmount = paymentAmount;
        this.balanceDue = balanceDue;
    }
}