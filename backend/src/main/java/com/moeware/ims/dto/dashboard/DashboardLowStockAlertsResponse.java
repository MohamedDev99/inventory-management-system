package com.moeware.ims.dto.dashboard;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard low-stock alerts response.
 * Lists all products below their reorder level with per-warehouse breakdown.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Low-stock alert summary: all active products at or below their reorder level, " +
        "with per-warehouse quantity and severity classification")
public class DashboardLowStockAlertsResponse {

    @Schema(description = "Total number of products in a low-stock or critical-stock state", example = "12")
    private int totalAlerts;

    @Schema(description = "Number of products in CRITICAL state (stock ≤ minStockLevel)", example = "3")
    private int criticalAlerts;

    @Schema(description = "List of low-stock products ordered by product ID")
    private List<LowStockProductDTO> products;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "A single product that is at or below its reorder level")
    public static class LowStockProductDTO {

        @Schema(description = "Product ID", example = "11")
        private Long productId;

        @Schema(description = "Product SKU", example = "LAP-002")
        private String sku;

        @Schema(description = "Product name", example = "MacBook Pro 14\"")
        private String name;

        @Schema(description = "Total quantity across all warehouses", example = "2")
        private int totalStock;

        @Schema(description = "Reorder level configured on the product", example = "5")
        private int reorderLevel;

        @Schema(description = "Units short of the reorder level (reorderLevel − totalStock). Always ≥ 0.", example = "3")
        private int shortage;

        @Schema(description = "CRITICAL when totalStock ≤ minStockLevel, WARNING otherwise", example = "CRITICAL", allowableValues = {
                "CRITICAL", "WARNING" })
        private String severity;

        @Schema(description = "Stock quantity per warehouse")
        private List<WarehouseStockDTO> warehouses;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Quantity of the low-stock product in a single warehouse")
    public static class WarehouseStockDTO {

        @Schema(description = "Warehouse ID", example = "1")
        private Long warehouseId;

        @Schema(description = "Warehouse name", example = "Main Warehouse")
        private String warehouseName;

        @Schema(description = "Current quantity of this product in this warehouse", example = "1")
        private int quantity;
    }
}