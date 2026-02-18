package com.moeware.ims.dto.inventory;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Inventory valuation summary")
public class InventoryValuationResponse {

    @Schema(description = "Total number of unique products", example = "450")
    private Integer totalProducts;

    @Schema(description = "Total inventory units across all warehouses", example = "12500")
    private Integer totalUnits;

    @Schema(description = "Total inventory cost value", example = "8750000.00")
    private BigDecimal costValue;

    @Schema(description = "Total inventory retail value", example = "13125000.00")
    private BigDecimal retailValue;

    @Schema(description = "Potential profit (retail - cost)", example = "4375000.00")
    private BigDecimal potentialProfit;

    @Schema(description = "Valuation breakdown by warehouse")
    private List<WarehouseValuation> byWarehouse;

    @Schema(description = "Valuation breakdown by category")
    private List<CategoryValuation> byCategory;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Valuation for a single warehouse")
    public static class WarehouseValuation {
        @Schema(description = "Warehouse ID", example = "1")
        private Long warehouseId;

        @Schema(description = "Warehouse name", example = "Main Warehouse")
        private String warehouseName;

        @Schema(description = "Number of products in warehouse", example = "250")
        private Integer products;

        @Schema(description = "Total units in warehouse", example = "7500")
        private Integer units;

        @Schema(description = "Cost value", example = "5250000.00")
        private BigDecimal costValue;

        @Schema(description = "Retail value", example = "7875000.00")
        private BigDecimal retailValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Valuation for a single category")
    public static class CategoryValuation {
        @Schema(description = "Category ID", example = "1")
        private Long categoryId;

        @Schema(description = "Category name", example = "Electronics")
        private String categoryName;

        @Schema(description = "Cost value", example = "6125000.00")
        private BigDecimal costValue;

        @Schema(description = "Retail value", example = "9187500.00")
        private BigDecimal retailValue;
    }
}