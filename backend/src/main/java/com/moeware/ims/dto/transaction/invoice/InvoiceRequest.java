package com.moeware.ims.dto.transaction.invoice;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for generating a new invoice from a sales order")
public class InvoiceRequest {

    @NotNull(message = "Sales order ID is required")
    @Schema(description = "ID of the sales order to invoice", example = "456", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long salesOrderId;

    @NotNull(message = "Invoice date is required")
    @Schema(description = "Date the invoice is issued", example = "2026-02-09", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate invoiceDate;

    @NotNull(message = "Due date is required")
    @Schema(description = "Date by which payment is due", example = "2026-03-11", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dueDate;

    @Size(max = 100)
    @Schema(description = "Payment terms for this invoice", example = "Net 30")
    private String paymentTerms;

    @Schema(description = "Additional notes to include on the invoice", example = "Thank you for your business!")
    private String notes;
}