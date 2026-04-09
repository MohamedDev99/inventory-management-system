package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.moeware.ims.enums.DashboardPeriod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chart data for the inventory-trend widget.
 * Each data point captures total value, product count, and low-stock count on a
 * given date.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Per-day inventory snapshot data for the inventory-trend chart widget")
public class DashboardInventoryTrendResponse {

    @Schema(description = "The resolved time window used to generate the data points", example = "MONTH")
    private DashboardPeriod period;

    @Schema(description = "One data point per calendar day in the requested period, ordered chronologically")
    private List<DataPointDTO> dataPoints;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Inventory snapshot for a single calendar day")
    public static class DataPointDTO {

        @Schema(description = "Calendar date of this snapshot", example = "2026-02-09")
        private LocalDate date;

        @Schema(description = "Total retail value of all active inventory on this date", example = "8750000.00")
        private BigDecimal totalValue;

        @Schema(description = "Number of active products on this date", example = "450")
        private int productCount;

        @Schema(description = "Number of products at or below their reorder level on this date", example = "12")
        private int lowStockCount;
    }
}