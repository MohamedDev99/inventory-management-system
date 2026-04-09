package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.moeware.ims.enums.DashboardPeriod;

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
public class DashboardSalesTrendResponse {

    private DashboardPeriod period;
    private List<DataPointDTO> dataPoints;
    private ComparisonDTO comparison;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPointDTO {
        private LocalDate date;
        private int orders;
        private BigDecimal revenue;
        private int itemsSold;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparisonDTO {
        private PreviousPeriodDTO previousPeriod;
        private GrowthDTO growth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreviousPeriodDTO {
        private int totalOrders;
        private BigDecimal totalRevenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrowthDTO {
        private double ordersGrowth;
        private double revenueGrowth;
    }
}