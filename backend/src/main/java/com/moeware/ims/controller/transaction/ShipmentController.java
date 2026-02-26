package com.moeware.ims.controller.transaction;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.transaction.shipment.DeliverShipmentRequest;
import com.moeware.ims.dto.transaction.shipment.ShipmentRequest;
import com.moeware.ims.dto.transaction.shipment.ShipmentResponse;
import com.moeware.ims.dto.transaction.shipment.UpdateShipmentStatusRequest;
import com.moeware.ims.enums.transaction.ShipmentStatus;
import com.moeware.ims.service.transaction.ShipmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing shipments.
 * Base path: /api/v1/shipments
 */
@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipments", description = "Shipment tracking and delivery management for sales orders")
public class ShipmentController {

        private final ShipmentService shipmentService;

        // ─── LIST / SEARCH ───────────────────────────────────────────────────────

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE_STAFF','VIEWER')")
        @Operation(summary = "List all shipments", description = "Returns a paginated list of shipments with optional filtering.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Shipments retrieved successfully")
        })
        public ResponseEntity<ApiResponseWpp<Page<ShipmentResponse>>> getAllShipments(
                        @Parameter(description = "Filter by origin warehouse ID") @RequestParam(required = false) Long warehouseId,
                        @Parameter(description = "Filter by shipment status") @RequestParam(required = false) ShipmentStatus status,
                        @Parameter(description = "Filter by carrier name (partial match)") @RequestParam(required = false) String carrier,
                        Pageable pageable) {
                return ResponseEntity.ok(ApiResponseWpp.success(
                                shipmentService.getAllShipments(warehouseId, status, carrier, pageable)));
        }

        @GetMapping("/pending")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE_STAFF')")
        @Operation(summary = "Get pending shipments", description = "Returns all shipments with PENDING or IN_TRANSIT status.")
        public ResponseEntity<ApiResponseWpp<List<ShipmentResponse>>> getPendingShipments() {
                return ResponseEntity.ok(ApiResponseWpp.success(shipmentService.getPendingShipments()));
        }

        @GetMapping("/overdue")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE_STAFF')")
        @Operation(summary = "Get overdue shipments", description = "Returns all shipments past their estimated delivery date that have not been delivered.")
        public ResponseEntity<ApiResponseWpp<List<ShipmentResponse>>> getOverdueShipments() {
                return ResponseEntity.ok(ApiResponseWpp.success(shipmentService.getOverdueShipments()));
        }

        // ─── SINGLE RESOURCE ─────────────────────────────────────────────────────

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE_STAFF','VIEWER')")
        @Operation(summary = "Get shipment by ID", description = "Returns full shipment details by internal ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Shipment retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Shipment not found")
        })
        public ResponseEntity<ApiResponseWpp<ShipmentResponse>> getShipmentById(
                        @PathVariable Long id) {
                return ResponseEntity.ok(ApiResponseWpp.success(shipmentService.getShipmentById(id)));
        }

        @GetMapping("/tracking/{trackingNumber}")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE_STAFF','VIEWER')")
        @Operation(summary = "Get shipment by tracking number", description = "Looks up a shipment using the carrier-provided tracking number.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Shipment retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Shipment not found")
        })
        public ResponseEntity<ApiResponseWpp<ShipmentResponse>> getShipmentByTrackingNumber(
                        @Parameter(description = "Carrier tracking number", example = "1Z999AA10123456784") @PathVariable String trackingNumber) {
                return ResponseEntity.ok(ApiResponseWpp.success(
                                shipmentService.getShipmentByTrackingNumber(trackingNumber)));
        }

        @GetMapping("/sales-order/{salesOrderId}")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE_STAFF','VIEWER')")
        @Operation(summary = "Get shipments by sales order", description = "Returns all shipments associated with a specific sales order.")
        public ResponseEntity<ApiResponseWpp<List<ShipmentResponse>>> getShipmentsBySalesOrder(
                        @Parameter(description = "Sales order ID") @PathVariable Long salesOrderId) {
                return ResponseEntity.ok(ApiResponseWpp.success(
                                shipmentService.getShipmentsBySalesOrder(salesOrderId)));
        }

        // ─── CREATE ──────────────────────────────────────────────────────────────

        @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE_STAFF')")
        @Operation(summary = "Create shipment", description = "Creates a new shipment for a FULFILLED sales order. "
                        + "Automatically advances the sales order to SHIPPED status.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Shipment created successfully"),
                        @ApiResponse(responseCode = "400", description = "Validation error or sales order not in FULFILLED status"),
                        @ApiResponse(responseCode = "404", description = "Sales order, warehouse, or user not found")
        })
        public ResponseEntity<ApiResponseWpp<ShipmentResponse>> createShipment(
                        @Valid @RequestBody ShipmentRequest request,
                        @Parameter(description = "ID of the warehouse staff receiving the goods — stamped on every inventory movement record created during receipt") @RequestParam Long shippedByUserId) {
                return ResponseEntity.status(HttpStatus.CREATED).body(
                                ApiResponseWpp.success(shipmentService.createShipment(request, shippedByUserId)));
        }

        // ─── STATUS TRANSITIONS ──────────────────────────────────────────────────

        @PatchMapping("/{id}/status")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE_STAFF')")
        @Operation(summary = "Update shipment status", description = "Updates the current status of a shipment (e.g., PENDING → IN_TRANSIT). "
                        + "Use the /deliver endpoint to mark as DELIVERED.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Shipment status updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
                        @ApiResponse(responseCode = "404", description = "Shipment not found")
        })
        public ResponseEntity<ApiResponseWpp<ShipmentResponse>> updateShipmentStatus(
                        @PathVariable Long id,
                        @Valid @RequestBody UpdateShipmentStatusRequest request) {
                return ResponseEntity.ok(ApiResponseWpp.success(
                                shipmentService.updateShipmentStatus(id, request)));
        }

        @PatchMapping("/{id}/deliver")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE_STAFF')")
        @Operation(summary = "Mark shipment as delivered", description = "Records the actual delivery date and advances shipment to DELIVERED status. "
                        + "Also advances the linked sales order to DELIVERED.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Shipment marked as delivered"),
                        @ApiResponse(responseCode = "400", description = "Shipment cannot be delivered in current state"),
                        @ApiResponse(responseCode = "404", description = "Shipment not found")
        })
        public ResponseEntity<ApiResponseWpp<ShipmentResponse>> deliverShipment(
                        @PathVariable Long id,
                        @Valid @RequestBody DeliverShipmentRequest request) {
                return ResponseEntity.ok(ApiResponseWpp.success(
                                shipmentService.deliverShipment(id, request)));
        }
}