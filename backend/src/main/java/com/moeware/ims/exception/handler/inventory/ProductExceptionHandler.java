package com.moeware.ims.exception.handler.inventory;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.inventory.product.ProductAlreadyExistsException;
import com.moeware.ims.exception.inventory.product.ProductNotFoundException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all product-domain exceptions.
 *
 * Both exceptions carry extra fields (sku, barcode, productId) that enrich
 * the response body, so both get dedicated handler methods.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class ProductExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProductNotFoundErrorResponse> handleProductNotFound(
            ProductNotFoundException ex,
            WebRequest request) {

        log.error("Product not found: {}", ex.getMessage());

        ProductNotFoundErrorResponse body = ProductNotFoundErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getErrorTitle())
                .message(ex.getMessage())
                .path(extractPath(request))
                .productId(ex.getProductId())
                .sku(ex.getSku())
                .barcode(ex.getBarcode())
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ProductConflictErrorResponse> handleProductAlreadyExists(
            ProductAlreadyExistsException ex,
            WebRequest request) {

        log.error("Product conflict: {}", ex.getMessage());

        ProductConflictErrorResponse body = ProductConflictErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getErrorTitle())
                .message(ex.getMessage())
                .path(extractPath(request))
                .sku(ex.getSku())
                .barcode(ex.getBarcode())
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(body);
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
    @Schema(description = "Product not found error response")
    public static class ProductNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        @Schema(description = "Product ID that was not found", example = "10")
        private Long productId;

        @Schema(description = "SKU that was not found", example = "LAP-001")
        private String sku;

        @Schema(description = "Barcode that was not found", example = "1234567890")
        private String barcode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Product conflict error response")
    public static class ProductConflictErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        @Schema(description = "Conflicting SKU", example = "LAP-001")
        private String sku;

        @Schema(description = "Conflicting barcode", example = "1234567890")
        private String barcode;
    }
}