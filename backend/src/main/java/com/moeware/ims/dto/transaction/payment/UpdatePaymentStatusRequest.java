package com.moeware.ims.dto.transaction.payment;

import com.moeware.ims.enums.transaction.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating payment status")
public class UpdatePaymentStatusRequest {

    @NotNull(message = "Payment status is required")
    @Schema(description = "New payment status", example = "COMPLETED", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaymentStatus paymentStatus;

    @Schema(description = "Optional notes about the status change", example = "Payment verified")
    private String notes;
}