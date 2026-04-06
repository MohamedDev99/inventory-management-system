package com.moeware.ims.exception.handler.transaction;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.transaction.shipment.SalesOrderNotFulfilledException;
import com.moeware.ims.exception.transaction.shipment.ShipmentAlreadyTerminatedException;
import com.moeware.ims.exception.transaction.shipment.ShipmentDeliveryEndpointRequiredException;
import com.moeware.ims.exception.transaction.shipment.ShipmentNotEligibleForDeliveryException;
import com.moeware.ims.exception.transaction.shipment.ShipmentNotFoundException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all shipment-domain exceptions.
 *
 * All five exceptions carry structured fields and get dedicated handler
 * methods.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class ShipmentExceptionHandler {

    @ExceptionHandler(ShipmentNotFoundException.class)
    public ResponseEntity<ShipmentNotFoundErrorResponse> handleShipmentNotFound(
            ShipmentNotFoundException ex, WebRequest request) {

        log.error("Shipment not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                ShipmentNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .shipmentId(ex.getShipmentId())
                        .shipmentNumber(ex.getShipmentNumber())
                        .build());
    }

    @ExceptionHandler(SalesOrderNotFulfilledException.class)
    public ResponseEntity<SalesOrderNotFulfilledErrorResponse> handleSalesOrderNotFulfilled(
            SalesOrderNotFulfilledException ex, WebRequest request) {

        log.error("Sales order not fulfilled for shipment creation: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                SalesOrderNotFulfilledErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .salesOrderId(ex.getSalesOrderId())
                        .soNumber(ex.getSoNumber())
                        .currentStatus(ex.getCurrentStatus().name())
                        .build());
    }

    @ExceptionHandler(ShipmentAlreadyTerminatedException.class)
    public ResponseEntity<ShipmentTerminatedErrorResponse> handleShipmentAlreadyTerminated(
            ShipmentAlreadyTerminatedException ex, WebRequest request) {

        log.error("Shipment already terminated: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                ShipmentTerminatedErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .shipmentId(ex.getShipmentId())
                        .currentStatus(ex.getCurrentStatus().name())
                        .build());
    }

    @ExceptionHandler(ShipmentNotEligibleForDeliveryException.class)
    public ResponseEntity<ShipmentNotEligibleErrorResponse> handleShipmentNotEligibleForDelivery(
            ShipmentNotEligibleForDeliveryException ex, WebRequest request) {

        log.error("Shipment not eligible for delivery: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                ShipmentNotEligibleErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .shipmentId(ex.getShipmentId())
                        .currentStatus(ex.getCurrentStatus().name())
                        .build());
    }

    @ExceptionHandler(ShipmentDeliveryEndpointRequiredException.class)
    public ResponseEntity<ShipmentDeliveryEndpointErrorResponse> handleDeliveryEndpointRequired(
            ShipmentDeliveryEndpointRequiredException ex, WebRequest request) {

        log.warn("Wrong endpoint used for shipment delivery: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                ShipmentDeliveryEndpointErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .shipmentId(ex.getShipmentId())
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
    @Schema(description = "Shipment not found error response")
    public static class ShipmentNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Shipment ID that was not found", example = "9")
        private Long shipmentId;
        @Schema(description = "Shipment number that was not found", example = "SHIP-20250301-0001")
        private String shipmentNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Sales order not fulfilled error response")
    public static class SalesOrderNotFulfilledErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Sales order ID", example = "55")
        private Long salesOrderId;
        @Schema(description = "SO number", example = "SO-20250101-0001")
        private String soNumber;
        @Schema(description = "Current status of the sales order", example = "CONFIRMED")
        private String currentStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shipment already terminated error response")
    public static class ShipmentTerminatedErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Shipment ID", example = "9")
        private Long shipmentId;
        @Schema(description = "Terminal status the shipment is in", example = "DELIVERED")
        private String currentStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Shipment not eligible for delivery error response")
    public static class ShipmentNotEligibleErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Shipment ID", example = "9")
        private Long shipmentId;
        @Schema(description = "Current status that prevents delivery", example = "RETURNED")
        private String currentStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Wrong endpoint used for delivery error response")
    public static class ShipmentDeliveryEndpointErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Shipment ID the caller attempted to deliver", example = "9")
        private Long shipmentId;
    }
}