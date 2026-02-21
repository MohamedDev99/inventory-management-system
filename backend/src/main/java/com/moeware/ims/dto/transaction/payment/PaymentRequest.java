package com.moeware.ims.dto.transaction.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.moeware.ims.enums.transaction.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for recording a payment")
public class PaymentRequest {

    @Schema(description = "Related sales order ID (null for advance payment or account credit)", example = "456")
    private Long salesOrderId;

    @NotNull(message = "Customer ID is required")
    @Schema(description = "ID of the customer making the payment", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long customerId;

    @NotNull(message = "Payment date is required")
    @Schema(description = "Date when the payment was received/processed", example = "2026-01-31", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate paymentDate;

    @NotNull(message = "Payment method is required")
    @Schema(description = "Method used for payment", example = "CREDIT_CARD", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaymentMethod paymentMethod;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2)
    @Schema(description = "Payment amount", example = "1418.99", minimum = "0.01", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Size(min = 3, max = 3)
    @Builder.Default
    @Schema(description = "Currency code (ISO 4217)", example = "USD", defaultValue = "USD")
    private String currency = "USD";

    @Size(max = 100)
    @Schema(description = "External reference number (check number, transaction ID, etc.)", example = "CHK-98765")
    private String referenceNumber;

    @Schema(description = "Additional payment notes")
    private String notes;
}