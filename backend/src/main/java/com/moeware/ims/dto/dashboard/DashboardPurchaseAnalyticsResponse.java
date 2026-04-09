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
 * Dashboard purchase analytics response.
 * Includes PO summaries, status breakdowns, top suppliers, category spending,
 * and monthly trends.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPurchaseAnalyticsResponse {

    private DashboardPeriod period;
    private LocalDate startDate;
    private LocalDate endDate;
    private SummaryDTO summary;
    private Map<String, Integer> byStatus;
    private List<TopSupplierDTO> topSuppliers;
    private List<CategorySpendingDTO> categorySpending;
    private List<MonthlyTrendDTO> monthlyTrend;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryDTO {
        private int totalOrders;
        private BigDecimal totalSpent;
        private BigDecimal averageOrderValue;
        private int totalItemsOrdered;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopSupplierDTO {
        private Long supplierId;
        private String supplierName;
        private int orderCount;
        private BigDecimal totalSpent;
        private double onTimeDeliveryRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySpendingDTO {
        private Long categoryId;
        private String categoryName;
        private BigDecimal totalSpent;
        private int orderCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyTrendDTO {
        /** Format: YYYY-MM */
        private String month;
        private int orders;
        private BigDecimal totalSpent;
    }
}