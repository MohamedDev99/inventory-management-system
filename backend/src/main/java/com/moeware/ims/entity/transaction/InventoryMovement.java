package com.moeware.ims.entity.transaction;

import java.time.LocalDateTime;

import com.moeware.ims.entity.User;
import com.moeware.ims.entity.VersionedEntity;
import com.moeware.ims.entity.inventory.Product;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.enums.transaction.MovementType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inventory Movement entity
 * Tracks all inventory movements including transfers, receipts, shipments, and
 * adjustments
 */
@Entity
@Table(name = "inventory_movements", indexes = {
        @Index(name = "idx_im_product", columnList = "product_id"),
        @Index(name = "idx_im_from_warehouse", columnList = "from_warehouse_id"),
        @Index(name = "idx_im_to_warehouse", columnList = "to_warehouse_id"),
        @Index(name = "idx_im_movement_date", columnList = "movement_date"),
        @Index(name = "idx_im_movement_type", columnList = "movement_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Record of inventory movement between locations or during transactions")
public class InventoryMovement extends VersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the inventory movement", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @Schema(description = "Product being moved", requiredMode = Schema.RequiredMode.REQUIRED)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_warehouse_id")
    @Schema(description = "Source warehouse (null for receipts from suppliers)")
    private Warehouse fromWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_warehouse_id")
    @Schema(description = "Destination warehouse (null for shipments to customers)")
    private Warehouse toWarehouse;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    @Schema(description = "Quantity of product moved", example = "50", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @NotNull(message = "Movement type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", length = 20, nullable = false)
    @Schema(description = "Type of inventory movement", example = "TRANSFER", allowableValues = { "TRANSFER", "RECEIPT",
            "SHIPMENT", "ADJUSTMENT" }, requiredMode = Schema.RequiredMode.REQUIRED)
    private MovementType movementType;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Reason or explanation for the movement", example = "Warehouse consolidation - moving slow-moving items to main warehouse")
    private String reason;

    @Size(max = 50)
    @Column(name = "reference_number")
    @Schema(description = "Reference to related document (PO number, SO number, etc.)", example = "PO-20260131-0001")
    private String referenceNumber;

    @NotNull(message = "Performer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by", nullable = false)
    @Schema(description = "User who performed or authorized the movement", requiredMode = Schema.RequiredMode.REQUIRED)
    private User performedBy;

    @NotNull(message = "Movement date is required")
    @Column(name = "movement_date", nullable = false)
    @Schema(description = "Date and time when the movement occurred", example = "2026-01-31T10:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime movementDate;

    @PrePersist
    private void prePersist() {
        if (movementDate == null) {
            movementDate = LocalDateTime.now();
        }
        // Validate that at least one warehouse is set
        if (fromWarehouse == null && toWarehouse == null) {
            throw new IllegalStateException("At least one warehouse (from or to) must be specified");
        }
    }
}