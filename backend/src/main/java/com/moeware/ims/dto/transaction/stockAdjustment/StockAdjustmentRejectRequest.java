package com.moeware.ims.dto.transaction.stockAdjustment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Request to reject a pending stock adjustment")
public class StockAdjustmentRejectRequest {

    @Schema(description = "ID of the manager/admin rejecting the adjustment", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Rejected by user ID is required")
    private Long rejectedBy;

    @Schema(description = "Reason for rejection — required so staff can act on feedback", example = "Insufficient documentation provided. Please attach inspection report.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Rejection reason is required")
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;
}