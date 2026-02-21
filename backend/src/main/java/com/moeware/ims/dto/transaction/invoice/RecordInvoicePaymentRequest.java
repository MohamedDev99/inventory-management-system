package com.moeware.ims.dto.transaction.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.moeware.ims.enums.transaction.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for recording a payment against an invoice")
public class RecordInvoicePaymentRequest {

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    @Digits(integer = 10, fraction = 2)
    @Schema(description = "Amount being paid against this invoice", example = "2807.97", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal paymentAmount;

    @NotNull(message = "Payment date is required")
    @Schema(description = "Date when the payment was received", example = "2026-02-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate paymentDate;

    @NotNull(message = "Payment method is required")
    @Schema(description = "Method used for payment", example = "BANK_TRANSFER", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaymentMethod paymentMethod;

    @Size(max = 100)
    @Schema(description = "External reference number for this payment", example = "TXN-7890123")
    private String referenceNumber;
}