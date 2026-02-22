package com.moeware.ims.exception.transaction.payment;

import java.math.BigDecimal;
import lombok.Getter;

/**
 * Thrown when a requested refund amount exceeds the original payment amount.
 *
 * A refund can be full (equal to original) or partial (less than original),
 * but it can never be greater than what was originally paid.
 */
@Getter
public class RefundAmountExceedsPaymentException extends RuntimeException {

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
}