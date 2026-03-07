package com.moeware.ims.dto.transaction.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.moeware.ims.enums.transaction.SalesOrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Full response DTO for a sales order (used in detail views)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Full sales order details including line items")
public class SalesOrderResponse {

    @Schema(description = "Sales order ID", example = "456")
    private Long id;

    @Schema(description = "Sales order number", example = "SO-20260220-0042")
    private String soNumber;

    @Schema(description = "Customer information")
    private CustomerSummary customer;

    @Schema(description = "Customer name at time of order (denormalized)", example = "John Doe")
    private String customerName;

    @Schema(description = "Customer email at time of order", example = "john.doe@email.com")
    private String customerEmail;

    @Schema(description = "Customer phone at time of order", example = "+15550100")
    private String customerPhone;

    @Schema(description = "Shipping street address", example = "789 Customer St, Apt 4B")
    private String shippingAddress;

    @Schema(description = "Shipping city", example = "New York")
    private String city;

    @Schema(description = "Shipping postal code", example = "10001")
    private String postalCode;

    @Schema(description = "Fulfillment warehouse")
    private WarehouseSummary warehouse;

    @Schema(description = "User who created the order")
    private UserSummary createdByUser;

    @Schema(description = "Current status of the sales order", example = "CONFIRMED")
    private SalesOrderStatus status;

    @Schema(description = "Order placement date", example = "2026-02-20")
    private LocalDate orderDate;

    @Schema(description = "Date order was picked and packed", example = "2026-02-21")
    private LocalDate fulfillmentDate;

    @Schema(description = "Date order was shipped", example = "2026-02-22")
    private LocalDate shippingDate;

    @Schema(description = "Date order was delivered", example = "2026-02-25")
    private LocalDate deliveryDate;

    @Schema(description = "Subtotal before tax and shipping", example = "2599.98")
    private BigDecimal subtotal;

    @Schema(description = "Tax amount", example = "207.99")
    private BigDecimal taxAmount;

    @Schema(description = "Shipping cost", example = "15.00")
    private BigDecimal shippingCost;

    @Schema(description = "Total order amount (subtotal + tax + shipping)", example = "2822.97")
    private BigDecimal totalAmount;

    @Schema(description = "Order notes or special delivery instructions", example = "Please ring doorbell")
    private String notes;

    @Schema(description = "Number of line items", example = "2")
    private int itemCount;

    @Schema(description = "Line items in this sales order")
    private List<SalesOrderItemResponse> items;

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
    @Schema(description = "Customer summary in sales order response")
    public static class CustomerSummary {
        @Schema(description = "Customer ID", example = "15")
        private Long id;
        @Schema(description = "Customer code", example = "CUST-015")
        private String customerCode;
        @Schema(description = "Contact name", example = "John Doe")
        private String contactName;
        @Schema(description = "Email address", example = "john.doe@email.com")
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Warehouse summary in sales order response")
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
    @Schema(description = "User summary in sales order response")
    public static class UserSummary {
        @Schema(description = "User ID", example = "2")
        private Long id;
        @Schema(description = "Username", example = "john_manager")
        private String username;
        @Schema(description = "Email", example = "john@inventory.com")
        private String email;
    }
}