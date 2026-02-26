package com.moeware.ims.dto.transaction.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.moeware.ims.enums.transaction.PurchaseOrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Full response DTO for a purchase order (used in detail views)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Full purchase order details including line items")
public class PurchaseOrderResponse {

    @Schema(description = "Purchase order ID", example = "123")
    private Long id;

    @Schema(description = "Purchase order number", example = "PO-20260220-0001")
    private String poNumber;

    @Schema(description = "Supplier information")
    private SupplierSummary supplier;

    @Schema(description = "Destination warehouse information")
    private WarehouseSummary warehouse;

    @Schema(description = "User who created the order")
    private UserSummary createdByUser;

    @Schema(description = "Current status of the purchase order", example = "DRAFT")
    private PurchaseOrderStatus status;

    @Schema(description = "Order placement date", example = "2026-02-20")
    private LocalDate orderDate;

    @Schema(description = "Expected delivery date", example = "2026-03-05")
    private LocalDate expectedDeliveryDate;

    @Schema(description = "Actual delivery date (set on receipt)", example = "2026-03-04")
    private LocalDate actualDeliveryDate;

    @Schema(description = "Subtotal before tax and discount", example = "8999.90")
    private BigDecimal subtotal;

    @Schema(description = "Tax amount", example = "720.00")
    private BigDecimal taxAmount;

    @Schema(description = "Discount amount", example = "50.00")
    private BigDecimal discountAmount;

    @Schema(description = "Total order amount (subtotal + tax - discount)", example = "9669.90")
    private BigDecimal totalAmount;

    @Schema(description = "Order notes", example = "Please deliver to loading dock B")
    private String notes;

    @Schema(description = "Number of line items", example = "3")
    private int itemCount;

    @Schema(description = "Line items in this purchase order")
    private List<PurchaseOrderItemResponse> items;

    @Schema(description = "Optimistic locking version", example = "1")
    private Long version;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Username of the creator (audit)")
    private String createdBy;

    @Schema(description = "Username of the last modifier (audit)")
    private String updatedBy;

    // ---- Nested summaries ----

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Supplier summary in purchase order response")
    public static class SupplierSummary {
        @Schema(description = "Supplier ID", example = "1")
        private Long id;
        @Schema(description = "Supplier name", example = "Tech Supplies Inc")
        private String name;
        @Schema(description = "Supplier code", example = "SUP001")
        private String code;
        @Schema(description = "Contact person", example = "John Smith")
        private String contactPerson;
        @Schema(description = "Contact email", example = "orders@techsupplies.com")
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Warehouse summary in purchase order response")
    public static class WarehouseSummary {
        @Schema(description = "Warehouse ID", example = "1")
        private Long id;
        @Schema(description = "Warehouse name", example = "Main Warehouse")
        private String name;
        @Schema(description = "Warehouse code", example = "WH001")
        private String code;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "User summary in purchase order response")
    public static class UserSummary {
        @Schema(description = "User ID", example = "2")
        private Long id;
        @Schema(description = "Username", example = "john_manager")
        private String username;
        @Schema(description = "Email", example = "john@inventory.com")
        private String email;
    }
}