package com.moeware.ims.dto.transaction.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a purchase order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for creating or updating a purchase order")
public class PurchaseOrderRequest {

    @NotNull(message = "Supplier ID is required")
    @Schema(description = "ID of the supplier", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long supplierId;

    @NotNull(message = "Warehouse ID is required")
    @Schema(description = "ID of the destination warehouse", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long warehouseId;

    @NotNull(message = "Order date is required")
    @Schema(description = "Date when the order is placed", example = "2026-02-20", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate orderDate;

    @Schema(description = "Expected delivery date from the supplier", example = "2026-03-05")
    private LocalDate expectedDeliveryDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount must be non-negative")
    @Digits(integer = 10, fraction = 2)
    @Schema(description = "Tax amount applied to the order", example = "85.00", defaultValue = "0.00")
    private BigDecimal taxAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount must be non-negative")
    @Digits(integer = 10, fraction = 2)
    @Schema(description = "Discount amount applied to the order", example = "50.00", defaultValue = "0.00")
    private BigDecimal discountAmount;

    @Schema(description = "Additional notes or instructions for the purchase order", example = "Please deliver to loading dock B")
    private String notes;

    @NotEmpty(message = "Purchase order must have at least one line item")
    @Valid
    @Schema(description = "List of line items in the purchase order", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PurchaseOrderItemRequest> items;
}