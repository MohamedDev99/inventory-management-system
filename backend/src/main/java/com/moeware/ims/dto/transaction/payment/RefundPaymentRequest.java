package com.moeware.ims.dto.transaction.payment;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for processing a payment refund")
public class RefundPaymentRequest {

    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    @Digits(integer = 10, fraction = 2)
    @Schema(description = "Amount to refund (must not exceed original payment amount)", example = "2822.97", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal refundAmount;

    @NotBlank(message = "Refund reason is required")
    @Schema(description = "Reason for the refund", example = "Customer returned order", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;

    @Schema(description = "Additional refund notes", example = "Full refund processed")
    private String notes;
}