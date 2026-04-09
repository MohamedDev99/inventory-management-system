package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Top-level dashboard overview response.
 * Aggregates system-wide metrics, order counts, activity counts, alerts, and
 * revenue.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewResponse {

    private MetricsDTO metrics;
    private OrdersDTO orders;
    private RecentActivityDTO recentActivity;
    private AlertsDTO alerts;
    private RevenueDTO revenue;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricsDTO {
        private int totalProducts;
        private BigDecimal totalInventoryValue;
        private int lowStockProducts;
        private int outOfStockProducts;
        private int totalWarehouses;
        private int activeUsers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrdersDTO {
        private int pendingSalesOrders;
        private int confirmedSalesOrders;
        private int pendingPurchaseOrders;
        private int approvedPurchaseOrders;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivityDTO {
        private int salesOrdersToday;
        private int purchaseOrdersToday;
        private int shipmentsToday;
        private int receivedToday;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertsDTO {
        private int lowStockAlerts;
        private int pendingApprovals;
        private int overdueInvoices;
        private int pendingAdjustments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueDTO {
        private BigDecimal today;
        private BigDecimal thisWeek;
        private BigDecimal thisMonth;
        private BigDecimal lastMonth;
        private double growth;
    }
}