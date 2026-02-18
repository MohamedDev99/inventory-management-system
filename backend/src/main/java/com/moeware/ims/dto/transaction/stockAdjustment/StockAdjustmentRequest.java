package com.moeware.ims.dto.transaction.stockAdjustment;

import com.moeware.ims.enums.transaction.AdjustmentReason;
import com.moeware.ims.enums.transaction.AdjustmentType;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request to create a stock adjustment")
public class StockAdjustmentRequest {

        @Schema(description = "Product ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Product ID is required")
        private Long productId;

        @Schema(description = "Warehouse ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Warehouse ID is required")
        private Long warehouseId;

        @Schema(description = "Quantity change (positive for additions, negative for removals)", example = "-2", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Quantity change is required")
        private Integer quantityChange;

        @Schema(description = "Adjustment type", example = "REMOVE", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
                        "ADD", "REMOVE",
                        "CORRECTION" })
        @NotNull(message = "Adjustment type is required")
        private AdjustmentType adjustmentType;

        @Schema(description = "Reason for adjustment", example = "DAMAGED", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
                        "DAMAGED",
                        "EXPIRED", "THEFT", "COUNT_ERROR", "RETURN", "OTHER" })
        @NotNull(message = "Reason is required")
        private AdjustmentReason reason;

        @Schema(description = "Additional notes", example = "Water damage during inspection")
        @Size(max = 1000, message = "Notes must not exceed 1000 characters")
        private String notes;

        @Schema(description = "User ID performing the adjustment", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Performed by user ID is required")
        private Long performedBy;
}