package com.moeware.ims.controller.transaction;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.transaction.inventoryMovement.InventoryMovementDTO;
import com.moeware.ims.dto.transaction.inventoryMovement.InventoryMovementSummaryDTO;
import com.moeware.ims.enums.transaction.MovementType;
import com.moeware.ims.service.transaction.InventoryMovementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for inventory movement read operations.
 *
 * <p>
 * Base path: {@code /api/inventory-movements}
 * </p>
 *
 * <p>
 * Write operations (transfers, receipts, shipments, adjustments) are handled
 * by {@code InventoryController} and {@code StockAdjustmentController} because
 * they belong to those transactional workflows.
 * </p>
 *
 * @author MoeWare Team
 */
@RestController
@RequestMapping("/api/inventory-movements")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory Movements", description = "Read and query inventory movement records")
public class InventoryMovementController {

        private final InventoryMovementService inventoryMovementService;

        // ----------------------------------------------------------------
        // GET /api/inventory-movements
        // ----------------------------------------------------------------

        @GetMapping
        @Operation(summary = "List all inventory movements", description = "Returns a paginated list of inventory movements with optional filters. "
                        +
                        "Supports filtering by product, warehouse (from OR to), movement type, and date range.")
        public ResponseEntity<Page<InventoryMovementDTO>> getAllMovements(
                        @Parameter(description = "Filter by product ID") @RequestParam(required = false) Long productId,
                        @Parameter(description = "Filter by warehouse ID (matches from OR to warehouse)") @RequestParam(required = false) Long warehouseId,
                        @Parameter(description = "Filter by movement type: TRANSFER, ADJUSTMENT, RECEIPT, SHIPMENT") @RequestParam(required = false) MovementType movementType,
                        @Parameter(description = "Start of date range (ISO-8601), e.g. 2026-01-01T00:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                        @Parameter(description = "End of date range (ISO-8601), e.g. 2026-02-09T23:59:59") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                        @PageableDefault(size = 20, sort = "movementDate") Pageable pageable) {

                log.info("GET /api/inventory-movements - productId={}, warehouseId={}, type={}", productId, warehouseId,
                                movementType);

                Page<InventoryMovementDTO> result = inventoryMovementService
                                .getAllMovements(productId, warehouseId, movementType, startDate, endDate, pageable);

                return ResponseEntity.ok(result);
        }

        // ----------------------------------------------------------------
        // GET /api/inventory-movements/{id}
        // ----------------------------------------------------------------

        @GetMapping("/{id}")
        @Operation(summary = "Get inventory movement by ID", description = "Returns a single inventory movement record.")
        public ResponseEntity<InventoryMovementDTO> getMovementById(
                        @Parameter(description = "Movement ID", required = true) @PathVariable Long id) {

                log.info("GET /api/inventory-movements/{}", id);
                return ResponseEntity.ok(inventoryMovementService.getMovementById(id));
        }

        // ----------------------------------------------------------------
        // GET /api/inventory-movements/product/{productId}
        // ----------------------------------------------------------------

        @GetMapping("/product/{productId}")
        @Operation(summary = "Get movement history for a product", description = "Returns all inventory movements for a specific product, paginated.")
        public ResponseEntity<Page<InventoryMovementDTO>> getMovementsByProduct(
                        @Parameter(description = "Product ID", required = true) @PathVariable Long productId,
                        @PageableDefault(size = 20, sort = "movementDate") Pageable pageable) {

                log.info("GET /api/inventory-movements/product/{}", productId);
                return ResponseEntity.ok(inventoryMovementService.getMovementsByProduct(productId, pageable));
        }

        // ----------------------------------------------------------------
        // GET /api/inventory-movements/warehouse/{warehouseId}
        // ----------------------------------------------------------------

        @GetMapping("/warehouse/{warehouseId}")
        @Operation(summary = "Get movements for a warehouse", description = "Returns all inventory movements where the warehouse is either the source or destination, paginated.")
        public ResponseEntity<Page<InventoryMovementDTO>> getMovementsByWarehouse(
                        @Parameter(description = "Warehouse ID", required = true) @PathVariable Long warehouseId,
                        @PageableDefault(size = 20, sort = "movementDate") Pageable pageable) {

                log.info("GET /api/inventory-movements/warehouse/{}", warehouseId);
                return ResponseEntity.ok(inventoryMovementService.getMovementsByWarehouse(warehouseId, pageable));
        }

        // ----------------------------------------------------------------
        // GET /api/inventory-movements/summary
        // ----------------------------------------------------------------

        @GetMapping("/summary")
        @Operation(summary = "Get inventory movement summary", description = "Returns aggregated movement counts and quantities grouped by type and warehouse "
                        +
                        "over a given date range. Optionally filtered by a specific warehouse.")
        public ResponseEntity<InventoryMovementSummaryDTO> getMovementSummary(
                        @Parameter(description = "Start of the reporting period (ISO-8601)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                        @Parameter(description = "End of the reporting period (ISO-8601)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                        @Parameter(description = "Optional warehouse filter") @RequestParam(required = false) Long warehouseId) {

                log.info("GET /api/inventory-movements/summary - start={}, end={}, warehouseId={}",
                                startDate, endDate, warehouseId);

                return ResponseEntity.ok(
                                inventoryMovementService.getMovementSummary(startDate, endDate, warehouseId));
        }
}