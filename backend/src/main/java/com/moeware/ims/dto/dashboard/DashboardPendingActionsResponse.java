package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard pending-actions response.
 * Aggregates all items that require user attention (approvals, overdue
 * invoices, shipments).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPendingActionsResponse {

    private PendingApprovalsDTO pendingApprovals;
    private OverdueInvoicesDTO overdueInvoices;
    private PendingShipmentsDTO pendingShipments;
    private int lowStockAlerts;
    private List<ActionItemDTO> items;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingApprovalsDTO {
        private int purchaseOrders;
        private int stockAdjustments;
        private int total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverdueInvoicesDTO {
        private int count;
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingShipmentsDTO {
        private int count;
        private OldestShipmentDTO oldestShipment;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OldestShipmentDTO {
        private Long id;
        private String shipmentNumber;
        private int daysWaiting;
    }

    /**
     * An individual action item surfaced in the dashboard action feed.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionItemDTO {
        /** e.g. PURCHASE_ORDER_APPROVAL, OVERDUE_INVOICE */
        private String type;
        private Long id;
        private String title;
        /** HIGH, CRITICAL, MEDIUM, LOW */
        private String priority;
        /** For invoice items: amount outstanding */
        private BigDecimal amount;
        private String customer;
        private LocalDateTime createdAt;
        private String actionUrl;
    }
}