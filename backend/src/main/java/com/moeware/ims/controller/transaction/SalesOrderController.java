package com.moeware.ims.controller.transaction;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.moeware.ims.dto.transaction.salesOrder.SalesOrderRequest;
import com.moeware.ims.dto.transaction.salesOrder.SalesOrderResponse;
import com.moeware.ims.dto.transaction.salesOrder.SalesOrderSummaryResponse;
import com.moeware.ims.enums.transaction.SalesOrderStatus;
import com.moeware.ims.exception.GlobalExceptionHandler;
import com.moeware.ims.service.transaction.SalesOrderService;

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
 * REST controller for Sales Order management
 * Handles the full PENDING -> CONFIRMED -> FULFILLED -> SHIPPED -> DELIVERED
 * workflow
 */
@RestController
@RequestMapping("/api/v1/sales-orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sales Orders", description = "Sales order management and fulfillment workflow APIs")
@SecurityRequirement(name = "bearerAuth")
public class SalesOrderController {

        private final SalesOrderService salesOrderService;

        // ==================== READ ENDPOINTS ====================

        @Operation(summary = "Get all sales orders", description = "Returns a paginated list of sales orders with optional filters by status, customer, warehouse, and date range")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "List returned successfully"),
                        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<Page<SalesOrderSummaryResponse>>> getAllSalesOrders(
                        @Parameter(description = "Search by SO number or customer name") @RequestParam(required = false) String search,
                        @Parameter(description = "Filter by customer ID") @RequestParam(required = false) Long customerId,
                        @Parameter(description = "Filter by warehouse ID") @RequestParam(required = false) Long warehouseId,
                        @Parameter(description = "Filter by status") @RequestParam(required = false) SalesOrderStatus status,
                        @Parameter(description = "Filter by creator user ID") @RequestParam(required = false) Long createdBy,
                        @Parameter(description = "Start date filter (inclusive), format: yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @Parameter(description = "End date filter (inclusive), format: yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                        @PageableDefault(size = 20, sort = "orderDate") Pageable pageable) {

                Page<SalesOrderSummaryResponse> result = salesOrderService.getAllSalesOrders(
                                search, customerId, warehouseId, status, createdBy, startDate, endDate, pageable);

                return ResponseEntity.ok(ApiResponseWpp.success(result, "Sales orders retrieved successfully"));
        }

        @Operation(summary = "Get sales order by ID", description = "Returns full details of a specific sales order including all line items and workflow dates")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Sales order found"),
                        @ApiResponse(responseCode = "404", description = "Sales order not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<SalesOrderResponse>> getSalesOrderById(
                        @Parameter(description = "Sales order ID", required = true) @PathVariable Long id) {

                SalesOrderResponse response = salesOrderService.getSalesOrderById(id);
                return ResponseEntity.ok(ApiResponseWpp.success(response, "Sales order retrieved successfully"));
        }

        // ==================== WRITE ENDPOINTS ====================

        @Operation(summary = "Create sales order", description = "Creates a new sales order in PENDING status. Order number is auto-generated in format SO-YYYYMMDD-SEQUENCE.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Sales order created"),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Customer, warehouse, or product not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<SalesOrderResponse>> createSalesOrder(
                        @Valid @RequestBody SalesOrderRequest request,
                        @Parameter(description = "ID of the user creating the order (from auth token)") @RequestParam Long createdByUserId) {

                SalesOrderResponse response = salesOrderService.createSalesOrder(request, createdByUserId);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponseWpp.success(response, "Sales order created successfully"));
        }

        @Operation(summary = "Update sales order", description = "Updates a sales order. Only allowed when status is PENDING.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Sales order updated"),
                        @ApiResponse(responseCode = "400", description = "Validation error or order not in PENDING status", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Sales order not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<SalesOrderResponse>> updateSalesOrder(
                        @Parameter(description = "Sales order ID", required = true) @PathVariable Long id,
                        @Valid @RequestBody SalesOrderRequest request) {

                SalesOrderResponse response = salesOrderService.updateSalesOrder(id, request);
                return ResponseEntity.ok(ApiResponseWpp.success(response, "Sales order updated successfully"));
        }

        @Operation(summary = "Confirm sales order", description = "Transitions a PENDING order to CONFIRMED, reserving inventory in the assigned warehouse.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Sales order confirmed"),
                        @ApiResponse(responseCode = "400", description = "Order is not in PENDING status", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PatchMapping("/{id}/confirm")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<SalesOrderResponse>> confirmSalesOrder(
                        @Parameter(description = "Sales order ID", required = true) @PathVariable Long id) {

                SalesOrderResponse response = salesOrderService.confirmSalesOrder(id);
                return ResponseEntity
                                .ok(ApiResponseWpp.success(response, "Sales order confirmed and inventory reserved"));
        }

        @Operation(summary = "Fulfill sales order", description = "Transitions a CONFIRMED order to FULFILLED (picked and packed). Deducts inventory and creates movement records.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Sales order fulfilled"),
                        @ApiResponse(responseCode = "400", description = "Order is not in CONFIRMED status", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PatchMapping("/{id}/fulfill")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
        public ResponseEntity<ApiResponseWpp<SalesOrderResponse>> fulfillSalesOrder(
                        @Parameter(description = "Sales order ID", required = true) @PathVariable Long id,
                        @Parameter(description = "ID of the warehouse staff or manager performing the fulfillment and deducting inventory") @RequestParam Long performedByUserId) {

                SalesOrderResponse response = salesOrderService.fulfillSalesOrder(id, performedByUserId);
                return ResponseEntity.ok(ApiResponseWpp.success(response, "Sales order fulfilled successfully"));
        }

        @Operation(summary = "Mark sales order as shipped", description = "Transitions a FULFILLED order to SHIPPED. Records shipping date.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Sales order marked as shipped"),
                        @ApiResponse(responseCode = "400", description = "Order is not in FULFILLED status", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PatchMapping("/{id}/ship")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
        public ResponseEntity<ApiResponseWpp<SalesOrderResponse>> shipSalesOrder(
                        @Parameter(description = "Sales order ID", required = true) @PathVariable Long id) {

                SalesOrderResponse response = salesOrderService.shipSalesOrder(id);
                return ResponseEntity.ok(ApiResponseWpp.success(response, "Sales order shipped successfully"));
        }

        @Operation(summary = "Mark sales order as delivered", description = "Transitions a SHIPPED order to DELIVERED. Records delivery date.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Sales order marked as delivered"),
                        @ApiResponse(responseCode = "400", description = "Order is not in SHIPPED status", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PatchMapping("/{id}/deliver")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
        public ResponseEntity<ApiResponseWpp<SalesOrderResponse>> deliverSalesOrder(
                        @Parameter(description = "Sales order ID", required = true) @PathVariable Long id) {

                SalesOrderResponse response = salesOrderService.deliverSalesOrder(id);
                return ResponseEntity.ok(ApiResponseWpp.success(response, "Sales order delivered successfully"));
        }

        @Operation(summary = "Cancel sales order", description = "Cancels a sales order. Not allowed once the order has been SHIPPED or DELIVERED. Releases reserved inventory if applicable.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Sales order cancelled"),
                        @ApiResponse(responseCode = "400", description = "Order cannot be cancelled in current status (already shipped or delivered)", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PatchMapping("/{id}/cancel")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<SalesOrderResponse>> cancelSalesOrder(
                        @Parameter(description = "Sales order ID", required = true) @PathVariable Long id,
                        @Parameter(description = "ID of the user cancelling the order — required to record the inventory reversal movement if order was already fulfilled") @RequestParam Long performedByUserId,
                        @Valid @RequestBody CancelOrderRequest request) {

                SalesOrderResponse response = salesOrderService.cancelSalesOrder(id, request.getReason(),
                                performedByUserId);
                return ResponseEntity.ok(ApiResponseWpp.success(response, "Sales order cancelled successfully"));
        }

}