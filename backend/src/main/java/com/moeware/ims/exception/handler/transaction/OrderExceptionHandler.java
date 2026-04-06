package com.moeware.ims.exception.handler.transaction;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.transaction.InvalidOrderStatusTransitionException;
import com.moeware.ims.exception.transaction.OrderNotEditableException;
import com.moeware.ims.exception.transaction.purchaseOrder.PurchaseOrderItemNotFoundException;
import com.moeware.ims.exception.transaction.purchaseOrder.PurchaseOrderNotFoundException;
import com.moeware.ims.exception.transaction.purchaseOrder.PurchaseOrderReceiptException;
import com.moeware.ims.exception.transaction.salesOrder.SalesOrderNotFoundException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all order-domain exceptions (both sales and purchase orders).
 *
 * Dedicated handlers (extra fields in response body):
 * {@link SalesOrderNotFoundException}, {@link PurchaseOrderNotFoundException},
 * {@link InvalidOrderStatusTransitionException},
 * {@link OrderNotEditableException},
 * {@link PurchaseOrderReceiptException},
 * {@link PurchaseOrderItemNotFoundException}.
 *
 * Covered by the generic handler in {@link UserExceptionHandler}:
 * {@link InvalidPurchaseOrderOperationException} — no extra fields needed.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class OrderExceptionHandler {

    @ExceptionHandler(SalesOrderNotFoundException.class)
    public ResponseEntity<SalesOrderNotFoundErrorResponse> handleSalesOrderNotFound(
            SalesOrderNotFoundException ex, WebRequest request) {

        log.error("Sales order not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                SalesOrderNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .salesOrderId(ex.getSalesOrderId())
                        .soNumber(ex.getSoNumber())
                        .build());
    }

    @ExceptionHandler(PurchaseOrderNotFoundException.class)
    public ResponseEntity<PurchaseOrderNotFoundErrorResponse> handlePurchaseOrderNotFound(
            PurchaseOrderNotFoundException ex, WebRequest request) {

        log.error("Purchase order not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                PurchaseOrderNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .purchaseOrderId(ex.getPurchaseOrderId())
                        .poNumber(ex.getPoNumber())
                        .build());
    }

    @ExceptionHandler(InvalidOrderStatusTransitionException.class)
    public ResponseEntity<InvalidStatusTransitionErrorResponse> handleInvalidStatusTransition(
            InvalidOrderStatusTransitionException ex, WebRequest request) {

        log.error("Invalid order status transition: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                InvalidStatusTransitionErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .orderType(ex.getOrderType())
                        .currentStatus(ex.getCurrentStatus())
                        .targetStatus(ex.getTargetStatus())
                        .build());
    }

    @ExceptionHandler(OrderNotEditableException.class)
    public ResponseEntity<OrderNotEditableErrorResponse> handleOrderNotEditable(
            OrderNotEditableException ex, WebRequest request) {

        log.error("Order not editable: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                OrderNotEditableErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .orderType(ex.getOrderType())
                        .orderId(ex.getOrderId())
                        .currentStatus(ex.getCurrentStatus())
                        .build());
    }

    @ExceptionHandler(PurchaseOrderReceiptException.class)
    public ResponseEntity<PurchaseOrderReceiptErrorResponse> handlePurchaseOrderReceipt(
            PurchaseOrderReceiptException ex, WebRequest request) {

        log.error("Purchase order receipt violation: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                PurchaseOrderReceiptErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .itemId(ex.getItemId())
                        .alreadyReceived(ex.getAlreadyReceived())
                        .newReceiptQuantity(ex.getNewReceiptQuantity())
                        .totalReceived(ex.getTotalReceived())
                        .quantityOrdered(ex.getQuantityOrdered())
                        .build());
    }

    @ExceptionHandler(PurchaseOrderItemNotFoundException.class)
    public ResponseEntity<PurchaseOrderItemNotFoundErrorResponse> handlePurchaseOrderItemNotFound(
            PurchaseOrderItemNotFoundException ex, WebRequest request) {

        log.error("Purchase order item not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                PurchaseOrderItemNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .itemId(ex.getItemId())
                        .purchaseOrderId(ex.getPurchaseOrderId())
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
    @Schema(description = "Sales order not found error response")
    public static class SalesOrderNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Sales order ID that was not found", example = "55")
        private Long salesOrderId;
        @Schema(description = "SO number that was not found", example = "SO-20250101-0001")
        private String soNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Purchase order not found error response")
    public static class PurchaseOrderNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Purchase order ID that was not found", example = "12")
        private Long purchaseOrderId;
        @Schema(description = "PO number that was not found", example = "PO-20250101-0001")
        private String poNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Invalid order status transition error response")
    public static class InvalidStatusTransitionErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Order type", example = "SalesOrder")
        private String orderType;
        @Schema(description = "Current order status", example = "PENDING")
        private String currentStatus;
        @Schema(description = "Target status that was rejected", example = "SHIPPED")
        private String targetStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Order not editable error response")
    public static class OrderNotEditableErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Order type", example = "PurchaseOrder")
        private String orderType;
        @Schema(description = "Order ID", example = "12")
        private Long orderId;
        @Schema(description = "Current status that prevents editing", example = "APPROVED")
        private String currentStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Purchase order receipt error response")
    public static class PurchaseOrderReceiptErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "PO line item ID", example = "8")
        private Long itemId;
        @Schema(description = "Quantity already received before this receipt", example = "40")
        private int alreadyReceived;
        @Schema(description = "Quantity being received in this request", example = "30")
        private int newReceiptQuantity;
        @Schema(description = "Total received after this request", example = "70")
        private int totalReceived;
        @Schema(description = "Original ordered quantity", example = "50")
        private int quantityOrdered;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Purchase order item not found error response")
    public static class PurchaseOrderItemNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "PO line item ID that was not found", example = "8")
        private Long itemId;
        @Schema(description = "Purchase order ID it was looked up against", example = "12")
        private Long purchaseOrderId;
    }
}