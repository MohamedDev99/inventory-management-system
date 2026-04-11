package com.moeware.ims.controller.transaction;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentApproveRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentRejectRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentResponse;
import com.moeware.ims.enums.transaction.AdjustmentReason;
import com.moeware.ims.enums.transaction.StockAdjustmentStatus;
import com.moeware.ims.exception.handler.GlobalExceptionHandler;
import com.moeware.ims.service.transaction.StockAdjustmentService;

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
 * REST controller for stock adjustment CRUD and approval workflow.
 *
 * <p>
 * Base path: {@code /api/stock-adjustments}
 * </p>
 *
 * <pre>
 * Workflow:
 *   POST /              → create (status = PENDING)
 *   PATCH /{id}/approve → approve (status = APPROVED, inventory updated)
 *   PATCH /{id}/reject  → reject  (status = REJECTED, no inventory change)
 * </pre>
 *
 * @author MoeWare Team
 */
@RestController
@RequestMapping("/api/stock-adjustments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Stock Adjustments", description = "Manage stock adjustment requests and approval workflow")
@SecurityRequirement(name = "bearerAuth")
public class StockAdjustmentController {

        private final StockAdjustmentService stockAdjustmentService;

        // ==================== READ ENDPOINTS ====================

        @Operation(summary = "Get all stock adjustments", description = "Returns a paginated list of stock adjustments with optional filters.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "List returned successfully"),
                        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<Page<StockAdjustmentResponse>>> getAllAdjustments(
                        @Parameter(description = "Filter by product ID") @RequestParam(required = false) Long productId,
                        @Parameter(description = "Filter by warehouse ID") @RequestParam(required = false) Long warehouseId,
                        @Parameter(description = "Filter by status: PENDING, APPROVED, REJECTED") @RequestParam(required = false) StockAdjustmentStatus status,
                        @Parameter(description = "Filter by reason: DAMAGED, EXPIRED, THEFT, COUNT_ERROR, RETURN, OTHER") @RequestParam(required = false) AdjustmentReason reason,
                        @Parameter(description = "Start of date range (ISO-8601), e.g. 2026-01-01T00:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                        @Parameter(description = "End of date range (ISO-8601), e.g. 2026-02-09T23:59:59") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                        @PageableDefault(size = 20, sort = "adjustmentDate") Pageable pageable) {

                log.info("GET /api/stock-adjustments - productId={}, warehouseId={}, status={}, reason={}",
                                productId, warehouseId, status, reason);

                return ResponseEntity.ok(ApiResponseWpp.success(
                                stockAdjustmentService.getAllAdjustments(
                                                productId, warehouseId, status, reason, startDate, endDate, pageable),
                                "Stock adjustments retrieved successfully"));
        }

        @Operation(summary = "Get stock adjustment by ID", description = "Returns a single stock adjustment record with full details.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Stock adjustment found"),
                        @ApiResponse(responseCode = "404", description = "Stock adjustment not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<StockAdjustmentResponse>> getAdjustmentById(
                        @Parameter(description = "Adjustment ID", required = true) @PathVariable Long id) {

                log.info("GET /api/stock-adjustments/{}", id);
                return ResponseEntity.ok(ApiResponseWpp.success(
                                stockAdjustmentService.getAdjustmentById(id),
                                "Stock adjustment retrieved successfully"));
        }

        @Operation(summary = "Get pending stock adjustments", description = "Returns all stock adjustments awaiting approval, ordered by adjustment date ascending.")
        @ApiResponse(responseCode = "200", description = "Pending adjustments returned successfully")
        @GetMapping("/pending")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<List<StockAdjustmentResponse>>> getPendingAdjustments() {
                log.info("GET /api/stock-adjustments/pending");
                return ResponseEntity.ok(ApiResponseWpp.success(
                                stockAdjustmentService.getPendingAdjustments(),
                                "Pending stock adjustments retrieved successfully"));
        }

        @Operation(summary = "Get available adjustment reasons", description = "Returns the list of valid reason codes: DAMAGED, EXPIRED, THEFT, COUNT_ERROR, RETURN, OTHER.")
        @ApiResponse(responseCode = "200", description = "Reasons returned successfully")
        @GetMapping("/reasons")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<List<AdjustmentReason>>> getAdjustmentReasons() {
                log.info("GET /api/stock-adjustments/reasons");
                return ResponseEntity.ok(ApiResponseWpp.success(
                                stockAdjustmentService.getAdjustmentReasons(),
                                "Adjustment reasons retrieved successfully"));
        }

        // ==================== WRITE ENDPOINTS ====================

        @Operation(summary = "Create a stock adjustment request", description = "Creates a new adjustment in PENDING status. "
                        +
                        "Inventory is NOT modified until a manager approves the request.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Stock adjustment created"),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Product, warehouse, or user not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "409", description = "Adjustment would result in negative stock", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
        public ResponseEntity<ApiResponseWpp<StockAdjustmentResponse>> createAdjustment(
                        @Valid @RequestBody StockAdjustmentRequest request) {

                log.info("POST /api/stock-adjustments - product={}, warehouse={}, change={}",
                                request.getProductId(), request.getWarehouseId(), request.getQuantityChange());

                return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseWpp.success(
                                stockAdjustmentService.createAdjustment(request),
                                "Stock adjustment created successfully"));
        }

        @Operation(summary = "Approve a stock adjustment", description = "Approves a PENDING stock adjustment. " +
                        "The inventory quantity is updated and a movement record of type ADJUSTMENT is created. " +
                        "Requires MANAGER or ADMIN role.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Stock adjustment approved and inventory updated"),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Insufficient role", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Adjustment or approver not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "409", description = "Adjustment already processed or stock insufficient at approval time", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PatchMapping("/{id}/approve")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<StockAdjustmentResponse>> approveAdjustment(
                        @Parameter(description = "Adjustment ID", required = true) @PathVariable Long id,
                        @Valid @RequestBody StockAdjustmentApproveRequest request,
                        Authentication authentication) {

                log.info("PATCH /api/v1/stock-adjustments/{}/approve - actor={}", id, authentication.getName());
                return ResponseEntity.ok(ApiResponseWpp.success(
                                stockAdjustmentService.approveAdjustment(id, request, authentication),
                                "Stock adjustment approved successfully"));
        }

        @Operation(summary = "Reject a stock adjustment", description = "Rejects a PENDING stock adjustment. " +
                        "No inventory change is made. The rejection reason is recorded in the adjustment notes. " +
                        "Requires MANAGER or ADMIN role.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Stock adjustment rejected"),
                        @ApiResponse(responseCode = "400", description = "Validation error or rejection reason missing", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Insufficient role", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Adjustment or rejector not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "409", description = "Adjustment already processed", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PatchMapping("/{id}/reject")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<StockAdjustmentResponse>> rejectAdjustment(
                        @Parameter(description = "Adjustment ID", required = true) @PathVariable Long id,
                        @Valid @RequestBody StockAdjustmentRejectRequest request,
                        Authentication authentication) {

                log.info("PATCH /api/v1/stock-adjustments/{}/reject - actor={}", id, authentication.getName());
                return ResponseEntity.ok(ApiResponseWpp.success(
                                stockAdjustmentService.rejectAdjustment(id, request, authentication),
                                "Stock adjustment rejected successfully"));
        }
}