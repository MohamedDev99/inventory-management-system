package com.moeware.ims.dto.transaction.stockAdjustment;

import java.time.LocalDateTime;

import com.moeware.ims.enums.transaction.AdjustmentReason;
import com.moeware.ims.enums.transaction.AdjustmentType;
import com.moeware.ims.enums.transaction.StockAdjustmentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for a stock adjustment record.
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Stock adjustment record — includes before/after quantities and current approval status")
public class StockAdjustmentResponse {

    @Schema(description = "Unique identifier of the adjustment record", example = "45", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID of the adjusted product", example = "10")
    private Long productId;

    @Schema(description = "ID of the warehouse where the adjustment applies", example = "1")
    private Long warehouseId;

    @Schema(description = "Inventory quantity before the adjustment was applied", example = "45", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer quantityBefore;

    @Schema(description = "Inventory quantity after the adjustment is applied (quantityBefore + quantityChange)", example = "43", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer quantityAfter;

    @Schema(description = "Net quantity change — positive for additions, negative for removals", example = "-2", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer quantityChange;

    @Schema(description = "Type of adjustment operation", example = "REMOVE", allowableValues = { "ADD", "REMOVE",
            "CORRECTION" })
    private AdjustmentType adjustmentType;

    @Schema(description = "Reason for the adjustment", example = "DAMAGED", allowableValues = { "DAMAGED", "EXPIRED",
            "THEFT", "COUNT_ERROR", "RETURN", "OTHER" })
    private AdjustmentReason reason;

    @Schema(description = "Current approval status of the adjustment", example = "PENDING", allowableValues = {
            "PENDING", "APPROVED", "REJECTED" }, accessMode = Schema.AccessMode.READ_ONLY)
    private StockAdjustmentStatus status;

    @Schema(description = "User who submitted the adjustment request")
    private UserSummaryDTO performedBy;

    @Schema(description = "Manager or admin who approved or rejected the adjustment — null while PENDING", accessMode = Schema.AccessMode.READ_ONLY)
    private UserSummaryDTO approvedBy;

    @Schema(description = "Additional notes. Rejection reason is appended here when status becomes REJECTED.", example = "Water damage during inspection")
    private String notes;

    @Schema(description = "Timestamp when the adjustment record was created", example = "2026-02-09T10:50:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    // ---- Nested summary ----

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Condensed user information included in an adjustment record")
    public static class UserSummaryDTO {

        @Schema(description = "User ID", example = "5")
        private Long id;

        @Schema(description = "Username", example = "warehouse_staff")
        private String username;
    }
}