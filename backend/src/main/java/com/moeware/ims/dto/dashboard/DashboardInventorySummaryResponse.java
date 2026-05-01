package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard inventory summary response.
 * Provides stock-status counts, total values, and breakdowns by warehouse and
 * category.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Inventory summary including cost/retail value, stock-status distribution, " +
        "and per-category and per-warehouse breakdowns")
public class DashboardInventorySummaryResponse {

    @Schema(description = "Total number of distinct active products across the filtered scope", example = "450")
    private int totalProducts;

    @Schema(description = "Cost, retail, and potential profit values for the filtered inventory")
    private TotalValueDTO totalValue;

    @Schema(description = "Product counts grouped by stock status")
    private StockStatusDTO stockStatus;

    @Schema(description = "Inventory value and product counts broken down by category")
    private List<CategoryBreakdownDTO> byCategory;

    @Schema(description = "Inventory value and utilisation broken down by warehouse")
    private List<WarehouseBreakdownDTO> byWarehouse;

    @Schema(description = "Top 10 products by retail value")
    private List<TopProductDTO> topProducts;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Aggregated inventory value at cost, retail, and potential profit")
    public static class TotalValueDTO {

        @Schema(description = "Sum of (costPrice × quantity) for all items in scope", example = "5250000.00")
        private BigDecimal cost;

        @Schema(description = "Sum of (unitPrice × quantity) for all items in scope", example = "7875000.00")
        private BigDecimal retail;

        @Schema(description = "Retail value minus cost value (potential gross profit)", example = "2625000.00")
        private BigDecimal potentialProfit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Product counts grouped by current stock status")
    public static class StockStatusDTO {

        @Schema(description = "Products with quantity above their reorder level", example = "435")
        private int inStock;

        @Schema(description = "Products with quantity at or below their reorder level (but > 0)", example = "12")
        private int lowStock;

        @Schema(description = "Products with zero stock across all warehouses", example = "3")
        private int outOfStock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Inventory value and low-stock count for a single product category")
    public static class CategoryBreakdownDTO {

        @Schema(description = "Category ID", example = "1")
        private Long categoryId;

        @Schema(description = "Category name", example = "Electronics")
        private String categoryName;

        @Schema(description = "Number of distinct products in this category within the scope", example = "250")
        private int productCount;

        @Schema(description = "Total retail value for this category", example = "6125000.00")
        private BigDecimal totalValue;

        @Schema(description = "Number of inventory items at or below their reorder level in this category", example = "8")
        private int lowStockCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Inventory value and capacity utilisation for a single warehouse")
    public static class WarehouseBreakdownDTO {

        @Schema(description = "Warehouse ID", example = "1")
        private Long warehouseId;

        @Schema(description = "Warehouse name", example = "Main Warehouse")
        private String warehouseName;

        @Schema(description = "Number of distinct products stored in this warehouse", example = "250")
        private int productCount;

        @Schema(description = "Total retail value of inventory in this warehouse", example = "5250000.00")
        private BigDecimal totalValue;

        @Schema(description = "Capacity utilisation percentage (0–100). 0.0 when warehouse has no defined capacity.", example = "70.0")
        private double utilization;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Top product by total retail inventory value")
    public static class TopProductDTO {

        @Schema(description = "Product ID", example = "10")
        private Long productId;

        @Schema(description = "Product SKU", example = "LAP-001")
        private String sku;

        @Schema(description = "Product name", example = "Dell Laptop XPS 15")
        private String name;

        @Schema(description = "Total quantity across all warehouses in scope", example = "75")
        private int totalQuantity;

        @Schema(description = "Total retail value (unitPrice × totalQuantity)", example = "97499.25")
        private BigDecimal totalValue;
    }
}