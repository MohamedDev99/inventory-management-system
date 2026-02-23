package com.moeware.ims.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.auth.InvalidTokenException;
import com.moeware.ims.exception.inventory.InventoryConcurrentModificationException;
import com.moeware.ims.exception.inventory.category.CategoryAlreadyExistsException;
import com.moeware.ims.exception.inventory.category.CategoryHasChildrenException;
import com.moeware.ims.exception.inventory.category.CategoryHasProductsException;
import com.moeware.ims.exception.inventory.category.CategoryNotFoundException;
import com.moeware.ims.exception.inventory.category.CircularCategoryReferenceException;
import com.moeware.ims.exception.inventory.category.ParentCategoryNotFoundException;
import com.moeware.ims.exception.inventory.product.ProductAlreadyExistsException;
import com.moeware.ims.exception.inventory.product.ProductNotFoundException;
import com.moeware.ims.exception.staff.warehouse.WarehouseAlreadyExistsException;
import com.moeware.ims.exception.staff.warehouse.WarehouseHasInventoryException;
import com.moeware.ims.exception.staff.warehouse.WarehouseNotFoundException;
import com.moeware.ims.exception.transaction.PendingApprovalException;
import com.moeware.ims.exception.transaction.inventoryMovement.InvalidTransferException;
import com.moeware.ims.exception.transaction.stockAdjustment.InsufficientStockException;
import com.moeware.ims.exception.transaction.stockAdjustment.StockAdjustmentException;
import com.moeware.ims.exception.user.ManagerNotFoundException;
import com.moeware.ims.exception.user.UserAlreadyExistsException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the application
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        // ==================== User Exceptions ====================

        /**
         * Handle user already exists exceptions
         */
        @ExceptionHandler(UserAlreadyExistsException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleUserAlreadyExistsException(
                        UserAlreadyExistsException ex,
                        WebRequest request) {

                log.error("User already exists: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Conflict")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle manager not found exceptions
         */
        @ExceptionHandler(ManagerNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ErrorResponse handleManagerNotFoundException(
                        ManagerNotFoundException ex,
                        WebRequest request) {

                log.error("Manager not found: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        // ==================== Auth Exceptions ====================

        /**
         * Handle authentication errors
         */
        @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        public ErrorResponse handleAuthenticationException(
                        Exception ex,
                        WebRequest request) {

                log.error("Authentication error: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Unauthorized")
                                .message("Invalid username or password")
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle invalid token exceptions
         */
        @ExceptionHandler(InvalidTokenException.class)
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        public ErrorResponse handleInvalidTokenException(
                        InvalidTokenException ex,
                        WebRequest request) {

                log.error("Invalid token: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Unauthorized")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        // ==================== Product Exceptions ====================

        /**
         * Handle product not found exceptions
         */
        @ExceptionHandler(ProductNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ProductErrorResponse handleProductNotFoundException(
                        ProductNotFoundException ex,
                        WebRequest request) {

                log.error("Product not found: {}", ex.getMessage());

                return ProductErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Product Not Found")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .productId(ex.getProductId())
                                .sku(ex.getSku())
                                .build();
        }

        /**
         * Handle product already exists exceptions
         */
        @ExceptionHandler(ProductAlreadyExistsException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleProductAlreadyExistsException(
                        ProductAlreadyExistsException ex,
                        WebRequest request) {

                log.error("Product already exists: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Conflict")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        // ==================== Category Exceptions ====================

        /**
         * Handle category not found exceptions
         */
        @ExceptionHandler(CategoryNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ErrorResponse handleCategoryNotFoundException(
                        CategoryNotFoundException ex,
                        WebRequest request) {

                log.error("Category not found: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle category already exists exceptions
         */
        @ExceptionHandler(CategoryAlreadyExistsException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleCategoryAlreadyExistsException(
                        CategoryAlreadyExistsException ex,
                        WebRequest request) {

                log.error("Category already exists: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Conflict")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle category has children exceptions
         */
        @ExceptionHandler(CategoryHasChildrenException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleCategoryHasChildrenException(
                        CategoryHasChildrenException ex,
                        WebRequest request) {

                log.error("Category has children: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Conflict")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle category has products exceptions
         */
        @ExceptionHandler(CategoryHasProductsException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleCategoryHasProductsException(
                        CategoryHasProductsException ex,
                        WebRequest request) {

                log.error("Category has products: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Conflict")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle circular category reference exceptions
         */
        @ExceptionHandler(CircularCategoryReferenceException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleCircularCategoryReferenceException(
                        CircularCategoryReferenceException ex,
                        WebRequest request) {

                log.error("Circular category reference: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Bad Request")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle parent category not found exceptions
         */
        @ExceptionHandler(ParentCategoryNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ErrorResponse handleParentCategoryNotFoundException(
                        ParentCategoryNotFoundException ex,
                        WebRequest request) {

                log.error("Parent category not found: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        // ==================== Warehouse Exceptions ====================

        /**
         * Handle warehouse not found exceptions
         */
        @ExceptionHandler(WarehouseNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public WarehouseErrorResponse handleWarehouseNotFoundException(
                        WarehouseNotFoundException ex,
                        WebRequest request) {

                log.error("Warehouse not found: {}", ex.getMessage());

                return WarehouseErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Warehouse Not Found")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .warehouseId(ex.getWarehouseId())
                                .build();
        }

        /**
         * Handle warehouse already exists exceptions
         */
        @ExceptionHandler(WarehouseAlreadyExistsException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleWarehouseAlreadyExistsException(
                        WarehouseAlreadyExistsException ex,
                        WebRequest request) {

                log.error("Warehouse already exists: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Conflict")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle warehouse has inventory exceptions
         */
        @ExceptionHandler(WarehouseHasInventoryException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleWarehouseHasInventoryException(
                        WarehouseHasInventoryException ex,
                        WebRequest request) {

                log.error("Warehouse has inventory: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Conflict")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        // ==================== Transaction Exceptions ====================

        /**
         * Handle pending approval exceptions
         */
        @ExceptionHandler(PendingApprovalException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public PendingApprovalErrorResponse handlePendingApprovalException(
                        PendingApprovalException ex,
                        WebRequest request) {

                log.error("Pending approval: {}", ex.getMessage());

                return PendingApprovalErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Pending Approval")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .resourceType(ex.getResourceType())
                                .resourceId(ex.getResourceId())
                                .build();
        }

        /**
         * Handle concurrent modification exceptions (inventory-specific)
         */
        @ExceptionHandler(InventoryConcurrentModificationException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ConcurrentModificationErrorResponse handleInventoryConcurrentModificationException(
                        InventoryConcurrentModificationException ex,
                        WebRequest request) {

                log.error("Concurrent modification detected: {}", ex.getMessage());

                return ConcurrentModificationErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Concurrent Modification")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .entityType(ex.getEntityType())
                                .entityId(ex.getEntityId())
                                .expectedVersion(ex.getExpectedVersion())
                                .actualVersion(ex.getActualVersion())
                                .build();
        }

        // ==================== Inventory Movement Exceptions ====================

        /**
         * Handle invalid transfer exceptions
         */
        @ExceptionHandler(InvalidTransferException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleInvalidTransferException(
                        InvalidTransferException ex,
                        WebRequest request) {

                log.error("Invalid transfer: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Invalid Transfer")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        // ==================== Stock Exceptions ====================

        /**
         * Handle insufficient stock exceptions
         */
        @ExceptionHandler(InsufficientStockException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleInsufficientStockException(
                        InsufficientStockException ex,
                        WebRequest request) {

                log.error("Insufficient stock: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Insufficient Stock")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle stock adjustment exceptions
         */
        @ExceptionHandler(StockAdjustmentException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleStockAdjustmentException(
                        StockAdjustmentException ex,
                        WebRequest request) {

                log.error("Stock adjustment error: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Stock Adjustment Error")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        // ==================== Exceptions ====================

        /**
         * Handle resource not found exceptions
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ErrorResponse handleResourceNotFoundException(
                        ResourceNotFoundException ex,
                        WebRequest request) {

                log.error("Resource not found: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle JPA optimistic locking exceptions
         */
        @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleOptimisticLockingFailure(
                        ObjectOptimisticLockingFailureException ex,
                        WebRequest request) {

                log.error("Optimistic locking failure: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Concurrent Modification")
                                .message("The record has been modified by another user. Please refresh and try again.")
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle illegal argument exceptions
         */
        @ExceptionHandler(IllegalArgumentException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleIllegalArgumentException(
                        IllegalArgumentException ex,
                        WebRequest request) {

                log.error("Illegal argument: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Invalid Argument")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle illegal state exceptions
         */
        @ExceptionHandler(IllegalStateException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleIllegalStateException(
                        IllegalStateException ex,
                        WebRequest request) {

                log.error("Illegal state: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Invalid State")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle validation errors
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ValidationErrorResponse handleValidationExceptions(
                        MethodArgumentNotValidException ex,
                        WebRequest request) {

                log.error("Validation error: {}", ex.getMessage());

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach((error) -> {
                        String errorMessage = error.getDefaultMessage();
                        if (error instanceof FieldError) {
                                FieldError fieldError = (FieldError) error;
                                errors.put(fieldError.getField(), errorMessage);
                        } else {
                                errors.put(error.getObjectName(), errorMessage);
                        }
                });

                return ValidationErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Validation Failed")
                                .message("Invalid input data")
                                .path(request.getDescription(false).replace("uri=", ""))
                                .fieldErrors(errors)
                                .build();
        }

        /**
         * Handle duplicate resource exceptions
         */
        @ExceptionHandler(DuplicateResourceException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleDuplicateResourceException(
                        DuplicateResourceException ex,
                        WebRequest request) {

                log.error("Duplicate resource: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Conflict")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle invalid operation exceptions
         */
        @ExceptionHandler({ InvalidOperationException.class, IllegalArgumentException.class,
                        IllegalStateException.class })
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleInvalidOperationException(
                        Exception ex,
                        WebRequest request) {

                log.error("Invalid operation: {}", ex.getMessage());

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Bad Request")
                                .message(ex.getMessage())
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        /**
         * Handle all other exceptions
         */
        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ErrorResponse handleGlobalException(
                        Exception ex,
                        WebRequest request) {

                log.error("Internal server error: ", ex);

                return ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("Internal Server Error")
                                .message("An unexpected error occurred")
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
        }

        // ==================== Error Response Classes ====================

        /**
         * Standard error response
         */
        @lombok.Data
        @lombok.Builder
        @lombok.NoArgsConstructor
        @lombok.AllArgsConstructor
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

        /**
         * Validation error response with field errors
         */
        @lombok.Data
        @lombok.Builder
        @lombok.NoArgsConstructor
        @lombok.AllArgsConstructor
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

                @Schema(description = "Field-specific validation errors", example = "{\"username\": \"Username must be between 3 and 50 characters\", \"email\": \"Email must be valid\"}")
                private Map<String, String> fieldErrors;
        }

        /**
         * Warehouse-specific error response
         */
        @lombok.Data
        @lombok.Builder
        @lombok.NoArgsConstructor
        @lombok.AllArgsConstructor
        @Schema(description = "Warehouse not found error response")
        public static class WarehouseErrorResponse {
                @Schema(description = "Error timestamp", example = "2026-02-13T10:30:00")
                private LocalDateTime timestamp;

                @Schema(description = "HTTP status code", example = "404")
                private int status;

                @Schema(description = "Error type", example = "Warehouse Not Found")
                private String error;

                @Schema(description = "Error message", example = "Warehouse not found with id: 5")
                private String message;

                @Schema(description = "Request path", example = "/api/v1/inventory/warehouse/5")
                private String path;

                @Schema(description = "Warehouse ID that was not found", example = "5")
                private Long warehouseId;
        }

        /**
         * Product-specific error response
         */
        @lombok.Data
        @lombok.Builder
        @lombok.NoArgsConstructor
        @lombok.AllArgsConstructor
        @Schema(description = "Product not found error response")
        public static class ProductErrorResponse {
                @Schema(description = "Error timestamp", example = "2026-02-13T10:30:00")
                private LocalDateTime timestamp;

                @Schema(description = "HTTP status code", example = "404")
                private int status;

                @Schema(description = "Error type", example = "Product Not Found")
                private String error;

                @Schema(description = "Error message", example = "Product not found with SKU: LAP-001")
                private String message;

                @Schema(description = "Request path", example = "/api/v1/inventory/product/10")
                private String path;

                @Schema(description = "Product ID that was not found", example = "10")
                private Long productId;

                @Schema(description = "Product SKU that was not found", example = "LAP-001")
                private String sku;
        }

        /**
         * Pending approval error response
         */
        @lombok.Data
        @lombok.Builder
        @lombok.NoArgsConstructor
        @lombok.AllArgsConstructor
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

                @Schema(description = "Resource type", example = "StockAdjustment")
                private String resourceType;

                @Schema(description = "Resource ID", example = "45")
                private Long resourceId;
        }

        /**
         * Concurrent modification error response
         */
        @lombok.Data
        @lombok.Builder
        @lombok.NoArgsConstructor
        @lombok.AllArgsConstructor
        @Schema(description = "Concurrent modification error response")
        public static class ConcurrentModificationErrorResponse {
                @Schema(description = "Error timestamp", example = "2026-02-13T10:30:00")
                private LocalDateTime timestamp;

                @Schema(description = "HTTP status code", example = "409")
                private int status;

                @Schema(description = "Error type", example = "Concurrent Modification")
                private String error;

                @Schema(description = "Error message", example = "Record modified by another user")
                private String message;

                @Schema(description = "Request path", example = "/api/v1/inventory/transfer")
                private String path;

                @Schema(description = "Entity type", example = "InventoryItem")
                private String entityType;

                @Schema(description = "Entity ID", example = "123")
                private Long entityId;

                @Schema(description = "Expected version", example = "5")
                private Integer expectedVersion;

                @Schema(description = "Actual version", example = "6")
                private Integer actualVersion;
        }
}