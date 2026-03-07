package com.moeware.ims.dto.transaction.purchaseOrder;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for marking a purchase order as received (with partial receipt
 * support)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for receiving a purchase order")
public class ReceivePurchaseOrderRequest {

    @NotNull(message = "Actual delivery date is required")
    @Schema(description = "Actual date the shipment was received", example = "2026-03-04", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate actualDeliveryDate;

    @NotEmpty(message = "At least one item receipt is required")
    @Valid
    @Schema(description = "Items received and their quantities", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ItemReceipt> items;

    @Schema(description = "Optional notes about the receipt", example = "All items received in good condition")
    private String notes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Individual item receipt entry")
    public static class ItemReceipt {

        @NotNull(message = "Item ID is required")
        @Schema(description = "ID of the purchase order item", example = "456", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long itemId;

        @NotNull(message = "Quantity received is required")
        @Min(value = 0, message = "Quantity received cannot be negative")
        @Schema(description = "Actual quantity received for this line item", example = "10", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer quantityReceived;
    }
}