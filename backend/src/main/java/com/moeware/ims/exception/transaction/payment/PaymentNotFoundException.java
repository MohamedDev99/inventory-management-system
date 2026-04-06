package com.moeware.ims.exception.transaction.payment;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a payment cannot be found by ID or payment number.
 *
 * @author MoeWare Team
 */
public class PaymentNotFoundException extends BaseAppException {

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

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Payment Not Found";
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }
}