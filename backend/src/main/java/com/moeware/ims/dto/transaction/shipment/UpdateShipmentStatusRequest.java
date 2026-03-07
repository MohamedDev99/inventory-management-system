package com.moeware.ims.dto.transaction.shipment;

import com.moeware.ims.enums.transaction.ShipmentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating shipment status")
public class UpdateShipmentStatusRequest {

    @NotNull(message = "Shipment status is required")
    @Schema(description = "New shipment status", example = "IN_TRANSIT", requiredMode = Schema.RequiredMode.REQUIRED)
    private ShipmentStatus status;

    @Schema(description = "Optional notes about the status change", example = "Package picked up by FedEx driver")
    private String notes;
}