package com.moeware.ims.controller.transaction;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentApproveRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentRejectRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentResponse;
import com.moeware.ims.enums.transaction.AdjustmentReason;
import com.moeware.ims.enums.transaction.StockAdjustmentStatus;
import com.moeware.ims.service.transaction.StockAdjustmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 *   POST /             → create (status = PENDING)
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
public class StockAdjustmentController {

    private final StockAdjustmentService stockAdjustmentService;

    // ----------------------------------------------------------------
    // POST /api/stock-adjustments
    // ----------------------------------------------------------------

    @PostMapping
    @Operation(summary = "Create a stock adjustment request", description = "Creates a new adjustment in PENDING status. "
            +
            "Inventory is NOT modified until a manager approves the request.")
    public ResponseEntity<StockAdjustmentResponse> createAdjustment(
            @Valid @RequestBody StockAdjustmentRequest request) {

        log.info("POST /api/stock-adjustments - product={}, warehouse={}, change={}",
                request.getProductId(), request.getWarehouseId(), request.getQuantityChange());

        StockAdjustmentResponse response = stockAdjustmentService.createAdjustment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ----------------------------------------------------------------
    // GET /api/stock-adjustments
    // ----------------------------------------------------------------

    @GetMapping
    @Operation(summary = "List all stock adjustments", description = "Returns a paginated list of stock adjustments with optional filters.")
    public ResponseEntity<Page<StockAdjustmentResponse>> getAllAdjustments(
            @Parameter(description = "Filter by product ID") @RequestParam(required = false) Long productId,
            @Parameter(description = "Filter by warehouse ID") @RequestParam(required = false) Long warehouseId,
            @Parameter(description = "Filter by status: PENDING, APPROVED, REJECTED") @RequestParam(required = false) StockAdjustmentStatus status,
            @Parameter(description = "Filter by reason: DAMAGED, EXPIRED, THEFT, COUNT_ERROR, RETURN, OTHER") @RequestParam(required = false) AdjustmentReason reason,
            @Parameter(description = "Start of date range (ISO-8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End of date range (ISO-8601)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "adjustmentDate") Pageable pageable) {

        log.info("GET /api/stock-adjustments - productId={}, warehouseId={}, status={}, reason={}",
                productId, warehouseId, status, reason);

        return ResponseEntity.ok(
                stockAdjustmentService.getAllAdjustments(
                        productId, warehouseId, status, reason, startDate, endDate, pageable));
    }

    // ----------------------------------------------------------------
    // GET /api/stock-adjustments/pending
    // ----------------------------------------------------------------

    @GetMapping("/pending")
    @Operation(summary = "Get pending stock adjustments", description = "Returns all stock adjustments awaiting approval, ordered by adjustment date ascending.")
    public ResponseEntity<List<StockAdjustmentResponse>> getPendingAdjustments() {
        log.info("GET /api/stock-adjustments/pending");
        return ResponseEntity.ok(stockAdjustmentService.getPendingAdjustments());
    }

    // ----------------------------------------------------------------
    // GET /api/stock-adjustments/reasons
    // ----------------------------------------------------------------

    @GetMapping("/reasons")
    @Operation(summary = "Get available adjustment reasons", description = "Returns the list of valid reason codes: DAMAGED, EXPIRED, THEFT, COUNT_ERROR, RETURN, OTHER.")
    public ResponseEntity<List<AdjustmentReason>> getAdjustmentReasons() {
        log.info("GET /api/stock-adjustments/reasons");
        return ResponseEntity.ok(stockAdjustmentService.getAdjustmentReasons());
    }

    // ----------------------------------------------------------------
    // GET /api/stock-adjustments/{id}
    // ----------------------------------------------------------------

    @GetMapping("/{id}")
    @Operation(summary = "Get stock adjustment by ID", description = "Returns a single stock adjustment record with full details.")
    public ResponseEntity<StockAdjustmentResponse> getAdjustmentById(
            @Parameter(description = "Adjustment ID", required = true) @PathVariable Long id) {

        log.info("GET /api/stock-adjustments/{}", id);
        return ResponseEntity.ok(stockAdjustmentService.getAdjustmentById(id));
    }

    // ----------------------------------------------------------------
    // PATCH /api/stock-adjustments/{id}/approve
    // ----------------------------------------------------------------

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve a stock adjustment", description = "Approves a PENDING stock adjustment. " +
            "The inventory quantity is updated and a movement record of type ADJUSTMENT is created. " +
            "Requires MANAGER or ADMIN role.")
    public ResponseEntity<StockAdjustmentResponse> approveAdjustment(
            @Parameter(description = "Adjustment ID", required = true) @PathVariable Long id,
            @Valid @RequestBody StockAdjustmentApproveRequest request) {

        log.info("PATCH /api/stock-adjustments/{}/approve - approvedBy={}", id, request.getApprovedBy());
        return ResponseEntity.ok(stockAdjustmentService.approveAdjustment(id, request));
    }

    // ----------------------------------------------------------------
    // PATCH /api/stock-adjustments/{id}/reject
    // ----------------------------------------------------------------

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Reject a stock adjustment", description = "Rejects a PENDING stock adjustment. " +
            "No inventory change is made. The rejection reason is recorded in the adjustment notes. " +
            "Requires MANAGER or ADMIN role.")
    public ResponseEntity<StockAdjustmentResponse> rejectAdjustment(
            @Parameter(description = "Adjustment ID", required = true) @PathVariable Long id,
            @Valid @RequestBody StockAdjustmentRejectRequest request) {

        log.info("PATCH /api/stock-adjustments/{}/reject - rejectedBy={}", id, request.getRejectedBy());
        return ResponseEntity.ok(stockAdjustmentService.rejectAdjustment(id, request));
    }
}