package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Reverse-chronological feed of recent system events merged from " +
        "sales orders, purchase order changes, and shipment deliveries")
public class DashboardActivityFeedResponse {

    @Schema(description = "Activity entries sorted newest-first, capped at the requested limit (max 100)")
    private List<ActivityDTO> activities;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "A single activity event in the feed")
    public static class ActivityDTO {

        @Schema(description = "ID of the underlying entity (sales order ID, PO ID, or shipment ID)", example = "456")
        private Long id;

        @Schema(description = "Machine-readable activity type", example = "SALES_ORDER_CREATED", allowableValues = {
                "SALES_ORDER_CREATED", "SALES_ORDER_UPDATED",
                "PURCHASE_ORDER_APPROVED", "PURCHASE_ORDER_UPDATED",
                "SHIPMENT_DELIVERED"
        })
        private String type;

        @Schema(description = "Human-readable title for the activity", example = "New sales order created")
        private String title;

        @Schema(description = "Full description of the activity", example = "SO-20260209-0045 pending by sales_rep")
        private String description;

        @Schema(description = "User who triggered the activity")
        private UserSummaryDTO user;

        @Schema(description = "Additional entity-specific data. Fields are populated depending on the activity type.")
        private ActivityMetadataDTO metadata;

        @Schema(description = "Timestamp when the activity occurred", example = "2026-02-09T15:45:00")
        private LocalDateTime timestamp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Summary of the user who triggered the activity")
    public static class UserSummaryDTO {

        @Schema(description = "User ID", example = "3")
        private Long id;

        @Schema(description = "Username", example = "sales_rep")
        private String username;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Flexible metadata bag — not all fields are populated for every activity type. " +
            "Sales/PO activities populate orderId, orderNumber, customerName/supplierName, totalAmount. " +
            "Shipment activities populate shipmentId and shipmentNumber.")
    public static class ActivityMetadataDTO {

        @Schema(description = "Sales order or purchase order ID", example = "458")
        private Long orderId;

        @Schema(description = "Sales order or purchase order number", example = "SO-20260209-0045")
        private String orderNumber;

        @Schema(description = "Customer name — populated for sales order activities", example = "Tech Solutions Inc")
        private String customerName;

        @Schema(description = "Supplier name — populated for purchase order activities", example = "Tech Supplies Inc")
        private String supplierName;

        @Schema(description = "Order total amount", example = "8500.00")
        private BigDecimal totalAmount;

        @Schema(description = "Shipment ID — populated for shipment delivery activities", example = "75")
        private Long shipmentId;

        @Schema(description = "Shipment number — populated for shipment delivery activities", example = "SHIP-20260205-0032")
        private String shipmentNumber;
    }
}