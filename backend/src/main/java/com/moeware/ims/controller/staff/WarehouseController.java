package com.moeware.ims.controller.staff;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.moeware.ims.dto.staff.warehouse.WarehouseCreateRequest;
import com.moeware.ims.dto.staff.warehouse.WarehouseResponse;
import com.moeware.ims.dto.staff.warehouse.WarehouseStatsResponse;
import com.moeware.ims.dto.staff.warehouse.WarehouseUpdateRequest;
import com.moeware.ims.service.staff.WarehouseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Warehouse management endpoints.
 * Provides CRUD operations and statistics for warehouse locations.
 */
@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Warehouses", description = "Warehouse management APIs - CRUD operations, inventory tracking, and statistics")
public class WarehouseController {

        private final WarehouseService warehouseService;

        @PostMapping
        @Operation(summary = "Create a new warehouse", description = "Creates a new warehouse location with the provided details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Warehouse created successfully", content = @Content(schema = @Schema(implementation = WarehouseResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data or duplicate code/name"),
                        @ApiResponse(responseCode = "404", description = "Manager user not found")
        })
        public ResponseEntity<WarehouseResponse> createWarehouse(
                        @Valid @RequestBody WarehouseCreateRequest request) {
                log.info("REST request to create warehouse: {}", request.getCode());
                WarehouseResponse response = warehouseService.createWarehouse(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get warehouse by ID", description = "Retrieves detailed information about a warehouse by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Warehouse found", content = @Content(schema = @Schema(implementation = WarehouseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Warehouse not found")
        })
        public ResponseEntity<WarehouseResponse> getWarehouseById(
                        @Parameter(description = "Warehouse ID", required = true) @PathVariable Long id) {
                log.debug("REST request to get warehouse by ID: {}", id);
                WarehouseResponse response = warehouseService.getWarehouseById(id);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/code/{code}")
        @Operation(summary = "Get warehouse by code", description = "Retrieves a warehouse by its unique code")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Warehouse found", content = @Content(schema = @Schema(implementation = WarehouseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Warehouse not found")
        })
        public ResponseEntity<WarehouseResponse> getWarehouseByCode(
                        @Parameter(description = "Warehouse code", required = true, example = "WH001") @PathVariable String code) {
                log.debug("REST request to get warehouse by code: {}", code);
                WarehouseResponse response = warehouseService.getWarehouseByCode(code);
                return ResponseEntity.ok(response);
        }

        @GetMapping
        @Operation(summary = "Get all warehouses with pagination and filters", description = "Retrieves a paginated list of warehouses with optional filters for search, location, and active status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Warehouses retrieved successfully")
        })
        public ResponseEntity<Page<WarehouseResponse>> getAllWarehouses(
                        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) @Parameter(description = "Pagination information (page, size, sort)") Pageable pageable,

                        @Parameter(description = "Search term for name, code, city, or address", example = "Main") @RequestParam(required = false) String search,

                        @Parameter(description = "Filter by active status", example = "true") @RequestParam(required = false) Boolean isActive,

                        @Parameter(description = "Filter by city", example = "New York") @RequestParam(required = false) String city,

                        @Parameter(description = "Filter by country", example = "USA") @RequestParam(required = false) String country,

                        @Parameter(description = "Filter by state", example = "NY") @RequestParam(required = false) String state) {

                log.debug("REST request to get all warehouses with filters");
                Page<WarehouseResponse> warehouses = warehouseService.getAllWarehouses(
                                pageable, search, isActive, city, country, state);
                return ResponseEntity.ok(warehouses);
        }

        @GetMapping("/active")
        @Operation(summary = "Get all active warehouses", description = "Retrieves a list of all active (operational) warehouses without pagination")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Active warehouses retrieved successfully")
        })
        public ResponseEntity<List<WarehouseResponse>> getAllActiveWarehouses() {
                log.debug("REST request to get all active warehouses");
                List<WarehouseResponse> warehouses = warehouseService.getAllActiveWarehouses();
                return ResponseEntity.ok(warehouses);
        }

        @GetMapping("/manager/{managerId}")
        @Operation(summary = "Get warehouses by manager", description = "Retrieves all warehouses managed by a specific user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Warehouses retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Manager user not found")
        })
        public ResponseEntity<Page<WarehouseResponse>> getWarehousesByManager(
                        @Parameter(description = "Manager user ID", required = true) @PathVariable Long managerId,

                        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

                log.debug("REST request to get warehouses for manager: {}", managerId);
                Page<WarehouseResponse> warehouses = warehouseService.getWarehousesByManager(managerId, pageable);
                return ResponseEntity.ok(warehouses);
        }

        @GetMapping("/{id}/stats")
        @Operation(summary = "Get warehouse statistics", description = "Retrieves comprehensive statistics for a warehouse including product count, stock units, inventory value, and capacity utilization")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully", content = @Content(schema = @Schema(implementation = WarehouseStatsResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Warehouse not found")
        })
        public ResponseEntity<WarehouseStatsResponse> getWarehouseStatistics(
                        @Parameter(description = "Warehouse ID", required = true) @PathVariable Long id) {

                log.debug("REST request to get statistics for warehouse: {}", id);
                WarehouseStatsResponse stats = warehouseService.getWarehouseStatistics(id);
                return ResponseEntity.ok(stats);
        }

        @GetMapping("/low-stock-alerts")
        @Operation(summary = "Get warehouses with low stock alerts", description = "Retrieves all warehouses that have products with stock levels at or below reorder levels")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Warehouses with low stock alerts retrieved successfully")
        })
        public ResponseEntity<List<WarehouseResponse>> getWarehousesWithLowStock() {
                log.debug("REST request to get warehouses with low stock alerts");
                List<WarehouseResponse> warehouses = warehouseService.getWarehousesWithLowStock();
                return ResponseEntity.ok(warehouses);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update warehouse", description = "Updates an existing warehouse with the provided details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Warehouse updated successfully", content = @Content(schema = @Schema(implementation = WarehouseResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "404", description = "Warehouse or manager not found"),
                        @ApiResponse(responseCode = "409", description = "Duplicate warehouse name")
        })
        public ResponseEntity<WarehouseResponse> updateWarehouse(
                        @Parameter(description = "Warehouse ID", required = true) @PathVariable Long id,

                        @Valid @RequestBody WarehouseUpdateRequest request) {

                log.info("REST request to update warehouse: {}", id);
                WarehouseResponse response = warehouseService.updateWarehouse(id, request);
                return ResponseEntity.ok(response);
        }

        @PatchMapping("/{id}")
        @Operation(summary = "Partially update warehouse", description = "Updates specific fields of an existing warehouse")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Warehouse updated successfully", content = @Content(schema = @Schema(implementation = WarehouseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Warehouse not found")
        })
        public ResponseEntity<WarehouseResponse> patchWarehouse(
                        @Parameter(description = "Warehouse ID", required = true) @PathVariable Long id,

                        @RequestBody WarehouseUpdateRequest request) {

                log.info("REST request to partially update warehouse: {}", id);
                WarehouseResponse response = warehouseService.updateWarehouse(id, request);
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Soft delete warehouse", description = "Deactivates a warehouse (soft delete) - sets isActive to false")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Warehouse deactivated successfully"),
                        @ApiResponse(responseCode = "404", description = "Warehouse not found")
        })
        public ResponseEntity<Void> deleteWarehouse(
                        @Parameter(description = "Warehouse ID", required = true) @PathVariable Long id) {

                log.info("REST request to soft delete warehouse: {}", id);
                warehouseService.deleteWarehouse(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/stats/count")
        @Operation(summary = "Get warehouse count", description = "Returns the total count of warehouses, optionally filtered by active status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Warehouse count retrieved successfully")
        })
        public ResponseEntity<Long> getWarehouseCount(
                        @Parameter(description = "Filter by active status", example = "true") @RequestParam(required = false) Boolean isActive) {

                log.debug("REST request to get warehouse count");
                long count = warehouseService.countWarehouses(isActive);
                return ResponseEntity.ok(count);
        }
}