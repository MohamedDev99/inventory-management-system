package com.moeware.ims.exception.transaction.payment;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;
import com.moeware.ims.enums.transaction.PaymentStatus;

/**
 * Thrown when a refund is attempted on a payment that is not in COMPLETED
 * status. Only COMPLETED payments are eligible for refund.
 *
 * @author MoeWare Team
 */
public class PaymentNotRefundableException extends BaseAppException {

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

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Payment Not Refundable";
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public PaymentStatus getCurrentStatus() {
        return currentStatus;
    }
}