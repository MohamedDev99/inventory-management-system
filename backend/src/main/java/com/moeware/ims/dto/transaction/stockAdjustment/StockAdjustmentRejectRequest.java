package com.moeware.ims.dto.transaction.stockAdjustment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for PATCH /api/stock-adjustments/{id}/reject.
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for rejecting a pending stock adjustment. " +
        "No inventory change is made. The rejection reason is recorded in the adjustment notes.")
public class StockAdjustmentRejectRequest {

    @Schema(description = "Reason for rejection — required so the submitting staff member can act on the feedback", example = "Insufficient documentation provided. Please attach the inspection report.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Rejection reason is required")
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;
}