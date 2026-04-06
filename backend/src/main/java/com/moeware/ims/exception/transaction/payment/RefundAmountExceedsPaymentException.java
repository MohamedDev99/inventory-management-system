package com.moeware.ims.exception.transaction.payment;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a requested refund amount exceeds the original payment amount.
 * A refund can be full or partial, but never greater than what was paid.
 *
 * @author MoeWare Team
 */
public class RefundAmountExceedsPaymentException extends BaseAppException {

        private final Long paymentId;
        private final BigDecimal refundAmount;
        private final BigDecimal originalAmount;

        public RefundAmountExceedsPaymentException(Long paymentId, BigDecimal refundAmount,
                        BigDecimal originalAmount) {
                super(String.format(
                                "Refund amount (%s) for payment %d exceeds the original payment amount (%s). "
                                                + "Refunds cannot exceed the amount originally paid.",
                                refundAmount.toPlainString(), paymentId, originalAmount.toPlainString()));
                this.paymentId = paymentId;
                this.refundAmount = refundAmount;
                this.originalAmount = originalAmount;
        }

        @Override
        public HttpStatus getHttpStatus() {
                return HttpStatus.BAD_REQUEST;
        }

        @Override
        public String getErrorTitle() {
                return "Refund Amount Exceeds Payment";
        }

        public Long getPaymentId() {
                return paymentId;
        }

        public BigDecimal getRefundAmount() {
                return refundAmount;
        }

        public BigDecimal getOriginalAmount() {
                return originalAmount;
        }
}