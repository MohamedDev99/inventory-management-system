package com.moeware.ims.dto.transaction.shipment;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for marking a shipment as delivered")
public class DeliverShipmentRequest {

    @NotNull(message = "Actual delivery date is required")
    @Schema(description = "Actual date when the shipment was delivered", example = "2026-02-04", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate actualDeliveryDate;

    @Schema(description = "Delivery notes (e.g., left at front door)", example = "Left at front door per customer instructions")
    private String notes;
}