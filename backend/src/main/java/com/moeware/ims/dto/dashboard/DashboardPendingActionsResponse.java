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
 * Dashboard pending-actions response.
 * Aggregates all items that require user attention (approvals, overdue
 * invoices, shipments).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "All items currently requiring manager or admin attention, grouped by type")
public class DashboardPendingActionsResponse {

    @Schema(description = "Purchase orders and stock adjustments awaiting approval")
    private PendingApprovalsDTO pendingApprovals;

    @Schema(description = "Invoices that have passed their due date without full payment")
    private OverdueInvoicesDTO overdueInvoices;

    @Schema(description = "Shipments that have not yet been dispatched")
    private PendingShipmentsDTO pendingShipments;

    @Schema(description = "Number of active products currently at or below their reorder level", example = "12")
    private int lowStockAlerts;

    @Schema(description = "Flat, prioritised list of individual action items for the dashboard panel")
    private List<ActionItemDTO> items;

    // ─── Nested DTOs ─────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Counts of records awaiting approval")
    public static class PendingApprovalsDTO {

        @Schema(description = "Purchase orders in SUBMITTED status awaiting manager approval", example = "8")
        private int purchaseOrders;

        @Schema(description = "Stock adjustments in PENDING status awaiting approval", example = "3")
        private int stockAdjustments;

        @Schema(description = "Combined total of all pending approvals", example = "11")
        private int total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Summary of invoices past their due date")
    public static class OverdueInvoicesDTO {

        @Schema(description = "Number of overdue invoices", example = "5")
        private int count;

        @Schema(description = "Sum of balance due across all overdue invoices", example = "28500.00")
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Summary of shipments in PENDING status")
    public static class PendingShipmentsDTO {

        @Schema(description = "Total number of shipments in PENDING status", example = "18")
        private int count;

        @Schema(description = "The oldest pending shipment. Null when there are no pending shipments.")
        private OldestShipmentDTO oldestShipment;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "The oldest shipment currently in PENDING status")
    public static class OldestShipmentDTO {

        @Schema(description = "Shipment ID", example = "75")
        private Long id;

        @Schema(description = "Shipment number", example = "SHIP-20260205-0032")
        private String shipmentNumber;

        @Schema(description = "Number of calendar days the shipment has been waiting", example = "4")
        private int daysWaiting;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "An individual action item surfaced in the dashboard action panel")
    public static class ActionItemDTO {

        @Schema(description = "Action type identifier", example = "PURCHASE_ORDER_APPROVAL", allowableValues = {
                "PURCHASE_ORDER_APPROVAL", "OVERDUE_INVOICE" })
        private String type;

        @Schema(description = "ID of the related entity (PO ID or Invoice ID)", example = "124")
        private Long id;

        @Schema(description = "Human-readable title for the action item", example = "Purchase Order PO-20260209-0002 awaiting approval")
        private String title;

        @Schema(description = "Priority level for display ordering", example = "HIGH", allowableValues = { "LOW",
                "MEDIUM", "HIGH", "CRITICAL" })
        private String priority;

        @Schema(description = "Monetary amount related to the action item (balance due for invoices, order total for POs)", example = "5800.00")
        private BigDecimal amount;

        @Schema(description = "Customer name — populated for invoice action items", example = "ABC Corporation")
        private String customer;

        @Schema(description = "Timestamp when the underlying record was created", example = "2026-02-09T14:00:00")
        private LocalDateTime createdAt;

        @Schema(description = "Frontend route for navigating to the entity detail page", example = "/purchase-orders/124")
        private String actionUrl;
    }
}