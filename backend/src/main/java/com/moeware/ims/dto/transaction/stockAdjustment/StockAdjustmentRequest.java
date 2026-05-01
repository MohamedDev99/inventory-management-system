package com.moeware.ims.dto.transaction.stockAdjustment;

import com.moeware.ims.enums.transaction.AdjustmentReason;
import com.moeware.ims.enums.transaction.AdjustmentType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for POST /api/stock-adjustments.
 *
 * <p>
 * The adjustment is saved as PENDING and does NOT modify inventory until
 * a manager approves it via PATCH /{id}/approve.
 * </p>
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for creating a stock adjustment. " +
                "The adjustment is saved as PENDING and does not affect inventory until approved.")
public class StockAdjustmentRequest {

        @Schema(description = "ID of the product whose inventory is being adjusted", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Product ID is required")
        private Long productId;

        @Schema(description = "ID of the warehouse where the adjustment is being made", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Warehouse ID is required")
        private Long warehouseId;

        @Schema(description = "Quantity change — positive for additions (ADD), negative for removals (REMOVE). " +
                        "Must not be zero. Sign must be consistent with adjustmentType.", example = "-2", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Quantity change is required")
        private Integer quantityChange;

        @Schema(description = "Type of adjustment operation", example = "REMOVE", allowableValues = { "ADD", "REMOVE",
                        "CORRECTION" }, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Adjustment type is required")
        private AdjustmentType adjustmentType;

        @Schema(description = "Standardized reason for the adjustment", example = "DAMAGED", allowableValues = {
                        "DAMAGED", "EXPIRED", "THEFT", "COUNT_ERROR", "RETURN",
                        "OTHER" }, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Reason is required")
        private AdjustmentReason reason;

        @Schema(description = "Additional notes providing more detail about the adjustment", example = "Water damage found during routine warehouse inspection")
        @Size(max = 1000, message = "Notes must not exceed 1000 characters")
        private String notes;

        @Schema(description = "ID of the warehouse staff member submitting this adjustment request", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Performed by user ID is required")
        private Long performedBy;

        @AssertTrue(message = "Quantity change must not be zero")
        public boolean isQuantityChangeNonZero() {
                return quantityChange != null && quantityChange != 0;
        }

        @AssertTrue(message = "Quantity change sign must match adjustment type: ADD expects positive, REMOVE expects negative")
        public boolean isQuantitySignConsistent() {
                if (adjustmentType == null || quantityChange == null)
                        return true;
                return switch (adjustmentType) {
                        case ADD -> quantityChange > 0;
                        case REMOVE -> quantityChange < 0;
                        case CORRECTION -> true;
                };
        }
}