package com.moeware.ims.exception.staff.customer;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a sales order would push a customer over their credit limit.
 *
 * @author MoeWare Team
 */
public class CustomerCreditLimitExceededException extends BaseAppException {

    private final Long customerId;
    private final BigDecimal currentBalance;
    private final BigDecimal creditLimit;
    private final BigDecimal attemptedAmount;
    private final BigDecimal availableCredit;

    public CustomerCreditLimitExceededException(Long customerId, BigDecimal currentBalance,
            BigDecimal creditLimit, BigDecimal attemptedAmount) {
        super(String.format(
                "Customer ID: %d would exceed credit limit. " +
                        "Current balance: $%s, Credit limit: $%s, " +
                        "Attempted amount: $%s, Available credit: $%s",
                customerId, currentBalance, creditLimit,
                attemptedAmount, creditLimit.subtract(currentBalance)));
        this.customerId = customerId;
        this.currentBalance = currentBalance;
        this.creditLimit = creditLimit;
        this.attemptedAmount = attemptedAmount;
        this.availableCredit = creditLimit.subtract(currentBalance);
    }

    public CustomerCreditLimitExceededException(String message, Throwable cause) {
        super(message, cause);
        this.customerId = null;
        this.currentBalance = null;
        this.creditLimit = null;
        this.attemptedAmount = null;
        this.availableCredit = null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Credit Limit Exceeded";
    }

    public Long getCustomerId() {
        return customerId;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public BigDecimal getAttemptedAmount() {
        return attemptedAmount;
    }

    public BigDecimal getAvailableCredit() {
        return availableCredit;
    }
}