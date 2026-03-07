package com.moeware.ims.controller.transaction;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.transaction.CancelOrderRequest;
import com.moeware.ims.dto.transaction.purchaseOrder.PurchaseOrderRequest;
import com.moeware.ims.dto.transaction.purchaseOrder.PurchaseOrderResponse;
import com.moeware.ims.dto.transaction.purchaseOrder.PurchaseOrderSummaryResponse;
import com.moeware.ims.dto.transaction.purchaseOrder.ReceivePurchaseOrderRequest;
import com.moeware.ims.enums.transaction.PurchaseOrderStatus;
import com.moeware.ims.exception.GlobalExceptionHandler;
import com.moeware.ims.service.transaction.PurchaseOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for Purchase Order management
 * Handles the full DRAFT -> SUBMITTED -> APPROVED -> RECEIVED workflow
 */
@RestController
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Purchase Orders", description = "Purchase order management and workflow APIs")
@SecurityRequirement(name = "bearerAuth")
public class PurchaseOrderController {

        private final PurchaseOrderService purchaseOrderService;

        // ==================== READ ENDPOINTS ====================

        @Operation(summary = "Get all purchase orders", description = "Returns a paginated list of purchase orders with optional filters")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "List returned successfully"),
                        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<Page<PurchaseOrderSummaryResponse>>> getAllPurchaseOrders(
                        @Parameter(description = "Search by PO number") @RequestParam(required = false) String search,
                        @Parameter(description = "Filter by supplier ID") @RequestParam(required = false) Long supplierId,
                        @Parameter(description = "Filter by warehouse ID") @RequestParam(required = false) Long warehouseId,
                        @Parameter(description = "Filter by status") @RequestParam(required = false) PurchaseOrderStatus status,
                        @Parameter(description = "Filter by creator user ID") @RequestParam(required = false) Long createdBy,
                        @Parameter(description = "Start date filter (inclusive), format: yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @Parameter(description = "End date filter (inclusive), format: yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                        @PageableDefault(size = 20, sort = "orderDate") Pageable pageable) {

                Page<PurchaseOrderSummaryResponse> result = purchaseOrderService.getAllPurchaseOrders(
                                search, supplierId, warehouseId, status, createdBy, startDate, endDate, pageable);

                return ResponseEntity.ok(ApiResponseWpp.success(result, "Purchase orders retrieved successfully"));
        }

        @Operation(summary = "Get purchase order by ID", description = "Returns full details of a specific purchase order including all line items")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Purchase order found"),
                        @ApiResponse(responseCode = "404", description = "Purchase order not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<PurchaseOrderResponse>> getPurchaseOrderById(
                        @Parameter(description = "Purchase order ID", required = true) @PathVariable Long id) {

                PurchaseOrderResponse response = purchaseOrderService.getPurchaseOrderById(id);
                return ResponseEntity.ok(ApiResponseWpp.success(response, "Purchase order retrieved successfully"));
        }

        @Operation(summary = "Get purchase orders pending approval", description = "Returns all purchase orders in SUBMITTED status awaiting manager approval")
        @ApiResponse(responseCode = "200", description = "Pending orders returned successfully")
        @GetMapping("/pending-approval")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<Page<PurchaseOrderSummaryResponse>>> getPendingApprovalOrders(
                        @PageableDefault(size = 20, sort = "orderDate") Pageable pageable) {

                Page<PurchaseOrderSummaryResponse> result = purchaseOrderService.getPendingApprovalOrders(pageable);
                return ResponseEntity
                                .ok(ApiResponseWpp.success(result, "Pending approval orders retrieved successfully"));
        }

        // ==================== WRITE ENDPOINTS ====================

        @Operation(summary = "Create purchase order", description = "Creates a new purchase order in DRAFT status. Order number is auto-generated.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Purchase order created"),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Supplier, warehouse, or product not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<PurchaseOrderResponse>> createPurchaseOrder(
                        @Valid @RequestBody PurchaseOrderRequest request,
                        // In a real implementation, extract from SecurityContext:
                        // @AuthenticationPrincipal UserDetails userDetails
                        // For now, accept as a header param or derive from security context
                        @Parameter(description = "ID of the user creating the order (from auth token)") @RequestParam Long createdByUserId) {

                PurchaseOrderResponse response = purchaseOrderService.createPurchaseOrder(request, createdByUserId);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponseWpp.success(response, "Purchase order created successfully"));
        }

        @Operation(summary = "Update purchase order", description = "Updates a purchase order. Only allowed when status is DRAFT.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Purchase order updated"),
                        @ApiResponse(responseCode = "400", description = "Validation error or order not in DRAFT status", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Purchase order not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<PurchaseOrderResponse>> updatePurchaseOrder(
                        @Parameter(description = "Purchase order ID", required = true) @PathVariable Long id,
                        @Valid @RequestBody PurchaseOrderRequest request) {

                PurchaseOrderResponse response = purchaseOrderService.updatePurchaseOrder(id, request);
                return ResponseEntity.ok(ApiResponseWpp.success(response, "Purchase order updated successfully"));
        }

        @Operation(summary = "Submit purchase order for approval", description = "Transitions a DRAFT purchase order to SUBMITTED status for manager approval.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Purchase order submitted"),
                        @ApiResponse(responseCode = "400", description = "Order is not in DRAFT status", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Purchase order not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PatchMapping("/{id}/submit")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<PurchaseOrderResponse>> submitPurchaseOrder(
                        @Parameter(description = "Purchase order ID", required = true) @PathVariable Long id) {

                PurchaseOrderResponse response = purchaseOrderService.submitPurchaseOrder(id);

                return ResponseEntity.ok(ApiResponseWpp.success(response, "Purchase order submitted for approval"));
        }

        @Operation(summary = "Approve purchase order", description = "Approves a SUBMITTED purchase order. Requires MANAGER or ADMIN role.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Purchase order approved"),
                        @ApiResponse(responseCode = "400", description = "Order is not in SUBMITTED status", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Insufficient role", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PatchMapping("/{id}/approve")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<PurchaseOrderResponse>> approvePurchaseOrder(
                        @Parameter(description = "Purchase order ID", required = true) @PathVariable Long id) {

                PurchaseOrderResponse response = purchaseOrderService.approvePurchaseOrder(id);
                return ResponseEntity.ok(ApiResponseWpp.success(response, "Purchase order approved successfully"));
        }

        @Operation(summary = "Reject purchase order", description = "Rejects a SUBMITTED purchase order, returning it to DRAFT for revision. Requires MANAGER or ADMIN role.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Purchase order rejected and returned to DRAFT"),
                        @ApiResponse(responseCode = "400", description = "Order is not in SUBMITTED status", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PatchMapping("/{id}/reject")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<PurchaseOrderResponse>> rejectPurchaseOrder(
                        @Parameter(description = "Purchase order ID", required = true) @PathVariable Long id,
                        @Valid @RequestBody CancelOrderRequest request) {

                PurchaseOrderResponse response = purchaseOrderService.rejectPurchaseOrder(id, request.getReason());
                return ResponseEntity.ok(ApiResponseWpp.success(response, "Purchase order rejected"));
        }

        @Operation(summary = "Receive purchase order", description = "Marks an APPROVED purchase order as RECEIVED and records actual quantities received. Supports partial receipts.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Purchase order received successfully"),
                        @ApiResponse(responseCode = "400", description = "Order is not in APPROVED status or quantity exceeds ordered", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Purchase order or item not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PostMapping("/{id}/receive")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
        public ResponseEntity<ApiResponseWpp<PurchaseOrderResponse>> receivePurchaseOrder(
                        @Parameter(description = "Purchase order ID", required = true) @PathVariable Long id,
                        @Parameter(description = "ID of the warehouse staff receiving the goods — stamped on every inventory movement record created during receipt") @RequestParam Long performedByUserId,
                        @Valid @RequestBody ReceivePurchaseOrderRequest request) {

                PurchaseOrderResponse response = purchaseOrderService.receivePurchaseOrder(id, request,
                                performedByUserId);
                return ResponseEntity.ok(ApiResponseWpp.success(response, "Purchase order received successfully"));
        }

        @Operation(summary = "Cancel purchase order", description = "Cancels a purchase order. Not allowed once it has been RECEIVED.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Purchase order cancelled"),
                        @ApiResponse(responseCode = "400", description = "Order cannot be cancelled in current status", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PatchMapping("/{id}/cancel")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<PurchaseOrderResponse>> cancelPurchaseOrder(
                        @Parameter(description = "Purchase order ID", required = true) @PathVariable Long id,
                        @Valid @RequestBody CancelOrderRequest request) {

                PurchaseOrderResponse response = purchaseOrderService.cancelPurchaseOrder(id, request);
                return ResponseEntity.ok(ApiResponseWpp.success(response, "Purchase order cancelled successfully"));
        }

        @Operation(summary = "Delete purchase order", description = "Permanently deletes a purchase order. Only allowed when status is DRAFT.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Purchase order deleted"),
                        @ApiResponse(responseCode = "400", description = "Order is not in DRAFT status", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Purchase order not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<Void> deletePurchaseOrder(
                        @Parameter(description = "Purchase order ID", required = true) @PathVariable Long id) {

                purchaseOrderService.deletePurchaseOrder(id);
                return ResponseEntity.noContent().build();
        }
}