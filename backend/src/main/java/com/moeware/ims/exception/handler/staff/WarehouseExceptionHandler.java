package com.moeware.ims.exception.handler.staff;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.staff.warehouse.WarehouseAlreadyExistsException;
import com.moeware.ims.exception.staff.warehouse.WarehouseHasInventoryException;
import com.moeware.ims.exception.staff.warehouse.WarehouseNotFoundException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all warehouse-domain exceptions.
 *
 * All three exceptions carry extra fields worth exposing in the response,
 * so each gets a dedicated handler method.
 *
 * {@link ManagerNotFoundException} extends {@link BaseAppException} and returns
 * a standard {@link ErrorResponse} — covered automatically by the generic
 * handler in {@link UserExceptionHandler}. No dedicated method needed here.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class WarehouseExceptionHandler {

    @ExceptionHandler(WarehouseNotFoundException.class)
    public ResponseEntity<WarehouseNotFoundErrorResponse> handleWarehouseNotFound(
            WarehouseNotFoundException ex, WebRequest request) {

        log.error("Warehouse not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                WarehouseNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .warehouseId(ex.getWarehouseId())
                        .code(ex.getCode())
                        .build());
    }

    @ExceptionHandler(WarehouseAlreadyExistsException.class)
    public ResponseEntity<WarehouseConflictErrorResponse> handleWarehouseAlreadyExists(
            WarehouseAlreadyExistsException ex, WebRequest request) {

        log.error("Warehouse conflict: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                WarehouseConflictErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .code(ex.getCode())
                        .name(ex.getName())
                        .build());
    }

    @ExceptionHandler(WarehouseHasInventoryException.class)
    public ResponseEntity<WarehouseHasInventoryErrorResponse> handleWarehouseHasInventory(
            WarehouseHasInventoryException ex, WebRequest request) {

        log.error("Warehouse deletion blocked (has inventory): {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                WarehouseHasInventoryErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .warehouseId(ex.getWarehouseId())
                        .inventoryItemCount(ex.getInventoryItemCount())
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
    @Schema(description = "Warehouse not found error response")
    public static class WarehouseNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Warehouse ID that was not found", example = "5")
        private Long warehouseId;
        @Schema(description = "Warehouse code that was not found", example = "WH-001")
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Warehouse conflict error response")
    public static class WarehouseConflictErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Conflicting code", example = "WH-001")
        private String code;
        @Schema(description = "Conflicting name", example = "Main Warehouse")
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Warehouse has inventory error response")
    public static class WarehouseHasInventoryErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Warehouse ID that could not be deleted", example = "5")
        private Long warehouseId;
        @Schema(description = "Number of inventory items that must be transferred first", example = "43")
        private int inventoryItemCount;
    }
}