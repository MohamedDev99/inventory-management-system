package com.moeware.ims.controller.transaction;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.transaction.inventoryMovement.InventoryMovementDTO;
import com.moeware.ims.dto.transaction.inventoryMovement.InventoryMovementSummaryDTO;
import com.moeware.ims.enums.transaction.MovementType;
import com.moeware.ims.exception.handler.GlobalExceptionHandler;
import com.moeware.ims.service.transaction.InventoryMovementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
public class InventoryMovementController {

        private final InventoryMovementService inventoryMovementService;

        // ==================== READ ENDPOINTS ====================

        @Operation(summary = "Get all inventory movements", description = "Returns a paginated list of inventory movements with optional filters. "
                        +
                        "Supports filtering by product, warehouse (from OR to), movement type, and date range.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "List returned successfully"),
                        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<Page<InventoryMovementDTO>>> getAllMovements(
                        @Parameter(description = "Filter by product ID") @RequestParam(required = false) Long productId,
                        @Parameter(description = "Filter by warehouse ID (matches from OR to warehouse)") @RequestParam(required = false) Long warehouseId,
                        @Parameter(description = "Filter by movement type: TRANSFER, ADJUSTMENT, RECEIPT, SHIPMENT") @RequestParam(required = false) MovementType movementType,
                        @Parameter(description = "Start of date range (ISO-8601), e.g. 2026-01-01T00:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                        @Parameter(description = "End of date range (ISO-8601), e.g. 2026-02-09T23:59:59") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                        @PageableDefault(size = 20, sort = "movementDate") Pageable pageable) {

                log.info("GET /api/inventory-movements - productId={}, warehouseId={}, type={}",
                                productId, warehouseId, movementType);

                Page<InventoryMovementDTO> result = inventoryMovementService
                                .getAllMovements(productId, warehouseId, movementType, startDate, endDate, pageable);

                return ResponseEntity.ok(ApiResponseWpp.success(result, "Inventory movements retrieved successfully"));
        }

        @Operation(summary = "Get inventory movement by ID", description = "Returns a single inventory movement record.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Movement found"),
                        @ApiResponse(responseCode = "404", description = "Movement not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<InventoryMovementDTO>> getMovementById(
                        @Parameter(description = "Movement ID", required = true) @PathVariable Long id) {

                log.info("GET /api/inventory-movements/{}", id);
                return ResponseEntity.ok(
                                ApiResponseWpp.success(inventoryMovementService.getMovementById(id),
                                                "Inventory movement retrieved successfully"));
        }

        @Operation(summary = "Get movement history for a product", description = "Returns all inventory movements for a specific product, paginated.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Movement history returned successfully"),
                        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @GetMapping("/product/{productId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<Page<InventoryMovementDTO>>> getMovementsByProduct(
                        @Parameter(description = "Product ID", required = true) @PathVariable Long productId,
                        @PageableDefault(size = 20, sort = "movementDate") Pageable pageable) {

                log.info("GET /api/inventory-movements/product/{}", productId);
                return ResponseEntity.ok(
                                ApiResponseWpp.success(
                                                inventoryMovementService.getMovementsByProduct(productId, pageable),
                                                "Product movement history retrieved successfully"));
        }

        @Operation(summary = "Get movements for a warehouse", description = "Returns all inventory movements where the warehouse is either the source or destination, paginated.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Warehouse movements returned successfully"),
                        @ApiResponse(responseCode = "404", description = "Warehouse not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @GetMapping("/warehouse/{warehouseId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<Page<InventoryMovementDTO>>> getMovementsByWarehouse(
                        @Parameter(description = "Warehouse ID", required = true) @PathVariable Long warehouseId,
                        @PageableDefault(size = 20, sort = "movementDate") Pageable pageable) {

                log.info("GET /api/inventory-movements/warehouse/{}", warehouseId);
                return ResponseEntity.ok(
                                ApiResponseWpp.success(
                                                inventoryMovementService.getMovementsByWarehouse(warehouseId, pageable),
                                                "Warehouse movement history retrieved successfully"));
        }

        @Operation(summary = "Get inventory movement summary", description = "Returns aggregated movement counts and quantities grouped by type and warehouse "
                        +
                        "over a given date range. Optionally filtered by a specific warehouse.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Summary returned successfully"),
                        @ApiResponse(responseCode = "400", description = "startDate or endDate missing or invalid", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Warehouse not found (when warehouseId is provided)", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @GetMapping("/summary")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<InventoryMovementSummaryDTO>> getMovementSummary(
                        @Parameter(description = "Start of the reporting period (ISO-8601)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                        @Parameter(description = "End of the reporting period (ISO-8601)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                        @Parameter(description = "Optional warehouse filter") @RequestParam(required = false) Long warehouseId) {

                log.info("GET /api/inventory-movements/summary - start={}, end={}, warehouseId={}",
                                startDate, endDate, warehouseId);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(
                                                inventoryMovementService.getMovementSummary(startDate, endDate,
                                                                warehouseId),
                                                "Movement summary retrieved successfully"));
        }
}