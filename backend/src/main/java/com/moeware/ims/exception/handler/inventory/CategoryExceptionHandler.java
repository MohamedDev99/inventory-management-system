package com.moeware.ims.exception.handler.inventory;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.inventory.category.CategoryAlreadyExistsException;
import com.moeware.ims.exception.inventory.category.CategoryHasChildrenException;
import com.moeware.ims.exception.inventory.category.CategoryHasProductsException;
import com.moeware.ims.exception.inventory.category.CategoryNotFoundException;
import com.moeware.ims.exception.inventory.category.CircularCategoryReferenceException;
import com.moeware.ims.exception.inventory.category.ParentCategoryNotFoundException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all category-domain exceptions.
 *
 * All six exceptions carry extra fields worth exposing in the response,
 * so each gets its own dedicated handler method.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class CategoryExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<CategoryNotFoundErrorResponse> handleCategoryNotFound(
            CategoryNotFoundException ex, WebRequest request) {

        log.error("Category not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                CategoryNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .categoryId(ex.getCategoryId())
                        .code(ex.getCode())
                        .build());
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<CategoryConflictErrorResponse> handleCategoryAlreadyExists(
            CategoryAlreadyExistsException ex, WebRequest request) {

        log.error("Category conflict: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                CategoryConflictErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .code(ex.getCode())
                        .name(ex.getName())
                        .build());
    }

    @ExceptionHandler(CategoryHasChildrenException.class)
    public ResponseEntity<CategoryBlockedErrorResponse> handleCategoryHasChildren(
            CategoryHasChildrenException ex, WebRequest request) {

        log.error("Category deletion blocked (has children): {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                CategoryBlockedErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .categoryId(ex.getCategoryId())
                        .build());
    }

    @ExceptionHandler(CategoryHasProductsException.class)
    public ResponseEntity<CategoryHasProductsErrorResponse> handleCategoryHasProducts(
            CategoryHasProductsException ex, WebRequest request) {

        log.error("Category deletion blocked (has products): {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                CategoryHasProductsErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .categoryId(ex.getCategoryId())
                        .productCount(ex.getProductCount())
                        .build());
    }

    @ExceptionHandler(ParentCategoryNotFoundException.class)
    public ResponseEntity<ParentCategoryNotFoundErrorResponse> handleParentCategoryNotFound(
            ParentCategoryNotFoundException ex, WebRequest request) {

        log.error("Parent category not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                ParentCategoryNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .parentCategoryId(ex.getParentCategoryId())
                        .build());
    }

    @ExceptionHandler(CircularCategoryReferenceException.class)
    public ResponseEntity<CircularReferenceErrorResponse> handleCircularCategoryReference(
            CircularCategoryReferenceException ex, WebRequest request) {

        log.error("Circular category reference: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                CircularReferenceErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .categoryId(ex.getCategoryId())
                        .parentCategoryId(ex.getParentCategoryId())
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
    @Schema(description = "Category not found error response")
    public static class CategoryNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Category ID that was not found", example = "5")
        private Long categoryId;
        @Schema(description = "Category code that was not found", example = "ELEC-01")
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Category conflict error response")
    public static class CategoryConflictErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Conflicting code", example = "ELEC-01")
        private String code;
        @Schema(description = "Conflicting name", example = "Electronics")
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Category deletion blocked error response")
    public static class CategoryBlockedErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Category ID that could not be deleted", example = "5")
        private Long categoryId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Category has products error response")
    public static class CategoryHasProductsErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Category ID that could not be deleted", example = "5")
        private Long categoryId;
        @Schema(description = "Number of products that must be reassigned first", example = "12")
        private long productCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Parent category not found error response")
    public static class ParentCategoryNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Parent category ID that was not found", example = "3")
        private Long parentCategoryId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Circular category reference error response")
    public static class CircularReferenceErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Category ID involved in the circular reference", example = "7")
        private Long categoryId;
        @Schema(description = "Parent category ID that would create the circular reference", example = "3")
        private Long parentCategoryId;
    }
}