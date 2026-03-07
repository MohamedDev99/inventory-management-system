package com.moeware.ims.dto.transaction.shipment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.moeware.ims.enums.transaction.ShipmentStatus;
import com.moeware.ims.enums.transaction.ShippingMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Full shipment details response")
public class ShipmentResponse {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Unique shipment number", example = "SHIP-20260131-0001")
    private String shipmentNumber;

    @Schema(description = "Related sales order summary")
    private SalesOrderSummary salesOrder;

    @Schema(description = "Origin warehouse summary")
    private WarehouseSummary shippedFromWarehouse;

    @Schema(description = "User who processed the shipment")
    private UserSummary shippedBy;

    @Schema(description = "Shipping carrier company name", example = "FedEx")
    private String carrier;

    @Schema(description = "Carrier-provided tracking number", example = "1Z999AA10123456784")
    private String trackingNumber;

    @Schema(description = "Shipping service level", example = "EXPRESS")
    private ShippingMethod shippingMethod;

    @Schema(description = "Estimated delivery date", example = "2026-02-05")
    private LocalDate estimatedDeliveryDate;

    @Schema(description = "Actual delivery date", example = "2026-02-04")
    private LocalDate actualDeliveryDate;

    @Schema(description = "Shipping cost", example = "15.99")
    private BigDecimal shippingCost;

    @Schema(description = "Package weight in kilograms", example = "2.5")
    private BigDecimal weight;

    @Schema(description = "Package dimensions (LxWxH cm)", example = "30x20x15")
    private String dimensions;

    @Schema(description = "Current shipment status", example = "IN_TRANSIT")
    private ShipmentStatus status;

    @Schema(description = "Additional notes")
    private String notes;

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
        private String customerName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseSummary {
        private Long id;
        private String name;
        private String code;
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