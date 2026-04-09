package com.moeware.ims.dto.dashboard;

import java.util.List;

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
public class DashboardLowStockAlertsResponse {

    private int totalAlerts;
    private int criticalAlerts;
    private List<LowStockProductDTO> products;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowStockProductDTO {
        private Long productId;
        private String sku;
        private String name;
        private int totalStock;
        private int reorderLevel;
        private int shortage;
        /** CRITICAL when totalStock <= minStockLevel, else WARNING */
        private String severity;
        private List<WarehouseStockDTO> warehouses;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseStockDTO {
        private Long warehouseId;
        private String warehouseName;
        private int quantity;
    }
}