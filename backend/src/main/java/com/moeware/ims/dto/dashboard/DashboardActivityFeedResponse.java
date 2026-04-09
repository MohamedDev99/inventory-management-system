package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard activity-feed response.
 * Returns the most recent system events across all entity types.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardActivityFeedResponse {

    private List<ActivityDTO> activities;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityDTO {
        private Long id;
        /**
         * e.g. SALES_ORDER_CREATED, PURCHASE_ORDER_APPROVED,
         * SHIPMENT_DELIVERED, STOCK_ADJUSTMENT_APPROVED
         */
        private String type;
        private String title;
        private String description;
        private UserSummaryDTO user;
        private ActivityMetadataDTO metadata;
        private LocalDateTime timestamp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDTO {
        private Long id;
        private String username;
    }

    /**
     * Flexible metadata bag – fields are populated depending on the activity type.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityMetadataDTO {
        // Sales / purchase order fields
        private Long orderId;
        private String orderNumber;
        private String customerName;
        private String supplierName;
        private BigDecimal totalAmount;
        // Shipment fields
        private Long shipmentId;
        private String shipmentNumber;
    }
}