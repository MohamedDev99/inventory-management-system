package com.moeware.ims.dto.transaction.shipment;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to receive shipment from purchase order")
public class ReceiveShipmentRequest {

    @Schema(description = "Purchase order ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Purchase order ID is required")
    private Long purchaseOrderId;

    @Schema(description = "Warehouse ID where items will be received", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @Schema(description = "Items being received", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<ReceiveShipmentItem> items;

    @Schema(description = "User ID receiving the shipment", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Received by user ID is required")
    private Long receivedBy;

    @Schema(description = "Date and time when shipment was received", example = "2026-02-09T11:00:00")
    private LocalDateTime receivedDate;

    @Schema(description = "Additional notes", example = "All items in good condition")
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Individual item in shipment")
    public static class ReceiveShipmentItem {
        @Schema(description = "Product ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Product ID is required")
        private Long productId;

        @Schema(description = "Quantity received", example = "50", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
        @NotNull(message = "Quantity received is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantityReceived;

        @Schema(description = "Storage location code", example = "A-12-3")
        @Size(max = 50, message = "Location code must not exceed 50 characters")
        private String locationCode;
    }
}