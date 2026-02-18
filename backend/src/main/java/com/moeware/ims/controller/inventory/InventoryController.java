package com.moeware.ims.controller.inventory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.inventory.InventoryResponseDTO;
import com.moeware.ims.dto.inventory.InventoryValuationResponse;
import com.moeware.ims.dto.inventory.inventoryItem.InventoryItemDTO;
import com.moeware.ims.dto.transaction.inventoryMovement.InventoryMovementDTO;
import com.moeware.ims.dto.transaction.inventoryMovement.TransferInventoryRequest;
import com.moeware.ims.dto.transaction.inventoryMovement.TransferInventoryResponse;
import com.moeware.ims.dto.transaction.shipment.ReceiveShipmentRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentResponse;
import com.moeware.ims.enums.transaction.MovementType;
import com.moeware.ims.service.inventory.InventoryService;

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
 * REST Controller for Inventory Operations
 * Handles inventory management, stock transfers, adjustments, and valuation
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory", description = "Inventory management and operations APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class InventoryController {

        private final InventoryService inventoryService;

        /**
         * Get all inventory items with pagination and filters
         */
        @Operation(summary = "Get all inventory items", description = "Retrieve paginated list of inventory items with optional filters for warehouse, product, low stock status, and search")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory items"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
        })
        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<InventoryResponseDTO<InventoryItemDTO>>> getAllInventoryItems(
                        @Parameter(description = "Filter by warehouse ID") @RequestParam(required = false) Long warehouseId,

                        @Parameter(description = "Filter by product ID") @RequestParam(required = false) Long productId,

                        @Parameter(description = "Show only low stock items") @RequestParam(required = false) Boolean lowStock,

                        @Parameter(description = "Search by product name or SKU") @RequestParam(required = false) String search,

                        @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

                log.info(
                                "GET /api/inventory - Fetching inventory items with filters: warehouseId={}, productId={}, lowStock={}, search={}",
                                warehouseId, productId, lowStock, search);

                Page<InventoryItemDTO> items = inventoryService.getAllInventoryItems(
                                warehouseId, productId, lowStock, search, pageable);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(InventoryResponseDTO.fromPage(items)));
        }

        /**
         * Get inventory item by ID
         */
        @Operation(summary = "Get inventory item by ID", description = "Retrieve detailed information about a specific inventory item")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory item"),
                        @ApiResponse(responseCode = "404", description = "Inventory item not found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<InventoryItemDTO>> getInventoryItemById(
                        @Parameter(description = "Inventory item ID", required = true) @PathVariable Long id) {

                log.info("GET /api/inventory/{} - Fetching inventory item", id);

                InventoryItemDTO item = inventoryService.getInventoryItemById(id);

                return ResponseEntity.ok(ApiResponseWpp.success(item));
        }

        /**
         * Get inventory for specific warehouse
         */
        @Operation(summary = "Get warehouse inventory", description = "Retrieve all inventory items for a specific warehouse")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved warehouse inventory"),
                        @ApiResponse(responseCode = "404", description = "Warehouse not found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @GetMapping("/warehouse/{warehouseId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<InventoryResponseDTO<InventoryItemDTO>>> getWarehouseInventory(
                        @Parameter(description = "Warehouse ID", required = true) @PathVariable Long warehouseId,

                        @PageableDefault(size = 20, sort = "product.name", direction = Sort.Direction.ASC) Pageable pageable) {

                log.info("GET /api/inventory/warehouse/{} - Fetching warehouse inventory", warehouseId);

                Page<InventoryItemDTO> items = inventoryService.getWarehouseInventory(warehouseId, pageable);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(InventoryResponseDTO.fromPage(items)));
        }

        /**
         * Get inventory for specific product across all warehouses
         */
        @Operation(summary = "Get product inventory", description = "Retrieve inventory information for a specific product across all warehouses")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved product inventory"),
                        @ApiResponse(responseCode = "404", description = "Product not found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @GetMapping("/product/{productId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<List<InventoryItemDTO>>> getProductInventory(
                        @Parameter(description = "Product ID", required = true) @PathVariable Long productId) {

                log.info("GET /api/inventory/product/{} - Fetching product inventory", productId);

                List<InventoryItemDTO> items = inventoryService.getProductInventory(productId);

                return ResponseEntity.ok(ApiResponseWpp.success(items));
        }

        /**
         * Transfer stock between warehouses
         */
        @Operation(summary = "Transfer inventory between warehouses", description = "Transfer stock from one warehouse to another. This operation is atomic and updates inventory levels in both warehouses.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Transfer completed successfully", content = @Content(schema = @Schema(implementation = TransferInventoryResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request or insufficient stock"),
                        @ApiResponse(responseCode = "404", description = "Product or warehouse not found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
        })
        @PostMapping("/transfer")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
        public ResponseEntity<ApiResponseWpp<TransferInventoryResponse>> transferInventory(
                        @Parameter(description = "Transfer request details", required = true) @Valid @RequestBody TransferInventoryRequest request) {

                log.info("POST /api/inventory/transfer - Transferring inventory");

                TransferInventoryResponse result = inventoryService.transferInventory(request);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(result, "Inventory transferred successfully"));
        }

        /**
         * Create stock adjustment request
         */
        @Operation(summary = "Create stock adjustment", description = "Create a request to adjust inventory levels. Adjustments require approval based on system configuration.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Stock adjustment created successfully", content = @Content(schema = @Schema(implementation = StockAdjustmentResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request"),
                        @ApiResponse(responseCode = "404", description = "Product or warehouse not found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
        })
        @PostMapping("/adjust")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
        public ResponseEntity<ApiResponseWpp<StockAdjustmentResponse>> createStockAdjustment(
                        @Parameter(description = "Stock adjustment request details", required = true) @Valid @RequestBody StockAdjustmentRequest request) {

                log.info("POST /api/inventory/adjust - Creating stock adjustment");

                StockAdjustmentResponse result = inventoryService.createStockAdjustment(request);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                                ApiResponseWpp.success(result, "Stock adjustment created successfully"));
        }

        /**
         * Receive shipment from purchase order
         */
        @Operation(summary = "Receive shipment", description = "Receive and process incoming shipment from a purchase order, updating inventory levels")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Shipment received successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request"),
                        @ApiResponse(responseCode = "404", description = "Purchase order, product, or warehouse not found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
        })
        @PostMapping("/receive")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
        public ResponseEntity<ApiResponseWpp<Void>> receiveShipment(
                        @Parameter(description = "Shipment receipt details", required = true) @Valid @RequestBody ReceiveShipmentRequest request) {

                log.info("POST /api/inventory/receive - Receiving shipment for PO: {}",
                                request.getPurchaseOrderId());

                inventoryService.receiveShipment(request);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(null, "Shipment received successfully"));
        }

        /**
         * Get inventory valuation
         */
        @Operation(summary = "Get inventory valuation", description = "Calculate total inventory value with optional filters for warehouse and category")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Valuation calculated successfully", content = @Content(schema = @Schema(implementation = InventoryValuationResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
        })
        @GetMapping("/valuation")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<InventoryValuationResponse>> getInventoryValuation(
                        @Parameter(description = "Filter by warehouse ID") @RequestParam(required = false) Long warehouseId,

                        @Parameter(description = "Filter by category ID") @RequestParam(required = false) Long categoryId,

                        @Parameter(description = "Valuation type: COST or RETAIL", example = "COST") @RequestParam(required = false, defaultValue = "COST") String valuationType) {

                log.info(
                                "GET /api/inventory/valuation - Calculating valuation with filters: warehouse={}, category={}, type={}",
                                warehouseId, categoryId, valuationType);

                InventoryValuationResponse valuation = inventoryService.getInventoryValuation(
                                warehouseId, categoryId, valuationType);

                return ResponseEntity.ok(ApiResponseWpp.success(valuation));
        }

        /**
         * Get inventory movement history
         */
        @Operation(summary = "Get inventory movements", description = "Retrieve history of inventory movements with optional filters")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved movement history"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
        })
        @GetMapping("/movements")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF', 'VIEWER')")
        public ResponseEntity<ApiResponseWpp<InventoryResponseDTO<InventoryMovementDTO>>> getInventoryMovements(
                        @Parameter(description = "Filter by product ID") @RequestParam(required = false) Long productId,

                        @Parameter(description = "Filter by warehouse ID") @RequestParam(required = false) Long warehouseId,

                        @Parameter(description = "Filter by movement type") @RequestParam(required = false) MovementType movementType,

                        @Parameter(description = "Start date for date range filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

                        @Parameter(description = "End date for date range filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

                        @PageableDefault(size = 20, sort = "movementDate", direction = Sort.Direction.DESC) Pageable pageable) {

                log.info("GET /api/inventory/movements - Fetching movement history with filters");

                Page<InventoryMovementDTO> movements = inventoryService.getInventoryMovements(
                                productId, warehouseId, movementType, startDate, endDate, pageable);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(InventoryResponseDTO.fromPage(movements)));
        }
}