package com.moeware.ims.dto.transaction.payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.moeware.ims.enums.transaction.PaymentMethod;
import com.moeware.ims.enums.transaction.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Full payment details response")
public class PaymentResponse {

    @Schema(description = "Unique identifier", example = "123")
    private Long id;

    @Schema(description = "Unique payment number", example = "PAY-20260131-0001")
    private String paymentNumber;

    @Schema(description = "Related sales order summary (may be null for advance payments)")
    private SalesOrderSummary salesOrder;

    @Schema(description = "Customer who made the payment")
    private CustomerSummary customer;

    @Schema(description = "Date payment was received", example = "2026-01-31")
    private LocalDate paymentDate;

    @Schema(description = "Payment method used", example = "CREDIT_CARD")
    private PaymentMethod paymentMethod;

    @Schema(description = "Payment amount", example = "1418.99")
    private BigDecimal amount;

    @Schema(description = "Currency code", example = "USD")
    private String currency;

    @Schema(description = "External reference number", example = "CHK-98765")
    private String referenceNumber;

    @Schema(description = "Current payment status", example = "COMPLETED")
    private PaymentStatus paymentStatus;

    @Schema(description = "Additional payment notes")
    private String notes;

    @Schema(description = "User who processed this payment")
    private UserSummary processedBy;

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
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerSummary {
        private Long id;
        private String customerCode;
        private String contactName;
        private String email;
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