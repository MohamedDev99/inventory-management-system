package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "System-wide dashboard KPIs returned by the overview endpoint")
public class DashboardOverviewResponse {

    @Schema(description = "Core inventory and user metrics")
    private MetricsDTO metrics;

    @Schema(description = "Current order queue counts by status")
    private OrdersDTO orders;

    @Schema(description = "Counts of operations performed today")
    private RecentActivityDTO recentActivity;

    @Schema(description = "Items requiring attention (low stock, approvals, overdue invoices)")
    private AlertsDTO alerts;

    @Schema(description = "Revenue figures across different time windows")
    private RevenueDTO revenue;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Core inventory and user metrics")
    public static class MetricsDTO {

        @Schema(description = "Total number of active products in the catalog", example = "450")
        private int totalProducts;

        @Schema(description = "Total retail value of all inventory across all warehouses", example = "8750000.00")
        private BigDecimal totalInventoryValue;

        @Schema(description = "Number of active products at or below their reorder level", example = "12")
        private int lowStockProducts;

        @Schema(description = "Number of active products with zero stock", example = "3")
        private int outOfStockProducts;

        @Schema(description = "Number of active warehouses", example = "5")
        private int totalWarehouses;

        @Schema(description = "Number of currently active system users", example = "85")
        private int activeUsers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Current order queue counts by status")
    public static class OrdersDTO {

        @Schema(description = "Sales orders in PENDING status", example = "25")
        private int pendingSalesOrders;

        @Schema(description = "Sales orders in CONFIRMED status (inventory reserved)", example = "48")
        private int confirmedSalesOrders;

        @Schema(description = "Purchase orders in SUBMITTED status awaiting manager approval", example = "8")
        private int pendingPurchaseOrders;

        @Schema(description = "Purchase orders in APPROVED status awaiting receipt", example = "15")
        private int approvedPurchaseOrders;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Counts of operations performed today")
    public static class RecentActivityDTO {

        @Schema(description = "Sales orders created today", example = "12")
        private int salesOrdersToday;

        @Schema(description = "Purchase orders created today", example = "3")
        private int purchaseOrdersToday;

        @Schema(description = "Shipments created today", example = "15")
        private int shipmentsToday;

        @Schema(description = "Purchase orders received (status = RECEIVED) today", example = "8")
        private int receivedToday;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Counts of items requiring immediate attention")
    public static class AlertsDTO {

        @Schema(description = "Products at or below their reorder level", example = "12")
        private int lowStockAlerts;

        @Schema(description = "Total pending approvals (POs + stock adjustments)", example = "11")
        private int pendingApprovals;

        @Schema(description = "Invoices past their due date that are not fully paid or cancelled", example = "5")
        private int overdueInvoices;

        @Schema(description = "Stock adjustments in PENDING status awaiting approval", example = "3")
        private int pendingAdjustments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Revenue figures across different time windows (excludes CANCELLED and PENDING orders)")
    public static class RevenueDTO {

        @Schema(description = "Revenue generated today", example = "45000.00")
        private BigDecimal today;

        @Schema(description = "Revenue generated in the last 7 days", example = "180000.00")
        private BigDecimal thisWeek;

        @Schema(description = "Revenue generated in the current calendar month", example = "650000.00")
        private BigDecimal thisMonth;

        @Schema(description = "Revenue generated in the previous calendar month", example = "580000.00")
        private BigDecimal lastMonth;

        @Schema(description = "Month-over-month revenue growth percentage (positive = growth)", example = "12.07")
        private double growth;
    }
}