package com.moeware.ims.exception.handler.inventory;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.inventory.supplier.SupplierAlreadyExistsException;
import com.moeware.ims.exception.inventory.supplier.SupplierNotFoundException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all supplier-domain exceptions.
 *
 * {@link SupplierNotFoundException} and {@link SupplierAlreadyExistsException}
 * carry extra fields worth exposing in the response, so both get dedicated
 * handler methods.
 *
 * {@link InactiveSupplierException}, {@link InvalidSupplierRatingException},
 * and
 * {@link SupplierHasPendingOrdersException} are currently unused in the service
 * (see TODOs). They extend {@link BaseAppException} and will be covered by the
 * generic handler in {@link UserExceptionHandler} automatically when wired in.
 * Add dedicated handlers here only if their response needs extra fields.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class SupplierExceptionHandler {

    @ExceptionHandler(SupplierNotFoundException.class)
    public ResponseEntity<SupplierNotFoundErrorResponse> handleSupplierNotFound(
            SupplierNotFoundException ex, WebRequest request) {

        log.error("Supplier not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                SupplierNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .supplierId(ex.getSupplierId())
                        .code(ex.getCode())
                        .build());
    }

    @ExceptionHandler(SupplierAlreadyExistsException.class)
    public ResponseEntity<SupplierConflictErrorResponse> handleSupplierAlreadyExists(
            SupplierAlreadyExistsException ex, WebRequest request) {

        log.error("Supplier conflict: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                SupplierConflictErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .conflictField(ex.getConflictField().name().toLowerCase())
                        .conflictValue(ex.getConflictValue())
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
    @Schema(description = "Supplier not found error response")
    public static class SupplierNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Supplier ID that was not found", example = "7")
        private Long supplierId;
        @Schema(description = "Supplier code that was not found", example = "SUP-001")
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Supplier conflict error response")
    public static class SupplierConflictErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Field that caused the conflict", example = "code", allowableValues = { "code", "email" })
        private String conflictField;
        @Schema(description = "Value that caused the conflict", example = "SUP-001")
        private String conflictValue;
    }
}