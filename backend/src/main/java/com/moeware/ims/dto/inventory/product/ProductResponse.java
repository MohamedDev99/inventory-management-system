package com.moeware.ims.dto.inventory.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for product response data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO containing product information")
public class ProductResponse {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Stock Keeping Unit", example = "LAP-001")
    private String sku;

    @Schema(description = "Product name", example = "Dell Laptop XPS 15")
    private String name;

    @Schema(description = "Product description", example = "15.6 inch laptop with Intel i7 processor")
    private String description;

    @Schema(description = "Category information")
    private CategorySummary category;

    @Schema(description = "Unit of measure", example = "PIECE")
    private String unit;

    @Schema(description = "Selling price per unit", example = "1299.99")
    private BigDecimal unitPrice;

    @Schema(description = "Cost/purchase price per unit", example = "899.00")
    private BigDecimal costPrice;

    @Schema(description = "Profit margin percentage", example = "44.49")
    private BigDecimal profitMargin;

    @Schema(description = "Reorder level threshold", example = "10")
    private Integer reorderLevel;

    @Schema(description = "Minimum stock level", example = "5")
    private Integer minStockLevel;

    @Schema(description = "Product barcode", example = "1234567890123")
    private String barcode;

    @Schema(description = "Product image URL")
    private String imageUrl;

    @Schema(description = "Active status", example = "true")
    private Boolean isActive;

    @Schema(description = "Total stock across all warehouses", example = "150")
    private Integer totalStock;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Version for optimistic locking")
    private Long version;

    /**
     * Nested DTO for category summary
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Summary of category information")
    public static class CategorySummary {
        @Schema(description = "Category ID", example = "1")
        private Long id;

        @Schema(description = "Category name", example = "Electronics")
        private String name;

        @Schema(description = "Category code", example = "ELEC")
        private String code;

        @Schema(description = "Full category path", example = "Electronics > Computers > Laptops")
        private String fullPath;
    }
}