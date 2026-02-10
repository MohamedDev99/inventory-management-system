package com.moeware.ims.entity.transaction;

import com.moeware.ims.entity.VersionedEntity;
import com.moeware.ims.entity.staff.Customer;
import com.moeware.ims.entity.User;
import com.moeware.ims.enums.InvoiceStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Invoice entity
 * Generates and tracks invoices for sales orders
 */
@Entity
@Table(name = "invoices", uniqueConstraints = @UniqueConstraint(columnNames = "invoice_number"), indexes = {
        @Index(name = "idx_invoice_sales_order", columnList = "sales_order_id"),
        @Index(name = "idx_invoice_customer", columnList = "customer_id"),
        @Index(name = "idx_invoice_status", columnList = "invoice_status"),
        @Index(name = "idx_invoice_date", columnList = "invoice_date"),
        @Index(name = "idx_invoice_due_date", columnList = "due_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Invoice document for customer billing")
public class Invoice extends VersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the invoice", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Invoice number is required")
    @Size(max = 50)
    @Column(name = "invoice_number", nullable = false, unique = true)
    @Schema(description = "Unique invoice number for accounting", example = "INV-20260131-0001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String invoiceNumber;

    @NotNull(message = "Sales order is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    @Schema(description = "Sales order this invoice is billing for", requiredMode = Schema.RequiredMode.REQUIRED)
    private SalesOrder salesOrder;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @Schema(description = "Customer being billed", requiredMode = Schema.RequiredMode.REQUIRED)
    private Customer customer;

    @NotNull(message = "Invoice date is required")
    @Column(name = "invoice_date", nullable = false)
    @Schema(description = "Date the invoice was issued/generated", example = "2026-01-31", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate invoiceDate;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    @Schema(description = "Date by which payment is due", example = "2026-02-28", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dueDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    @Schema(description = "Subtotal amount before tax and discounts", example = "1299.99", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal subtotal;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    @Schema(description = "Tax amount calculated on subtotal", example = "104.00", defaultValue = "0.00", minimum = "0")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    @Schema(description = "Discount amount applied to invoice", example = "50.00", defaultValue = "0.00", minimum = "0")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    @Schema(description = "Total invoice amount (subtotal + tax - discount)", example = "1353.99", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal totalAmount;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    @Schema(description = "Amount already paid by customer", example = "500.00", defaultValue = "0.00", minimum = "0")
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "balance_due", nullable = false, precision = 12, scale = 2)
    @Schema(description = "Remaining balance due (total amount - paid amount)", example = "853.99", minimum = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal balanceDue;

    @NotNull(message = "Invoice status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_status", length = 20, nullable = false)
    @Builder.Default
    @Schema(description = "Current status of the invoice", example = "DRAFT", defaultValue = "DRAFT", allowableValues = {
            "DRAFT", "SENT", "PAID", "PARTIAL", "OVERDUE", "CANCELLED" })
    private InvoiceStatus invoiceStatus = InvoiceStatus.DRAFT;

    @Size(max = 100)
    @Column(name = "payment_terms")
    @Schema(description = "Payment terms specified on this invoice", example = "Net 30")
    private String paymentTerms;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Additional notes to include on invoice", example = "Thank you for your business! Payment can be made via check or bank transfer.")
    private String notes;

    @Size(max = 500)
    @Column(name = "file_url")
    @Schema(description = "URL to generated PDF invoice file in cloud storage", example = "https://s3.amazonaws.com/invoices/INV-20260131-0001.pdf")
    private String fileUrl;

    @NotNull(message = "Generator is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by", nullable = false)
    @Schema(description = "User who generated/created this invoice", requiredMode = Schema.RequiredMode.REQUIRED)
    private User generatedBy;

    // Helper method
    public void calculateBalanceDue() {
        this.balanceDue = this.totalAmount.subtract(this.paidAmount);
    }

    @PrePersist
    @PreUpdate
    private void calculateBalanceBeforeSave() {
        calculateBalanceDue();
    }
}