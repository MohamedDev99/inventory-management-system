package com.moeware.ims.exception.staff.customer;

import java.math.BigDecimal;

/**
 * Exception thrown when a customer's credit limit would be exceeded
 */
public class CustomerCreditLimitExceededException extends RuntimeException {

    public CustomerCreditLimitExceededException(Long customerId, BigDecimal currentBalance,
            BigDecimal creditLimit, BigDecimal attemptedAmount) {
        super("Customer ID: " + customerId + " would exceed credit limit. " +
                "Current balance: $" + currentBalance + ", " +
                "Credit limit: $" + creditLimit + ", " +
                "Attempted amount: $" + attemptedAmount + ", " +
                "Available credit: $" + creditLimit.subtract(currentBalance));
    }

    public CustomerCreditLimitExceededException(String message) {
        super(message);
    }

    public CustomerCreditLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}