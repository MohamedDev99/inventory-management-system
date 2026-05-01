package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.moeware.ims.enums.dashboard.DashboardPeriod;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Purchase-order analytics for the requested period, including spend totals, " +
        "top supplier rankings, category spending breakdown, and monthly trend data")
public class DashboardPurchaseAnalyticsResponse {

    @Schema(description = "The resolved time window", example = "MONTH")
    private DashboardPeriod period;

    @Schema(description = "Inclusive start date of the analytics window", example = "2026-02-01")
    private LocalDate startDate;

    @Schema(description = "Inclusive end date of the analytics window", example = "2026-02-28")
    private LocalDate endDate;

    @Schema(description = "Aggregate purchase-order totals for the period")
    private SummaryDTO summary;

    @Schema(description = "Purchase order count grouped by status name (e.g. DRAFT, APPROVED, RECEIVED)")
    private Map<String, Integer> byStatus;

    @Schema(description = "Top 10 suppliers ranked by total spend in the period")
    private List<TopSupplierDTO> topSuppliers;

    @Schema(description = "Total spend broken down by product category")
    private List<CategorySpendingDTO> categorySpending;

    @Schema(description = "Month-by-month order count and spend within the analytics window")
    private List<MonthlyTrendDTO> monthlyTrend;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Aggregate purchase-order totals (excludes CANCELLED orders)")
    public static class SummaryDTO {

        @Schema(description = "Total number of non-cancelled purchase orders in the period", example = "45")
        private int totalOrders;

        @Schema(description = "Total amount spent on non-cancelled purchase orders", example = "385000.00")
        private BigDecimal totalSpent;

        @Schema(description = "Average purchase order value (totalSpent ÷ totalOrders)", example = "8555.56")
        private BigDecimal averageOrderValue;

        @Schema(description = "Total quantity of items ordered across all PO line items", example = "1250")
        private int totalItemsOrdered;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Top supplier by total spend entry")
    public static class TopSupplierDTO {

        @Schema(description = "Supplier ID", example = "1")
        private Long supplierId;

        @Schema(description = "Supplier name", example = "Tech Supplies Inc")
        private String supplierName;

        @Schema(description = "Number of purchase orders placed with this supplier in the period", example = "12")
        private int orderCount;

        @Schema(description = "Total amount spent with this supplier in the period", example = "125000.00")
        private BigDecimal totalSpent;

        @Schema(description = "On-time delivery rate for this supplier (0–100). " +
                "Populated via the dedicated supplier-performance endpoint; 0.0 in this summary.", example = "95.5")
        private double onTimeDeliveryRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Purchase spend for a single product category")
    public static class CategorySpendingDTO {

        @Schema(description = "Category ID", example = "1")
        private Long categoryId;

        @Schema(description = "Category name", example = "Electronics")
        private String categoryName;

        @Schema(description = "Total spend on products in this category in the period", example = "280000.00")
        private BigDecimal totalSpent;

        @Schema(description = "Number of distinct purchase orders that include products from this category", example = "28")
        private int orderCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Monthly purchase-order summary data point")
    public static class MonthlyTrendDTO {

        @Schema(description = "Year-month in YYYY-MM format", example = "2026-02")
        private String month;

        @Schema(description = "Number of non-cancelled purchase orders in this month", example = "45")
        private int orders;

        @Schema(description = "Total spend on non-cancelled purchase orders in this month", example = "385000.00")
        private BigDecimal totalSpent;
    }
}