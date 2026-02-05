package com.moeware.ims.entity.transaction;

import com.moeware.ims.entity.BaseEntity;
import com.moeware.ims.entity.Product;
import com.moeware.ims.entity.User;
import com.moeware.ims.entity.Warehouse;
import com.moeware.ims.enums.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

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
public class InventoryMovement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_warehouse_id")
    private Warehouse fromWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_warehouse_id")
    private Warehouse toWarehouse;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Movement type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", length = 20, nullable = false)
    private MovementType movementType;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Size(max = 50)
    @Column(name = "reference_number")
    private String referenceNumber;

    @NotNull(message = "Performer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @NotNull(message = "Movement date is required")
    @Column(name = "movement_date", nullable = false)
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