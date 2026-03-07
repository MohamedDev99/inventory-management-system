package com.moeware.ims.dto.transaction.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.moeware.ims.enums.transaction.InvoiceStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Full invoice details response")
public class InvoiceResponse {

    @Schema(description = "Unique identifier", example = "234")
    private Long id;

    @Schema(description = "Unique invoice number", example = "INV-20260209-0078")
    private String invoiceNumber;

    @Schema(description = "Related sales order summary")
    private SalesOrderSummary salesOrder;

    @Schema(description = "Customer being billed")
    private CustomerSummary customer;

    @Schema(description = "Date invoice was issued", example = "2026-02-09")
    private LocalDate invoiceDate;

    @Schema(description = "Payment due date", example = "2026-03-11")
    private LocalDate dueDate;

    @Schema(description = "Subtotal before tax and discounts", example = "2599.98")
    private BigDecimal subtotal;

    @Schema(description = "Tax amount", example = "207.99")
    private BigDecimal taxAmount;

    @Schema(description = "Discount amount", example = "0.00")
    private BigDecimal discountAmount;

    @Schema(description = "Total invoice amount", example = "2807.97")
    private BigDecimal totalAmount;

    @Schema(description = "Amount already paid", example = "0.00")
    private BigDecimal paidAmount;

    @Schema(description = "Remaining balance due", example = "2807.97")
    private BigDecimal balanceDue;

    @Schema(description = "Current invoice status", example = "SENT")
    private InvoiceStatus invoiceStatus;

    @Schema(description = "Payment terms", example = "Net 30")
    private String paymentTerms;

    @Schema(description = "Invoice notes")
    private String notes;

    @Schema(description = "URL to generated PDF file", example = "https://s3.amazonaws.com/invoices/INV-20260209-0078.pdf")
    private String fileUrl;

    @Schema(description = "User who generated this invoice")
    private UserSummary generatedBy;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Optimistic lock version", example = "1")
    private Long version;

    // --- Nested summary classes ---

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesOrderSummary {
        private Long id;
        private String soNumber;
        private LocalDate orderDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerSummary {
        private Long id;
        private String customerCode;
        private String contactName;
        private String companyName;
        private String email;
        // Billing address for invoice
        private String billingAddress;
        private String billingCity;
        private String billingState;
        private String billingPostalCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String username;
        private String email;
    }
}