package com.moeware.ims.dto.transaction.stockAdjustment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for PATCH /api/stock-adjustments/{id}/approve.
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to approve a pending stock adjustment")
public class StockAdjustmentApproveRequest {

    @Schema(description = "ID of the manager/admin approving the adjustment", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Approved by user ID is required")
    private Long approvedBy;

    @Schema(description = "Optional approval notes", example = "Damage verified via inspection report #INS-2026-042")
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}