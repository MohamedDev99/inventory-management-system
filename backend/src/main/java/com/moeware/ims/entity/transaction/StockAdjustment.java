package com.moeware.ims.entity.transaction;

import com.moeware.ims.entity.VersionedEntity;
import com.moeware.ims.entity.inventory.Product;
import com.moeware.ims.entity.User;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.enums.AdjustmentReason;
import com.moeware.ims.enums.AdjustmentType;
import com.moeware.ims.enums.StockAdjustmentStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Stock Adjustment entity
 * Represents manual adjustments to inventory levels with approval workflow
 */
@Entity
@Table(name = "stock_adjustments", indexes = {
                @Index(name = "idx_sa_product", columnList = "product_id"),
                @Index(name = "idx_sa_warehouse", columnList = "warehouse_id"),
                @Index(name = "idx_sa_status", columnList = "status"),
                @Index(name = "idx_sa_adjustment_date", columnList = "adjustment_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Manual adjustment to inventory levels requiring approval")
public class StockAdjustment extends VersionedEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Schema(description = "Unique identifier for the stock adjustment", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        private Long id;

        @NotNull(message = "Product is required")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "product_id", nullable = false)
        @Schema(description = "Product whose inventory is being adjusted", requiredMode = Schema.RequiredMode.REQUIRED)
        private Product product;

        @NotNull(message = "Warehouse is required")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "warehouse_id", nullable = false)
        @Schema(description = "Warehouse where the adjustment is being made", requiredMode = Schema.RequiredMode.REQUIRED)
        private Warehouse warehouse;

        @NotNull(message = "Quantity before is required")
        @Min(value = 0, message = "Quantity before must be at least 0")
        @Column(name = "quantity_before", nullable = false)
        @Schema(description = "Inventory quantity before the adjustment", example = "100", requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
        private Integer quantityBefore;

        @NotNull(message = "Quantity after is required")
        @Min(value = 0, message = "Quantity after must be at least 0")
        @Column(name = "quantity_after", nullable = false)
        @Schema(description = "Inventory quantity after the adjustment", example = "98", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer quantityAfter;

        // @Min(value = 0, message = "Quantity change must be at least 0")
        @NotNull(message = "Quantity change is required")
        @Column(name = "quantity_change", nullable = false)
        @Schema(description = "Change in quantity (positive for additions, negative for removals)", example = "-2", accessMode = Schema.AccessMode.READ_ONLY)
        private Integer quantityChange;

        @NotNull(message = "Adjustment type is required")
        @Enumerated(EnumType.STRING)
        @Column(name = "adjustment_type", length = 20, nullable = false)
        @Schema(description = "Type of adjustment operation", example = "REMOVE", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
                        "ADD", "REMOVE", "CORRECTION" })
        private AdjustmentType adjustmentType;

        @NotNull(message = "Reason is required")
        @Enumerated(EnumType.STRING)
        @Column(length = 20, nullable = false)
        @Schema(description = "Standardized reason for the adjustment", example = "DAMAGED", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
                        "DAMAGED", "EXPIRED", "THEFT", "COUNT_ERROR", "RETURN", "OTHER" })
        private AdjustmentReason reason;

        @NotNull(message = "Performer is required")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "performed_by", nullable = false)
        @Schema(description = "User who created this adjustment request", requiredMode = Schema.RequiredMode.REQUIRED)
        private User performedBy;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "approved_by")
        @Schema(description = "Manager/Admin who approved or rejected this adjustment", accessMode = Schema.AccessMode.READ_ONLY)
        private User approvedBy;

        @NotNull(message = "Status is required")
        @Enumerated(EnumType.STRING)
        @Column(length = 20, nullable = false)
        @Schema(description = "Approval status of the adjustment", example = "PENDING", allowableValues = { "PENDING",
                        "APPROVED", "REJECTED" }, defaultValue = "PENDING")
        private StockAdjustmentStatus status = StockAdjustmentStatus.PENDING; // PENDING, APPROVED, REJECTED

        @Column(columnDefinition = "TEXT")
        @Schema(description = "Additional notes explaining the adjustment in detail", example = "Items found damaged during quality inspection. Box was wet, products are unusable.")
        private String notes;

        @NotNull(message = "Adjustment date is required")
        @Column(name = "adjustment_date", nullable = false)
        @Schema(description = "Date and time when the adjustment was created", example = "2026-01-31T14:20:00", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime adjustmentDate;

        @PrePersist
        @PreUpdate
        private void prePersistOrUpdate() {
                if (adjustmentDate == null) {
                        adjustmentDate = LocalDateTime.now();
                }
                // Calculate quantity change
                quantityChange = quantityAfter - quantityBefore;
        }
}