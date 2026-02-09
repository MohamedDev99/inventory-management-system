package com.moeware.ims.entity.transaction;

import com.moeware.ims.entity.VersionedEntity;
import com.moeware.ims.entity.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import javax.swing.SwingContainer;

/**
 * PurchaseOrderItem entity
 * Represents a line item in a purchase order
 */
@Entity
@Table(name = "purchase_order_items", indexes = {
        @Index(name = "idx_poi_purchase_order", columnList = "purchase_order_id"),
        @Index(name = "idx_poi_product", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Line item in a purchase order")
public class PurchaseOrderItem extends VersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the line item", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Purchase order is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    @Schema(description = "Parent purchase order", requiredMode = Schema.RequiredMode.REQUIRED)
    private PurchaseOrder purchaseOrder;

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @Schema(description = "Product being ordered", requiredMode = Schema.RequiredMode.REQUIRED)
    private Product product;

    @NotNull(message = "Quantity ordered is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity_ordered", nullable = false)
    @Schema(description = "Quantity of product ordered", example = "10", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantityOrdered;

    @NotNull
    @Min(value = 0, message = "Quantity received cannot be negative")
    @Column(name = "quantity_received", nullable = false)
    @Builder.Default
    @Schema(description = "Quantity of product actually received", example = "10", minimum = "0", defaultValue = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer quantityReceived = 0;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    @Schema(description = "Price per unit at time of order", example = "899.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unitPrice;

    @NotNull(message = "Line total is required")
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    @Schema(description = "Total for this line (quantity * unit price)", example = "8999.90", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal lineTotal;

    // Helper method
    public void calculateLineTotal() {
        this.lineTotal = this.unitPrice.multiply(new BigDecimal(this.quantityOrdered));
    }

    @PrePersist
    @PreUpdate
    private void calculateTotalBeforeSave() {
        calculateLineTotal();
    }
}