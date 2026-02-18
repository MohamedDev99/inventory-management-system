package com.moeware.ims.dto.inventory.inventoryItem;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Inventory item response with product and warehouse details")
public class InventoryItemDTO {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Product information")
    private ProductSummaryDTO product;

    @Schema(description = "Warehouse information")
    private WarehouseSummaryDTO warehouse;

    @Schema(description = "Current stock quantity", example = "45")
    private Integer quantity;

    @Schema(description = "Physical location code within warehouse", example = "A-12-3")
    private String locationCode;

    @Schema(description = "Reorder level threshold", example = "5")
    private Integer reorderLevel;

    @Schema(description = "Indicates if stock is below reorder level", example = "false")
    private Boolean isLowStock;

    @Schema(description = "Stock status: CRITICAL, LOW, or NORMAL", example = "NORMAL")
    private String stockStatus;

    @Schema(description = "Last physical stock count timestamp", example = "2026-02-08T14:00:00")
    private LocalDateTime lastStockCheck;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Version for optimistic locking", example = "28")
    private Long version;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Product summary information")
    public static class ProductSummaryDTO {
        @Schema(description = "Product ID", example = "10")
        private Long id;

        @Schema(description = "Product SKU", example = "LAP-001")
        private String sku;

        @Schema(description = "Product name", example = "Dell Laptop XPS 15")
        private String name;

        @Schema(description = "Unit price", example = "1299.99")
        private java.math.BigDecimal unitPrice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Warehouse summary information")
    public static class WarehouseSummaryDTO {
        @Schema(description = "Warehouse ID", example = "1")
        private Long id;

        @Schema(description = "Warehouse name", example = "Main Warehouse")
        private String name;

        @Schema(description = "Warehouse code", example = "WH001")
        private String code;
    }
}