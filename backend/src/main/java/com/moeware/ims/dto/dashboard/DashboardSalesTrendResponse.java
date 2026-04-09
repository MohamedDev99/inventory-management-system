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
 * Chart data for the sales-trend widget.
 * Includes daily order/revenue data and a period-over-period growth comparison.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Daily sales data for the sales-trend chart widget, " +
        "including a period-over-period growth comparison")
public class DashboardSalesTrendResponse {

    @Schema(description = "The resolved time window used to generate the data points", example = "MONTH")
    private DashboardPeriod period;

    @Schema(description = "One data point per calendar day in the requested period, ordered chronologically")
    private List<DataPointDTO> dataPoints;

    @Schema(description = "Comparison of current period totals against the previous equal-length period")
    private ComparisonDTO comparison;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Sales data for a single calendar day (excludes CANCELLED orders)")
    public static class DataPointDTO {

        @Schema(description = "Calendar date of this data point", example = "2026-02-01")
        private LocalDate date;

        @Schema(description = "Number of non-cancelled sales orders on this date", example = "15")
        private int orders;

        @Schema(description = "Revenue from non-cancelled sales orders on this date", example = "78000.00")
        private BigDecimal revenue;

        @Schema(description = "Total items sold across all line items on this date", example = "95")
        private int itemsSold;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Period-over-period comparison between the current and previous equal-length windows")
    public static class ComparisonDTO {

        @Schema(description = "Aggregate totals for the previous period")
        private PreviousPeriodDTO previousPeriod;

        @Schema(description = "Growth percentages relative to the previous period")
        private GrowthDTO growth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Aggregate totals for the previous equal-length period")
    public static class PreviousPeriodDTO {

        @Schema(description = "Total non-cancelled sales orders in the previous period", example = "115")
        private int totalOrders;

        @Schema(description = "Total revenue in the previous period", example = "598000.00")
        private BigDecimal totalRevenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Growth percentages vs. the previous equal-length period. Positive = growth, negative = decline.")
    public static class GrowthDTO {

        @Schema(description = "Order count growth percentage", example = "8.70")
        private double ordersGrowth;

        @Schema(description = "Revenue growth percentage", example = "8.70")
        private double revenueGrowth;
    }
}