package com.moeware.ims.dto.staff.warehouse;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for warehouse statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Warehouse statistics and metrics")
public class WarehouseStatsResponse {

    @Schema(description = "Warehouse ID", example = "1")
    private Long warehouseId;

    @Schema(description = "Warehouse name", example = "Main Warehouse")
    private String warehouseName;

    @Schema(description = "Total number of different products", example = "125")
    private Integer totalProducts;

    @Schema(description = "Total stock units across all products", example = "5432")
    private Integer totalUnits;

    @Schema(description = "Total inventory value at cost price", example = "875000.50")
    private BigDecimal totalValue;

    @Schema(description = "Number of low stock products", example = "8")
    private Integer lowStockProducts;

    @Schema(description = "Number of out of stock products", example = "3")
    private Integer outOfStockProducts;

    @Schema(description = "Warehouse capacity", example = "50000.00")
    private BigDecimal capacity;

    @Schema(description = "Capacity utilization percentage", example = "67.5")
    private BigDecimal capacityUtilization;
}