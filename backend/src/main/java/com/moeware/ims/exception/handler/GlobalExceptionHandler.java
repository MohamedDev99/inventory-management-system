package com.moeware.ims.exception.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.BaseAppException;
import com.moeware.ims.exception.InvalidOperationException;
import com.moeware.ims.exception.inventory.InventoryConcurrentModificationException;
import com.moeware.ims.exception.transaction.PendingApprovalException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Global fallback exception handler.
 *
 * <p>
 * <strong>What lives here and why:</strong>
 * <ul>
 * <li>{@link BaseAppException} generic handler — single catch-all for every
 * domain
 * exception that does NOT need extra fields in the response body. Domain
 * handlers
 * that DO need extra fields ({@code UserExceptionHandler},
 * {@code ProductExceptionHandler},
 * etc.) declare their own more-specific {@code @ExceptionHandler} methods,
 * which
 * Spring resolves first due to type specificity.</li>
 * <li>{@link PendingApprovalException} — carries {@code resourceType} +
 * {@code resourceId}
 * that belong in the response body.</li>
 * <li>{@link InventoryConcurrentModificationException} — carries version
 * mismatch fields
 * useful for debugging concurrent writes.</li>
 * <li>{@link ObjectOptimisticLockingFailureException} — thrown directly by
 * Spring/JPA
 * (not our code); does not extend {@code BaseAppException}.</li>
 * <li>{@link ResourceNotFoundException} / {@link DuplicateResourceException} —
 * generic fallbacks still used in services not yet migrated to typed
 * exceptions.</li>
 * <li>{@link InvalidOperationException} — generic BAD_REQUEST fallback.</li>
 * <li>{@link MethodArgumentNotValidException} — Bean Validation failures.</li>
 * <li>{@link Exception} catch-all — last-resort 500 handler.</li>
 * </ul>
 *
 * <p>
 * <strong>What was removed and where it went:</strong>
 * <ul>
 * <li>User exceptions → {@code UserExceptionHandler}</li>
 * <li>Auth exceptions → {@code AuthExceptionHandler}</li>
 * <li>Product exceptions → {@code ProductExceptionHandler}</li>
 * <li>Category exceptions → {@code CategoryExceptionHandler}</li>
 * <li>Supplier exceptions → {@code SupplierExceptionHandler}</li>
 * <li>Warehouse exceptions → {@code WarehouseExceptionHandler}</li>
 * <li>Customer exceptions → {@code CustomerExceptionHandler}</li>
 * <li>Employee exceptions → {@code EmployeeExceptionHandler}</li>
 * <li>Department exceptions → {@code DepartmentExceptionHandler}</li>
 * <li>Inventory/stock exceptions → {@code InventoryExceptionHandler}</li>
 * <li>Order exceptions → {@code OrderExceptionHandler}</li>
 * <li>Shipment exceptions → {@code ShipmentExceptionHandler}</li>
 * <li>Payment exceptions → {@code PaymentExceptionHandler}</li>
 * <li>Invoice exceptions → {@code InvoiceExceptionHandler}</li>
 * </ul>
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        // ── Generic BaseAppException handler ─────────────────────────────────────
        //
        // Catches any BaseAppException NOT matched by a more-specific handler in a
        // domain ExceptionHandler. Spring always prefers the most specific type, so
        // e.g. UserExceptionHandler.handleUserNotFound(UserNotFoundException) fires
        // before this method even though UserNotFoundException extends
        // BaseAppException.

        @ExceptionHandler(BaseAppException.class)
        public ResponseEntity<ErrorResponse> handleBaseAppException(
                        BaseAppException ex, WebRequest request) {

                log.error("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());

                return ResponseEntity.status(ex.getHttpStatus()).body(
                                ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .status(ex.getHttpStatus().value())
                                                .error(ex.getErrorTitle())
                                                .message(ex.getMessage())
                                                .path(extractPath(request))
                                                .build());
        }

        // ── PendingApprovalException ──────────────────────────────────────────────
        // Carries resourceType + resourceId that enrich the response body.

        @ExceptionHandler(PendingApprovalException.class)
        public ResponseEntity<PendingApprovalErrorResponse> handlePendingApproval(
                        PendingApprovalException ex, WebRequest request) {

                log.error("Pending approval — {} id {}: {}",
                                ex.getResourceType(), ex.getResourceId(), ex.getMessage());

                return ResponseEntity.status(ex.getHttpStatus()).body(
                                PendingApprovalErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .status(ex.getHttpStatus().value())
                                                .error(ex.getErrorTitle())
                                                .message(ex.getMessage())
                                                .path(extractPath(request))
                                                .resourceType(ex.getResourceType())
                                                .resourceId(ex.getResourceId())
                                                .build());
        }

        // ── InventoryConcurrentModificationException ──────────────────────────────
        // Carries version mismatch fields useful for frontend conflict resolution.

        @ExceptionHandler(InventoryConcurrentModificationException.class)
        public ResponseEntity<ConcurrentModificationErrorResponse> handleInventoryConcurrentModification(
                        InventoryConcurrentModificationException ex, WebRequest request) {

                log.error("Concurrent modification — {} id {}: {}",
                                ex.getEntityType(), ex.getEntityId(), ex.getMessage());

                return ResponseEntity.status(ex.getHttpStatus()).body(
                                ConcurrentModificationErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .status(ex.getHttpStatus().value())
                                                .error(ex.getErrorTitle())
                                                .message(ex.getMessage())
                                                .path(extractPath(request))
                                                .entityType(ex.getEntityType())
                                                .entityId(ex.getEntityId())
                                                .expectedVersion(ex.getExpectedVersion())
                                                .actualVersion(ex.getActualVersion())
                                                .build());
        }

        // ── ObjectOptimisticLockingFailureException ───────────────────────────────
        // Thrown directly by Spring/JPA — does not extend BaseAppException.
        // Fires when a @Version-annotated entity is saved after a concurrent update.

        @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
        public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(
                        ObjectOptimisticLockingFailureException ex, WebRequest request) {

                log.error("Optimistic locking failure: {}", ex.getMessage());

                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                                ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .status(HttpStatus.CONFLICT.value())
                                                .error("Concurrent Modification")
                                                .message("The record was modified by another user. Please refresh and try again.")
                                                .path(extractPath(request))
                                                .build());
        }

        // ── Generic fallbacks (not yet replaced by typed exceptions) ─────────────

        /**
         * Generic bad-request fallback for invalid operations.
         * {@link InvalidOperationException} extends {@link BaseAppException} so it is
         * already covered by {@link #handleBaseAppException}; this explicit method
         * is kept only to preserve the original log message style.
         * Can be removed — the generic handler above will cover it automatically.
         */
        @ExceptionHandler(InvalidOperationException.class)
        public ResponseEntity<ErrorResponse> handleInvalidOperation(
                        InvalidOperationException ex, WebRequest request) {

                log.error("Invalid operation: {}", ex.getMessage());

                return ResponseEntity.status(ex.getHttpStatus()).body(
                                ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .status(ex.getHttpStatus().value())
                                                .error(ex.getErrorTitle())
                                                .message(ex.getMessage())
                                                .path(extractPath(request))
                                                .build());
        }

        // ── Bean Validation ───────────────────────────────────────────────────────

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ValidationErrorResponse> handleValidation(
                        MethodArgumentNotValidException ex, WebRequest request) {

                log.warn("Validation failed: {}", ex.getMessage());

                Map<String, String> fieldErrors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String message = error.getDefaultMessage();
                        if (error instanceof FieldError fieldError) {
                                fieldErrors.put(fieldError.getField(), message);
                        } else {
                                fieldErrors.put(error.getObjectName(), message);
                        }
                });

                return ResponseEntity.badRequest().body(
                                ValidationErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .status(HttpStatus.BAD_REQUEST.value())
                                                .error("Validation Failed")
                                                .message("Invalid input data")
                                                .path(extractPath(request))
                                                .fieldErrors(fieldErrors)
                                                .build());
        }

        // ── Last-resort catch-all ─────────────────────────────────────────────────

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleUnexpected(
                        Exception ex, WebRequest request) {

                log.error("Unexpected error: ", ex);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                                .error("Internal Server Error")
                                                .message("An unexpected error occurred")
                                                .path(extractPath(request))
                                                .build());
        }

        // ── Helper ────────────────────────────────────────────────────────────────

        private String extractPath(WebRequest request) {
                return request.getDescription(false).replace("uri=", "");
        }

        // ── Shared response classes ───────────────────────────────────────────────
        //
        // ErrorResponse is referenced by all domain handlers via
        // GlobalExceptionHandler.ErrorResponse — kept here as the single source of
        // truth to avoid duplicating the class across handler files.

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "Standard error response")
        public static class ErrorResponse {
                @Schema(description = "Error timestamp", example = "2026-01-28T10:30:00")
                private LocalDateTime timestamp;

                @Schema(description = "HTTP status code", example = "404")
                private int status;

                @Schema(description = "Error type", example = "Not Found")
                private String error;

                @Schema(description = "Error message", example = "User not found with id: 123")
                private String message;

                @Schema(description = "Request path", example = "/api/users/123")
                private String path;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "Validation error response with field-specific errors")
        public static class ValidationErrorResponse {
                @Schema(description = "Error timestamp", example = "2026-01-28T10:30:00")
                private LocalDateTime timestamp;

                @Schema(description = "HTTP status code", example = "400")
                private int status;

                @Schema(description = "Error type", example = "Validation Failed")
                private String error;

                @Schema(description = "Error message", example = "Invalid input data")
                private String message;

                @Schema(description = "Request path", example = "/api/auth/register")
                private String path;

                @Schema(description = "Field-specific validation errors", example = "{\"username\": \"must be between 3 and 50 characters\", \"email\": \"must be valid\"}")
                private Map<String, String> fieldErrors;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "Pending approval error response")
        public static class PendingApprovalErrorResponse {
                @Schema(description = "Error timestamp", example = "2026-02-13T10:30:00")
                private LocalDateTime timestamp;

                @Schema(description = "HTTP status code", example = "409")
                private int status;

                @Schema(description = "Error type", example = "Pending Approval")
                private String error;

                @Schema(description = "Error message", example = "StockAdjustment with id 45 is pending approval")
                private String message;

                @Schema(description = "Request path", example = "/api/v1/inventory/adjust/45")
                private String path;

                @Schema(description = "Resource type that is pending", example = "StockAdjustment")
                private String resourceType;

                @Schema(description = "ID of the resource pending approval", example = "45")
                private Long resourceId;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "Concurrent modification error response")
        public static class ConcurrentModificationErrorResponse {
                @Schema(description = "Error timestamp", example = "2026-02-13T10:30:00")
                private LocalDateTime timestamp;

                @Schema(description = "HTTP status code", example = "409")
                private int status;

                @Schema(description = "Error type", example = "Concurrent Modification")
                private String error;

                @Schema(description = "Error message")
                private String message;

                @Schema(description = "Request path", example = "/api/v1/inventory/transfer")
                private String path;

                @Schema(description = "Entity type involved in the conflict", example = "InventoryItem")
                private String entityType;

                @Schema(description = "Entity ID involved in the conflict", example = "123")
                private Long entityId;

                @Schema(description = "Version the client read", example = "5")
                private Integer expectedVersion;

                @Schema(description = "Version found in the database at write time", example = "6")
                private Integer actualVersion;
        }
}