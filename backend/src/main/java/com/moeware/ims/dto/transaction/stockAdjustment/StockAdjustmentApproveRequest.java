package com.moeware.ims.dto.transaction.stockAdjustment;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request body for approving a pending stock adjustment. " +
        "On approval the inventory quantity is updated and a movement record is created.")
public class StockAdjustmentApproveRequest {

    @Schema(description = "Optional notes from the approver — appended to the adjustment record", example = "Damage verified via inspection report #INS-2026-042")
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}