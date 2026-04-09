package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

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
public class DashboardInventorySummaryResponse {

    private int totalProducts;
    private TotalValueDTO totalValue;
    private StockStatusDTO stockStatus;
    private List<CategoryBreakdownDTO> byCategory;
    private List<WarehouseBreakdownDTO> byWarehouse;
    private List<TopProductDTO> topProducts;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalValueDTO {
        private BigDecimal cost;
        private BigDecimal retail;
        private BigDecimal potentialProfit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockStatusDTO {
        private int inStock;
        private int lowStock;
        private int outOfStock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryBreakdownDTO {
        private Long categoryId;
        private String categoryName;
        private int productCount;
        private BigDecimal totalValue;
        private int lowStockCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseBreakdownDTO {
        private Long warehouseId;
        private String warehouseName;
        private int productCount;
        private BigDecimal totalValue;
        private double utilization;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductDTO {
        private Long productId;
        private String sku;
        private String name;
        private int totalQuantity;
        private BigDecimal totalValue;
    }
}