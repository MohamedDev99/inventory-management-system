package com.moeware.ims.exception.transaction.payment;

import lombok.Getter;

@Getter
public class PaymentNotFoundException extends RuntimeException {

    private final Long paymentId;
    private final String paymentNumber;

    public PaymentNotFoundException(Long paymentId) {
        super("Payment not found with ID: " + paymentId);
        this.paymentId = paymentId;
        this.paymentNumber = null;
    }

    public PaymentNotFoundException(String paymentNumber) {
        super("Payment not found with number: " + paymentNumber);
        this.paymentId = null;
        this.paymentNumber = paymentNumber;
    }
}
