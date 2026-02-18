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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response for stock adjustment operation")
public class StockAdjustmentResponse {

    @Schema(description = "Adjustment ID", example = "45")
    private Long id;

    @Schema(description = "Product ID", example = "10")
    private Long productId;

    @Schema(description = "Warehouse ID", example = "1")
    private Long warehouseId;

    @Schema(description = "Quantity before adjustment", example = "45")
    private Integer quantityBefore;

    @Schema(description = "Quantity after adjustment", example = "43")
    private Integer quantityAfter;

    @Schema(description = "Quantity change", example = "-2")
    private Integer quantityChange;

    @Schema(description = "Adjustment type", example = "REMOVE")
    private AdjustmentType adjustmentType;

    @Schema(description = "Reason for adjustment", example = "DAMAGED")
    private AdjustmentReason reason;

    @Schema(description = "Adjustment status", example = "PENDING")
    private StockAdjustmentStatus status;

    @Schema(description = "User who performed the adjustment")
    private UserSummaryDTO performedBy;

    @Schema(description = "User who approved/rejected the adjustment")
    private UserSummaryDTO approvedBy;

    @Schema(description = "Additional notes", example = "Water damage during inspection")
    private String notes;

    @Schema(description = "Creation timestamp", example = "2026-02-09T10:50:00")
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "User summary information")
    public static class UserSummaryDTO {
        @Schema(description = "User ID", example = "5")
        private Long id;

        @Schema(description = "Username", example = "warehouse_staff")
        private String username;
    }
}