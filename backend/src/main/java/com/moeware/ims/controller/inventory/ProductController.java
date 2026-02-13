package com.moeware.ims.controller.inventory;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.inventory.product.ProductCreateRequest;
import com.moeware.ims.dto.inventory.product.ProductResponse;
import com.moeware.ims.dto.inventory.product.ProductUpdateRequest;
import com.moeware.ims.service.inventory.ProductService;

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
 * REST Controller for Product management endpoints.
 * Provides CRUD operations and search functionality for products.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "Product management APIs - CRUD operations, search, and inventory queries")
public class ProductController {

        private final ProductService productService;

        @PostMapping
        @Operation(summary = "Create a new product", description = "Creates a new product in the catalog with the provided details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data or duplicate SKU/barcode"),
                        @ApiResponse(responseCode = "404", description = "Category not found")
        })
        public ResponseEntity<ProductResponse> createProduct(
                        @Valid @RequestBody ProductCreateRequest request) {
                log.info("REST request to create product: {}", request.getSku());
                ProductResponse response = productService.createProduct(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get product by ID", description = "Retrieves detailed information about a product by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product found", content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Product not found")
        })
        public ResponseEntity<ProductResponse> getProductById(
                        @Parameter(description = "Product ID", required = true) @PathVariable Long id) {
                log.debug("REST request to get product by ID: {}", id);
                ProductResponse response = productService.getProductById(id);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/sku/{sku}")
        @Operation(summary = "Get product by SKU", description = "Retrieves a product by its Stock Keeping Unit (SKU)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product found", content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Product not found")
        })
        public ResponseEntity<ProductResponse> getProductBySku(
                        @Parameter(description = "Product SKU", required = true, example = "LAP-001") @PathVariable String sku) {
                log.debug("REST request to get product by SKU: {}", sku);
                ProductResponse response = productService.getProductBySku(sku);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/barcode/{barcode}")
        @Operation(summary = "Get product by barcode", description = "Retrieves a product by its barcode (EAN, UPC, etc.)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product found", content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Product not found")
        })
        public ResponseEntity<ProductResponse> getProductByBarcode(
                        @Parameter(description = "Product barcode", required = true, example = "1234567890123") @PathVariable String barcode) {
                log.debug("REST request to get product by barcode: {}", barcode);
                ProductResponse response = productService.getProductByBarcode(barcode);
                return ResponseEntity.ok(response);
        }

        @GetMapping
        @Operation(summary = "Get all products with pagination and filters", description = "Retrieves a paginated list of products with optional filters for search, category, price range, and active status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
        })
        public ResponseEntity<Page<ProductResponse>> getAllProducts(
                        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) @Parameter(description = "Pagination information (page, size, sort)") Pageable pageable,

                        @Parameter(description = "Search term for name, SKU, or description", example = "laptop") @RequestParam(required = false) String search,

                        @Parameter(description = "Filter by category ID", example = "1") @RequestParam(required = false) Long categoryId,

                        @Parameter(description = "Filter by active status", example = "true") @RequestParam(required = false) Boolean isActive,

                        @Parameter(description = "Minimum price filter", example = "100.00") @RequestParam(required = false) BigDecimal minPrice,

                        @Parameter(description = "Maximum price filter", example = "2000.00") @RequestParam(required = false) BigDecimal maxPrice) {

                log.debug("REST request to get all products with filters");
                Page<ProductResponse> products = productService.getAllProducts(
                                pageable, search, categoryId, isActive, minPrice, maxPrice);
                return ResponseEntity.ok(products);
        }

        @GetMapping("/category/{categoryId}")
        @Operation(summary = "Get products by category", description = "Retrieves all products belonging to a specific category")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Category not found")
        })
        public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
                        @Parameter(description = "Category ID", required = true) @PathVariable Long categoryId,

                        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

                log.debug("REST request to get products for category: {}", categoryId);
                Page<ProductResponse> products = productService.getProductsByCategory(categoryId, pageable);
                return ResponseEntity.ok(products);
        }

        @GetMapping("/low-stock")
        @Operation(summary = "Get low stock products", description = "Retrieves all products with stock levels at or below their reorder level")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Low stock products retrieved successfully")
        })
        public ResponseEntity<List<ProductResponse>> getLowStockProducts() {
                log.debug("REST request to get low stock products");
                List<ProductResponse> products = productService.getLowStockProducts();
                return ResponseEntity.ok(products);
        }

        @GetMapping("/critical-stock")
        @Operation(summary = "Get critical stock products", description = "Retrieves all products with stock levels at or below their minimum stock level (critical alert)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Critical stock products retrieved successfully")
        })
        public ResponseEntity<List<ProductResponse>> getCriticalStockProducts() {
                log.debug("REST request to get critical stock products");
                List<ProductResponse> products = productService.getCriticalStockProducts();
                return ResponseEntity.ok(products);
        }

        @GetMapping("/out-of-stock")
        @Operation(summary = "Get out of stock products", description = "Retrieves all products with zero stock quantity")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Out of stock products retrieved successfully")
        })
        public ResponseEntity<List<ProductResponse>> getOutOfStockProducts() {
                log.debug("REST request to get out of stock products");
                List<ProductResponse> products = productService.getOutOfStockProducts();
                return ResponseEntity.ok(products);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update product", description = "Updates an existing product with the provided details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product updated successfully", content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "404", description = "Product or category not found"),
                        @ApiResponse(responseCode = "409", description = "Duplicate barcode")
        })
        public ResponseEntity<ProductResponse> updateProduct(
                        @Parameter(description = "Product ID", required = true) @PathVariable Long id,

                        @Valid @RequestBody ProductUpdateRequest request) {

                log.info("REST request to update product: {}", id);
                ProductResponse response = productService.updateProduct(id, request);
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Soft delete product", description = "Deactivates a product (soft delete) - sets isActive to false")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Product deactivated successfully"),
                        @ApiResponse(responseCode = "404", description = "Product not found")
        })
        public ResponseEntity<Void> deleteProduct(
                        @Parameter(description = "Product ID", required = true) @PathVariable Long id) {

                log.info("REST request to soft delete product: {}", id);
                productService.deleteProduct(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/stats/total-value")
        @Operation(summary = "Get total inventory value", description = "Calculates the total value of all inventory (cost price × quantity)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Total inventory value calculated successfully")
        })
        public ResponseEntity<BigDecimal> getTotalInventoryValue() {
                log.debug("REST request to get total inventory value");
                BigDecimal totalValue = productService.getTotalInventoryValue();
                return ResponseEntity.ok(totalValue);
        }

        @GetMapping("/stats/count")
        @Operation(summary = "Get product count", description = "Returns the total count of products, optionally filtered by active status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product count retrieved successfully")
        })
        public ResponseEntity<Long> getProductCount(
                        @Parameter(description = "Filter by active status", example = "true") @RequestParam(required = false) Boolean isActive) {

                log.debug("REST request to get product count");
                long count = productService.countProducts(isActive);
                return ResponseEntity.ok(count);
        }
}