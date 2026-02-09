package com.moeware.ims.entity.transaction;

import com.moeware.ims.entity.VersionedEntity;
import com.moeware.ims.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Sales Order Item entity
 * Represents a line item in a sales order
 */
@Entity
@Table(name = "sales_order_items", indexes = {
        @Index(name = "idx_soi_sales_order", columnList = "sales_order_id"),
        @Index(name = "idx_soi_product", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Line item in a sales order representing one product")
public class SalesOrderItem extends VersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the line item", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Sales order is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    @Schema(description = "Parent sales order that contains this line item", requiredMode = Schema.RequiredMode.REQUIRED)
    private SalesOrder salesOrder;

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @Schema(description = "Product being sold in this line item", requiredMode = Schema.RequiredMode.REQUIRED)
    private Product product;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    @Schema(description = "Quantity of product ordered by customer", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    @Schema(description = "Selling price per unit at time of order (may differ from current product price)", example = "1299.99", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unitPrice;

    @NotNull(message = "Line total is required")
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    @Schema(description = "Total for this line (quantity × unit price)", example = "2599.98", minimum = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal lineTotal;

    // Helper method
    public void calculateLineTotal() {
        this.lineTotal = this.unitPrice.multiply(new BigDecimal(this.quantity));
    }

    @PrePersist
    @PreUpdate
    private void calculateTotalBeforeSave() {
        calculateLineTotal();
    }
}