package com.moeware.ims.dto.transaction.invoice;

import java.math.BigDecimal;

import com.moeware.ims.enums.transaction.InvoiceStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating invoice status")
public class UpdateInvoiceStatusRequest {

    @NotNull(message = "Invoice status is required")
    @Schema(description = "New invoice status", example = "PAID", requiredMode = Schema.RequiredMode.REQUIRED)
    private InvoiceStatus invoiceStatus;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Schema(description = "Amount paid (required when status is PAID or PARTIAL)", example = "2807.97")
    private BigDecimal paidAmount;

    @Schema(description = "Optional notes about the status change")
    private String notes;
}