package com.moeware.ims.exception.transaction.payment;

import lombok.Getter;

/**
 * Thrown when any status-changing operation is attempted on a payment
 * that has already been refunded.
 *
 * A REFUNDED payment is a terminal state — its status cannot be changed
 * and it cannot be refunded a second time.
 */
@Getter
public class PaymentAlreadyRefundedException extends RuntimeException {

    private final Long paymentId;
    private final String paymentNumber;

    public PaymentAlreadyRefundedException(Long paymentId, String paymentNumber) {
        super(String.format(
                "Payment %d (%s) has already been refunded and cannot be modified.",
                paymentId, paymentNumber));
        this.paymentId = paymentId;
        this.paymentNumber = paymentNumber;
    }
}