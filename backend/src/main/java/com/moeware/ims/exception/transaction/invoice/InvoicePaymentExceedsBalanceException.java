package com.moeware.ims.exception.transaction.invoice;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a payment recorded against an invoice exceeds the remaining
 * balance due. Overpayments require a separate credit or refund process.
 *
 * @author MoeWare Team
 */
public class InvoicePaymentExceedsBalanceException extends BaseAppException {

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

        @Override
        public HttpStatus getHttpStatus() {
                return HttpStatus.BAD_REQUEST;
        }

        @Override
        public String getErrorTitle() {
                return "Invoice Payment Exceeds Balance";
        }

        public Long getInvoiceId() {
                return invoiceId;
        }

        public String getInvoiceNumber() {
                return invoiceNumber;
        }

        public BigDecimal getPaymentAmount() {
                return paymentAmount;
        }

        public BigDecimal getBalanceDue() {
                return balanceDue;
        }
}