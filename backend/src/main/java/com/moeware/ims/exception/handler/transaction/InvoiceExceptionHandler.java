package com.moeware.ims.exception.handler.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.transaction.invoice.InvoiceAlreadyCancelledException;
import com.moeware.ims.exception.transaction.invoice.InvoiceAlreadyExistsException;
import com.moeware.ims.exception.transaction.invoice.InvoiceAlreadyPaidException;
import com.moeware.ims.exception.transaction.invoice.InvoiceNotFoundException;
import com.moeware.ims.exception.transaction.invoice.InvoicePaymentExceedsBalanceException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all invoice-domain exceptions.
 * All five exceptions carry structured fields and get dedicated handler
 * methods.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class InvoiceExceptionHandler {

    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<InvoiceNotFoundErrorResponse> handleInvoiceNotFound(
            InvoiceNotFoundException ex, WebRequest request) {

        log.error("Invoice not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                InvoiceNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .invoiceId(ex.getInvoiceId())
                        .invoiceNumber(ex.getInvoiceNumber())
                        .build());
    }

    @ExceptionHandler(InvoiceAlreadyExistsException.class)
    public ResponseEntity<InvoiceAlreadyExistsErrorResponse> handleInvoiceAlreadyExists(
            InvoiceAlreadyExistsException ex, WebRequest request) {

        log.error("Invoice already exists: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                InvoiceAlreadyExistsErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .salesOrderId(ex.getSalesOrderId())
                        .soNumber(ex.getSoNumber())
                        .existingInvoiceNumber(ex.getExistingInvoiceNumber())
                        .build());
    }

    @ExceptionHandler(InvoiceAlreadyCancelledException.class)
    public ResponseEntity<InvoiceTerminalStateErrorResponse> handleInvoiceAlreadyCancelled(
            InvoiceAlreadyCancelledException ex, WebRequest request) {

        log.error("Invoice already cancelled: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                InvoiceTerminalStateErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .invoiceId(ex.getInvoiceId())
                        .invoiceNumber(ex.getInvoiceNumber())
                        .build());
    }

    @ExceptionHandler(InvoiceAlreadyPaidException.class)
    public ResponseEntity<InvoiceTerminalStateErrorResponse> handleInvoiceAlreadyPaid(
            InvoiceAlreadyPaidException ex, WebRequest request) {

        log.error("Invoice already paid: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                InvoiceTerminalStateErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .invoiceId(ex.getInvoiceId())
                        .invoiceNumber(ex.getInvoiceNumber())
                        .build());
    }

    @ExceptionHandler(InvoicePaymentExceedsBalanceException.class)
    public ResponseEntity<InvoicePaymentExceedsErrorResponse> handleInvoicePaymentExceedsBalance(
            InvoicePaymentExceedsBalanceException ex, WebRequest request) {

        log.error("Invoice payment exceeds balance: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                InvoicePaymentExceedsErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .invoiceId(ex.getInvoiceId())
                        .invoiceNumber(ex.getInvoiceNumber())
                        .paymentAmount(ex.getPaymentAmount())
                        .balanceDue(ex.getBalanceDue())
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
    @Schema(description = "Invoice not found error response")
    public static class InvoiceNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Invoice ID that was not found", example = "22")
        private Long invoiceId;
        @Schema(description = "Invoice number that was not found", example = "INV-20250101-0001")
        private String invoiceNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Invoice already exists for this sales order error response")
    public static class InvoiceAlreadyExistsErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Sales order ID", example = "55")
        private Long salesOrderId;
        @Schema(description = "SO number", example = "SO-20250101-0001")
        private String soNumber;
        @Schema(description = "The invoice that already exists for this SO", example = "INV-20250101-0001")
        private String existingInvoiceNumber;
    }

    /**
     * Shared response body for CANCELLED and PAID terminal-state exceptions.
     * Both carry the same fields so one response class covers both.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Invoice terminal state error response (cancelled or paid)")
    public static class InvoiceTerminalStateErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Invoice ID", example = "22")
        private Long invoiceId;
        @Schema(description = "Invoice number", example = "INV-20250101-0001")
        private String invoiceNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Invoice payment exceeds balance error response")
    public static class InvoicePaymentExceedsErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Invoice ID", example = "22")
        private Long invoiceId;
        @Schema(description = "Invoice number", example = "INV-20250101-0001")
        private String invoiceNumber;
        @Schema(description = "Payment amount that was attempted", example = "750.00")
        private BigDecimal paymentAmount;
        @Schema(description = "Remaining balance due on the invoice", example = "500.00")
        private BigDecimal balanceDue;
    }
}