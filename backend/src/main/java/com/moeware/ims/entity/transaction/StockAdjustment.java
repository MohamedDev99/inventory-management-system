package com.moeware.ims.entity.transaction;

import com.moeware.ims.entity.BaseEntity;
import com.moeware.ims.entity.Product;
import com.moeware.ims.entity.User;
import com.moeware.ims.entity.Warehouse;
import com.moeware.ims.enums.AdjustmentReason;
import com.moeware.ims.enums.AdjustmentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

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
public class StockAdjustment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Warehouse is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @NotNull(message = "Quantity before is required")
    @Column(name = "quantity_before", nullable = false)
    private Integer quantityBefore;

    @NotNull(message = "Quantity after is required")
    @Column(name = "quantity_after", nullable = false)
    private Integer quantityAfter;

    @NotNull(message = "Quantity change is required")
    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;

    @NotNull(message = "Adjustment type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_type", length = 20, nullable = false)
    private AdjustmentType adjustmentType;

    @NotNull(message = "Reason is required")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AdjustmentReason reason;

    @NotNull(message = "Performer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @NotNull(message = "Status is required")
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(columnDefinition = "TEXT")
    private String notes;

    @NotNull(message = "Adjustment date is required")
    @Column(name = "adjustment_date", nullable = false)
    private LocalDateTime adjustmentDate;

    @PrePersist
    private void prePersist() {
        if (adjustmentDate == null) {
            adjustmentDate = LocalDateTime.now();
        }
        // Calculate quantity change
        quantityChange = quantityAfter - quantityBefore;
    }
}