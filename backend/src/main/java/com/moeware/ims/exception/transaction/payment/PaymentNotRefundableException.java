package com.moeware.ims.exception.transaction.payment;

import com.moeware.ims.enums.transaction.PaymentStatus;
import lombok.Getter;

/**
 * Thrown when a refund is attempted on a payment that is not in COMPLETED
 * status.
 *
 * Only COMPLETED payments can be refunded. PENDING payments should be voided
 * instead,
 * and FAILED payments have never settled so there is nothing to return.
 */
@Getter
public class PaymentNotRefundableException extends RuntimeException {

    private final Long paymentId;
    private final PaymentStatus currentStatus;

    public PaymentNotRefundableException(Long paymentId, PaymentStatus currentStatus) {
        super(String.format(
                "Payment %d cannot be refunded — only COMPLETED payments are eligible for refund, "
                        + "but current status is: %s.",
                paymentId, currentStatus));
        this.paymentId = paymentId;
        this.currentStatus = currentStatus;
    }
}