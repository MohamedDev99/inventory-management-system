package com.moeware.ims.dto.inventory.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Supplier Performance Metrics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Supplier performance metrics")
public class SupplierPerformanceDTO {

    @Schema(description = "Supplier ID", example = "1")
    private Long supplierId;

    @Schema(description = "Supplier name", example = "Tech Supplies Inc")
    private String supplierName;

    @Schema(description = "Supplier code", example = "SUP001")
    private String supplierCode;

    @Schema(description = "Supplier rating", example = "5")
    private Integer rating;

    @Schema(description = "Performance metrics")
    private PerformanceMetrics metrics;

    @Schema(description = "Performance trends")
    private PerformanceTrends trends;

    @Schema(description = "Recommendations based on performance")
    private String recommendations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceMetrics {
        @Schema(description = "Total number of purchase orders", example = "45")
        private Long totalOrders;

        @Schema(description = "Total amount spent with supplier", example = "125000.00")
        private BigDecimal totalSpent;

        @Schema(description = "Average order value", example = "2777.78")
        private BigDecimal averageOrderValue;

        @Schema(description = "On-time delivery rate percentage", example = "95.5")
        private Double onTimeDeliveryRate;

        @Schema(description = "Average delivery time in days", example = "3.2")
        private Double averageDeliveryDays;

        @Schema(description = "Number of cancelled orders", example = "1")
        private Long cancelledOrders;

        @Schema(description = "Cancellation rate percentage", example = "2.2")
        private Double cancelledOrderRate;

        @Schema(description = "Number of quality issues reported", example = "2")
        private Long qualityIssues;

        @Schema(description = "Quality issue rate percentage", example = "4.4")
        private Double qualityIssueRate;

        @Schema(description = "Average response time", example = "2 hours")
        private String responseTime;

        @Schema(description = "Number of pending orders", example = "3")
        private Long pendingOrders;

        @Schema(description = "Number of completed orders", example = "41")
        private Long completedOrders;

        @Schema(description = "Completion rate percentage", example = "91.1")
        private Double completionRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceTrends {
        @Schema(description = "Order frequency", example = "Weekly")
        private String orderFrequency;

        @Schema(description = "Spending trend", example = "Increasing")
        private String spendTrend;

        @Schema(description = "Quality trend", example = "Stable")
        private String qualityTrend;

        @Schema(description = "Delivery performance trend", example = "Improving")
        private String deliveryTrend;
    }
}