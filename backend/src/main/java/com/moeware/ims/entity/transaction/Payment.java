package com.moeware.ims.entity.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.moeware.ims.entity.User;
import com.moeware.ims.entity.VersionedEntity;
import com.moeware.ims.entity.staff.Customer;
import com.moeware.ims.enums.transaction.PaymentMethod;
import com.moeware.ims.enums.transaction.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payment entity
 * Tracks payment transactions for sales orders
 */
@Entity
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(columnNames = "payment_number"), indexes = {
                @Index(name = "idx_payment_sales_order", columnList = "sales_order_id"),
                @Index(name = "idx_payment_customer", columnList = "customer_id"),
                @Index(name = "idx_payment_status", columnList = "payment_status"),
                @Index(name = "idx_payment_date", columnList = "payment_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payment transaction record for customer orders")
public class Payment extends VersionedEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Schema(description = "Unique identifier for the payment", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        private Long id;

        @NotBlank(message = "Payment number is required")
        @Size(max = 50)
        @Column(name = "payment_number", nullable = false, unique = true)
        @Schema(description = "Unique payment transaction number", example = "PAY-20260131-0001", requiredMode = Schema.RequiredMode.REQUIRED)
        private String paymentNumber;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "sales_order_id")
        @Schema(description = "Related sales order (null if payment is advance payment or account credit)")
        private SalesOrder salesOrder;

        @NotNull(message = "Customer is required")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "customer_id", nullable = false)
        @Schema(description = "Customer who made the payment", requiredMode = Schema.RequiredMode.REQUIRED)
        private Customer customer;

        @NotNull(message = "Payment date is required")
        @Column(name = "payment_date", nullable = false)
        @Schema(description = "Date when the payment was received/processed", example = "2026-01-31", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDate paymentDate;

        @NotNull(message = "Payment method is required")
        @Enumerated(EnumType.STRING)
        @Column(name = "payment_method", length = 50, nullable = false)
        @Schema(description = "Method used for payment", example = "CREDIT_CARD", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
                        "CASH", "CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER", "CHECK", "PAYPAL" })
        private PaymentMethod paymentMethod;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @Digits(integer = 10, fraction = 2)
        @Column(nullable = false, precision = 12, scale = 2)
        @Schema(description = "Payment amount received", example = "1418.99", minimum = "0.01", requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal amount;

        @NotNull
        @Size(min = 3, max = 3)
        @Column(length = 3, nullable = false)
        @Builder.Default
        @Schema(description = "Payment currency code (ISO 4217)", example = "USD", defaultValue = "USD")
        private String currency = "USD";

        @Size(max = 100)
        @Column(name = "reference_number")
        @Schema(description = "External reference number (check number, transaction ID, etc.)", example = "CHK-98765")
        private String referenceNumber;

        @NotNull(message = "Payment status is required")
        @Enumerated(EnumType.STRING)
        @Column(name = "payment_status", length = 20, nullable = false)
        @Builder.Default
        @Schema(description = "Current status of the payment transaction", example = "PENDING", defaultValue = "PENDING", allowableValues = {
                        "PENDING", "COMPLETED", "FAILED", "REFUNDED" })
        private PaymentStatus paymentStatus = PaymentStatus.PENDING;

        @Column(columnDefinition = "TEXT")
        @Schema(description = "Additional payment notes or special circumstances", example = "Partial payment - customer will pay remaining balance in 30 days")
        private String notes;

        @NotNull(message = "Processor is required")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "processed_by", nullable = false)
        @Schema(description = "User who processed/recorded the payment", requiredMode = Schema.RequiredMode.REQUIRED)
        private User processedBy;

}