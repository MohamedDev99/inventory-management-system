package com.moeware.ims.exception.handler.staff;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.staff.customer.CustomerAlreadyExistsException;
import com.moeware.ims.exception.staff.customer.CustomerCreditLimitExceededException;
import com.moeware.ims.exception.staff.customer.CustomerNotFoundException;
import com.moeware.ims.exception.staff.customer.InactiveCustomerException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all customer-domain exceptions.
 *
 * Dedicated handlers for exceptions with extra response fields:
 * {@link CustomerNotFoundException}, {@link CustomerAlreadyExistsException},
 * {@link CustomerCreditLimitExceededException},
 * {@link InactiveCustomerException}.
 *
 * Covered automatically by the generic handler in {@link UserExceptionHandler}:
 * {@link CustomerHasPendingOrdersException} and
 * {@link InvalidCustomerTypeException}
 * — both extend {@link BaseAppException} and return a standard ErrorResponse.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class CustomerExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<CustomerNotFoundErrorResponse> handleCustomerNotFound(
            CustomerNotFoundException ex, WebRequest request) {

        log.error("Customer not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                CustomerNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .customerId(ex.getCustomerId())
                        .customerCode(ex.getCustomerCode())
                        .build());
    }

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<CustomerConflictErrorResponse> handleCustomerAlreadyExists(
            CustomerAlreadyExistsException ex, WebRequest request) {

        log.error("Customer conflict: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                CustomerConflictErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .conflictField(ex.getConflictField().name().toLowerCase())
                        .conflictValue(ex.getConflictValue())
                        .build());
    }

    @ExceptionHandler(CustomerCreditLimitExceededException.class)
    public ResponseEntity<CreditLimitErrorResponse> handleCreditLimitExceeded(
            CustomerCreditLimitExceededException ex, WebRequest request) {

        log.error("Credit limit exceeded for customer: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                CreditLimitErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .customerId(ex.getCustomerId())
                        .currentBalance(ex.getCurrentBalance())
                        .creditLimit(ex.getCreditLimit())
                        .attemptedAmount(ex.getAttemptedAmount())
                        .availableCredit(ex.getAvailableCredit())
                        .build());
    }

    @ExceptionHandler(InactiveCustomerException.class)
    public ResponseEntity<InactiveCustomerErrorResponse> handleInactiveCustomer(
            InactiveCustomerException ex, WebRequest request) {

        log.error("Operation on inactive customer: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                InactiveCustomerErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .customerId(ex.getCustomerId())
                        .customerCode(ex.getCustomerCode())
                        .build());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    // ── Response classes ──────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Customer not found error response")
    public static class CustomerNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Customer ID that was not found", example = "12")
        private Long customerId;
        @Schema(description = "Customer code that was not found", example = "CUST-001")
        private String customerCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Customer conflict error response")
    public static class CustomerConflictErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Field that caused the conflict", example = "email", allowableValues = { "code",
                "email" })
        private String conflictField;
        @Schema(description = "Value that caused the conflict", example = "john@example.com")
        private String conflictValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Credit limit exceeded error response")
    public static class CreditLimitErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Customer ID", example = "12")
        private Long customerId;
        @Schema(description = "Current outstanding balance", example = "4500.00")
        private BigDecimal currentBalance;
        @Schema(description = "Approved credit limit", example = "5000.00")
        private BigDecimal creditLimit;
        @Schema(description = "Amount that was attempted", example = "800.00")
        private BigDecimal attemptedAmount;
        @Schema(description = "Remaining available credit", example = "500.00")
        private BigDecimal availableCredit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Inactive customer error response")
    public static class InactiveCustomerErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Customer ID that is inactive", example = "12")
        private Long customerId;
        @Schema(description = "Customer code that is inactive", example = "CUST-001")
        private String customerCode;
    }
}