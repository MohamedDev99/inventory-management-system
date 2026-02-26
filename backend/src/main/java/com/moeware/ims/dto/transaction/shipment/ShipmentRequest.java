package com.moeware.ims.dto.transaction.shipment;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.moeware.ims.enums.transaction.ShippingMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Request payload for creating a shipment")
public class ShipmentRequest {

    @NotNull(message = "Sales order ID is required")
    @Schema(description = "ID of the sales order being shipped", example = "456", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long salesOrderId;

    @NotNull(message = "Origin warehouse ID is required")
    @Schema(description = "ID of the warehouse from which the shipment originates", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long warehouseId;

    @NotBlank(message = "Carrier is required")
    @Size(max = 100)
    @Schema(description = "Shipping carrier company name", example = "FedEx", requiredMode = Schema.RequiredMode.REQUIRED)
    private String carrier;

    @Size(max = 100)
    @Schema(description = "Carrier-provided tracking number", example = "1Z999AA10123456784")
    private String trackingNumber;

    @NotNull(message = "Shipping method is required")
    @Schema(description = "Shipping service level", example = "EXPRESS", requiredMode = Schema.RequiredMode.REQUIRED)
    private ShippingMethod shippingMethod;

    @Schema(description = "Estimated delivery date", example = "2026-02-05")
    private LocalDate estimatedDeliveryDate;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Builder.Default
    @Schema(description = "Shipping cost", example = "15.99", defaultValue = "0.00")
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 8, fraction = 2)
    @Schema(description = "Package weight in kilograms", example = "2.5")
    private BigDecimal weight;

    @Size(max = 50)
    @Schema(description = "Package dimensions (LxWxH cm)", example = "30x20x15")
    private String dimensions;

    @Schema(description = "Additional notes or special handling instructions")
    private String notes;
}