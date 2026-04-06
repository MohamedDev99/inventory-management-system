package com.moeware.ims.exception.transaction.payment;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when any status-changing operation is attempted on a payment that has
 * already been refunded. REFUNDED is a terminal state — it cannot be changed
 * and cannot be refunded a second time.
 *
 * @author MoeWare Team
 */
public class PaymentAlreadyRefundedException extends BaseAppException {

    private final Long paymentId;
    private final String paymentNumber;

    public PaymentAlreadyRefundedException(Long paymentId, String paymentNumber) {
        super(String.format(
                "Payment %d (%s) has already been refunded and cannot be modified.",
                paymentId, paymentNumber));
        this.paymentId = paymentId;
        this.paymentNumber = paymentNumber;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Payment Already Refunded";
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }
}