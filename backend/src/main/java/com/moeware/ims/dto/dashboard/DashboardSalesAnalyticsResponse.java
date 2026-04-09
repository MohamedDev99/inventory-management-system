package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.moeware.ims.enums.DashboardPeriod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard sales analytics response.
 * Includes order summaries, status breakdowns, top products/customers, and
 * daily trends.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSalesAnalyticsResponse {

    private DashboardPeriod period;
    private LocalDate startDate;
    private LocalDate endDate;
    private SummaryDTO summary;
    private Map<String, Integer> byStatus;
    private List<TopProductDTO> topProducts;
    private List<TopCustomerDTO> topCustomers;
    private List<DailyTrendDTO> dailyTrend;
    private GrowthMetricsDTO growthMetrics;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryDTO {
        private int totalOrders;
        private BigDecimal totalRevenue;
        private BigDecimal averageOrderValue;
        private int totalItemsSold;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductDTO {
        private Long productId;
        private String sku;
        private String name;
        private int unitsSold;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCustomerDTO {
        private Long customerId;
        private String customerName;
        private int orderCount;
        private BigDecimal totalSpent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyTrendDTO {
        private LocalDate date;
        private int orders;
        private BigDecimal revenue;
        private int itemsSold;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrowthMetricsDTO {
        private double revenueGrowth;
        private double orderGrowth;
        private double averageOrderValueGrowth;
    }
}