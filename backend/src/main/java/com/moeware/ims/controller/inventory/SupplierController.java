package com.moeware.ims.controller.inventory;

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

import com.moeware.ims.dto.inventory.supplier.SupplierDTO;
import com.moeware.ims.service.inventory.SupplierService;

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
 * REST Controller for Supplier management
 * Provides endpoints for CRUD operations on suppliers
 */
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Suppliers", description = "Supplier management APIs")
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * Get all suppliers with pagination and filters
     */
    @Operation(summary = "Get all suppliers", description = "Retrieve a paginated list of suppliers with optional filters for active status, country, rating range, and search term")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved suppliers", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<SupplierDTO>> getAllSuppliers(
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Filter by country") @RequestParam(required = false) String country,
            @Parameter(description = "Minimum rating (1-5)") @RequestParam(required = false) Integer minRating,
            @Parameter(description = "Maximum rating (1-5)") @RequestParam(required = false) Integer maxRating,
            @Parameter(description = "Search by name, code, or email") @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.info("GET /api/suppliers - Fetching suppliers with filters");

        Page<SupplierDTO> suppliers = supplierService.getAllSuppliers(
                isActive, country, minRating, maxRating, search, pageable);

        return ResponseEntity.ok(suppliers);
    }

    /**
     * Get supplier by ID
     */
    @Operation(summary = "Get supplier by ID", description = "Retrieve detailed information about a specific supplier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier found", content = @Content(schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(
            @Parameter(description = "Supplier ID", required = true) @PathVariable Long id) {

        log.info("GET /api/suppliers/{} - Fetching supplier", id);

        SupplierDTO supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    /**
     * Get supplier by code
     */
    @Operation(summary = "Get supplier by code", description = "Retrieve supplier information using the unique supplier code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier found", content = @Content(schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/code/{code}")
    public ResponseEntity<SupplierDTO> getSupplierByCode(
            @Parameter(description = "Supplier code", required = true) @PathVariable String code) {

        log.info("GET /api/suppliers/code/{} - Fetching supplier by code", code);

        SupplierDTO supplier = supplierService.getSupplierByCode(code);
        return ResponseEntity.ok(supplier);
    }

    /**
     * Create a new supplier
     */
    @Operation(summary = "Create a new supplier", description = "Add a new supplier to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Supplier created successfully", content = @Content(schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate code/email", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(
            @Parameter(description = "Supplier data", required = true) @Valid @RequestBody SupplierDTO supplierDTO) {

        log.info("POST /api/suppliers - Creating new supplier with code: {}", supplierDTO.getCode());

        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
    }

    /**
     * Update supplier (full update)
     */
    @Operation(summary = "Update supplier", description = "Perform a full update of an existing supplier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier updated successfully", content = @Content(schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(
            @Parameter(description = "Supplier ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated supplier data", required = true) @Valid @RequestBody SupplierDTO supplierDTO) {

        log.info("PUT /api/suppliers/{} - Updating supplier", id);

        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
        return ResponseEntity.ok(updatedSupplier);
    }

    /**
     * Partial update supplier
     */
    @Operation(summary = "Partially update supplier", description = "Update specific fields of an existing supplier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier updated successfully", content = @Content(schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<SupplierDTO> patchSupplier(
            @Parameter(description = "Supplier ID", required = true) @PathVariable Long id,
            @Parameter(description = "Partial supplier data", required = true) @RequestBody SupplierDTO supplierDTO) {

        log.info("PATCH /api/suppliers/{} - Partially updating supplier", id);

        SupplierDTO updatedSupplier = supplierService.patchSupplier(id, supplierDTO);
        return ResponseEntity.ok(updatedSupplier);
    }

    /**
     * Soft delete supplier (deactivate)
     */
    @Operation(summary = "Delete supplier", description = "Soft delete a supplier by setting isActive to false. Cannot delete suppliers with pending orders.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supplier deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cannot delete supplier with pending orders", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(
            @Parameter(description = "Supplier ID", required = true) @PathVariable Long id) {

        log.info("DELETE /api/suppliers/{} - Soft deleting supplier", id);

        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get top-rated suppliers
     */
    @Operation(summary = "Get top-rated suppliers", description = "Retrieve suppliers ordered by rating (highest first)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved top-rated suppliers", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/top-rated")
    public ResponseEntity<Page<SupplierDTO>> getTopRatedSuppliers(
            @PageableDefault(size = 10, sort = "rating", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("GET /api/suppliers/top-rated - Fetching top-rated suppliers");

        Page<SupplierDTO> suppliers = supplierService.getTopRatedSuppliers(pageable);
        return ResponseEntity.ok(suppliers);
    }

    /**
     * Search suppliers
     */
    @Operation(summary = "Search suppliers", description = "Search suppliers by name, code, or email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<Page<SupplierDTO>> searchSuppliers(
            @Parameter(description = "Search term", required = true) @RequestParam String term,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        log.info("GET /api/suppliers/search?term={} - Searching suppliers", term);

        Page<SupplierDTO> suppliers = supplierService.searchSuppliers(term, pageable);
        return ResponseEntity.ok(suppliers);
    }

    /**
     * Get active supplier count
     */
    @Operation(summary = "Get active supplier count", description = "Get the total number of active suppliers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/count/active")
    public ResponseEntity<Long> getActiveSupplierCount() {

        log.info("GET /api/suppliers/count/active - Getting active supplier count");

        Long count = supplierService.getActiveSupplierCount();
        return ResponseEntity.ok(count);
    }

    // Note: Additional endpoints for orders and performance metrics would be
    // implemented here
    // These require PurchaseOrder entity and repository to be implemented first
    // GET /api/v1/suppliers/{id}/orders
    // GET /api/v1/suppliers/{id}/performance
}