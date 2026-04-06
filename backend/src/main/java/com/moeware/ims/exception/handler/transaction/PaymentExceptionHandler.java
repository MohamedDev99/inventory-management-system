package com.moeware.ims.exception.handler.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.transaction.payment.PaymentAlreadyRefundedException;
import com.moeware.ims.exception.transaction.payment.PaymentNotFoundException;
import com.moeware.ims.exception.transaction.payment.PaymentNotRefundableException;
import com.moeware.ims.exception.transaction.payment.RefundAmountExceedsPaymentException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all payment-domain exceptions.
 * All four exceptions carry structured fields and get dedicated handler
 * methods.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class PaymentExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<PaymentNotFoundErrorResponse> handlePaymentNotFound(
            PaymentNotFoundException ex, WebRequest request) {

        log.error("Payment not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                PaymentNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .paymentId(ex.getPaymentId())
                        .paymentNumber(ex.getPaymentNumber())
                        .build());
    }

    @ExceptionHandler(PaymentAlreadyRefundedException.class)
    public ResponseEntity<PaymentAlreadyRefundedErrorResponse> handlePaymentAlreadyRefunded(
            PaymentAlreadyRefundedException ex, WebRequest request) {

        log.error("Payment already refunded: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                PaymentAlreadyRefundedErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .paymentId(ex.getPaymentId())
                        .paymentNumber(ex.getPaymentNumber())
                        .build());
    }

    @ExceptionHandler(PaymentNotRefundableException.class)
    public ResponseEntity<PaymentNotRefundableErrorResponse> handlePaymentNotRefundable(
            PaymentNotRefundableException ex, WebRequest request) {

        log.error("Payment not refundable: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                PaymentNotRefundableErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .paymentId(ex.getPaymentId())
                        .currentStatus(ex.getCurrentStatus().name())
                        .build());
    }

    @ExceptionHandler(RefundAmountExceedsPaymentException.class)
    public ResponseEntity<RefundAmountExceedsErrorResponse> handleRefundAmountExceeds(
            RefundAmountExceedsPaymentException ex, WebRequest request) {

        log.error("Refund amount exceeds payment: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                RefundAmountExceedsErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .paymentId(ex.getPaymentId())
                        .refundAmount(ex.getRefundAmount())
                        .originalAmount(ex.getOriginalAmount())
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
    @Schema(description = "Payment not found error response")
    public static class PaymentNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Payment ID that was not found", example = "18")
        private Long paymentId;
        @Schema(description = "Payment number that was not found", example = "PAY-20250101-0001")
        private String paymentNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Payment already refunded error response")
    public static class PaymentAlreadyRefundedErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Payment ID", example = "18")
        private Long paymentId;
        @Schema(description = "Payment number", example = "PAY-20250101-0001")
        private String paymentNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Payment not refundable error response")
    public static class PaymentNotRefundableErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Payment ID", example = "18")
        private Long paymentId;
        @Schema(description = "Current status that prevents refund", example = "PENDING")
        private String currentStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Refund amount exceeds payment error response")
    public static class RefundAmountExceedsErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Payment ID", example = "18")
        private Long paymentId;
        @Schema(description = "Refund amount that was requested", example = "350.00")
        private BigDecimal refundAmount;
        @Schema(description = "Original payment amount", example = "200.00")
        private BigDecimal originalAmount;
    }
}