package com.moeware.ims.exception.handler.inventory;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.BaseAppException;
import com.moeware.ims.exception.handler.UserExceptionHandler;
import com.moeware.ims.exception.inventory.inventoryItem.InventoryItemNotFoundException;
import com.moeware.ims.exception.transaction.inventoryMovement.InvalidInventoryTransferException;
import com.moeware.ims.exception.transaction.stockAdjustment.InsufficientStockException;
import com.moeware.ims.exception.transaction.stockAdjustment.StockAdjustmentException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all inventory-domain exceptions.
 *
 * Dedicated handlers for exceptions with extra response fields:
 * {@link InventoryItemNotFoundException},
 * {@link InvalidInventoryTransferException},
 * {@link InsufficientStockException}.
 *
 * {@link StockAdjustmentException} extends {@link BaseAppException} with no
 * extra
 * fields — covered automatically by the generic handler in
 * {@link UserExceptionHandler}.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class InventoryExceptionHandler {

    @ExceptionHandler(InventoryItemNotFoundException.class)
    public ResponseEntity<InventoryItemNotFoundErrorResponse> handleInventoryItemNotFound(
            InventoryItemNotFoundException ex, WebRequest request) {

        log.error("Inventory item not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                InventoryItemNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .inventoryItemId(ex.getInventoryItemId())
                        .productId(ex.getProductId())
                        .warehouseId(ex.getWarehouseId())
                        .build());
    }

    @ExceptionHandler(InvalidInventoryTransferException.class)
    public ResponseEntity<InvalidTransferErrorResponse> handleInvalidTransfer(
            InvalidInventoryTransferException ex, WebRequest request) {

        log.error("Invalid inventory transfer: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                InvalidTransferErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .fromWarehouseId(ex.getFromWarehouseId())
                        .toWarehouseId(ex.getToWarehouseId())
                        .build());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<InsufficientStockErrorResponse> handleInsufficientStock(
            InsufficientStockException ex, WebRequest request) {

        log.error("Insufficient stock: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                InsufficientStockErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .productId(ex.getProductId())
                        .warehouseId(ex.getWarehouseId())
                        .warehouseName(ex.getWarehouseName())
                        .availableQuantity(ex.getAvailableQuantity())
                        .requestedQuantity(ex.getRequestedQuantity())
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
    @Schema(description = "Inventory item not found error response")
    public static class InventoryItemNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Inventory item ID (when looked up directly)", example = "42")
        private Long inventoryItemId;
        @Schema(description = "Product ID (when looked up by product+warehouse)", example = "7")
        private Long productId;
        @Schema(description = "Warehouse ID (when looked up by product+warehouse)", example = "2")
        private Long warehouseId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Invalid inventory transfer error response")
    public static class InvalidTransferErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Source warehouse ID", example = "1")
        private Long fromWarehouseId;
        @Schema(description = "Destination warehouse ID", example = "1")
        private Long toWarehouseId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Insufficient stock error response")
    public static class InsufficientStockErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Product ID", example = "7")
        private Long productId;
        @Schema(description = "Warehouse ID", example = "2")
        private Long warehouseId;
        @Schema(description = "Warehouse display name", example = "Main Warehouse")
        private String warehouseName;
        @Schema(description = "Current available quantity", example = "50")
        private int availableQuantity;
        @Schema(description = "Quantity that was requested", example = "75")
        private int requestedQuantity;
    }
}