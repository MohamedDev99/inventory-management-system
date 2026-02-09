package com.moeware.ims.entity.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;

import com.moeware.ims.entity.VersionedEntity;
import com.moeware.ims.entity.product.Product;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items", uniqueConstraints = {
        @UniqueConstraint(name = "uk_inventory_product_warehouse", columnNames = { "product_id", "warehouse_id" })
}, indexes = {
        @Index(name = "idx_inventory_product", columnList = "product_id"),
        @Index(name = "idx_inventory_warehouse", columnList = "warehouse_id"),
        @Index(name = "idx_inventory_warehouse_quantity", columnList = "warehouse_id, quantity")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Current stock level for a specific product at a specific warehouse location")
public class InventoryItem extends VersionedEntity {

    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Product being tracked", implementation = Product.class, required = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Product is required")
    private Product product;

    @Schema(description = "Warehouse where product is stored", implementation = Warehouse.class, required = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @NotNull(message = "Warehouse is required")
    private Warehouse warehouse;

    @Schema(description = "Current stock quantity", example = "50", required = true, minimum = "0")
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Schema(description = "Physical location code within warehouse", example = "A-12-3", maxLength = 50)
    @Size(max = 50, message = "Location code must not exceed 50 characters")
    @Column(length = 50)
    private String locationCode; // e.g., "A-12-3" (Aisle-Rack-Shelf)

    @Schema(description = "Last physical stock count timestamp", example = "2026-01-20T14:30:00")
    @Column
    private LocalDateTime lastStockCheck;

    // Helper methods
    public void addStock(Integer amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot add negative stock");
        }
        this.quantity += amount;
    }

    public void removeStock(Integer amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot remove negative stock");
        }
        if (this.quantity < amount) {
            throw new IllegalStateException(
                    "Insufficient stock. Available: " + this.quantity + ", Requested: " + amount);
        }
        this.quantity -= amount;
    }

    public boolean isLowStock() {
        return product != null && product.isLowStock(this.quantity);
    }

    public boolean isBelowMinimum() {
        return product != null && product.isBelowMinimum(this.quantity);
    }

    public String getStockStatus() {
        if (isBelowMinimum()) {
            return "CRITICAL";
        } else if (isLowStock()) {
            return "LOW";
        } else {
            return "NORMAL";
        }
    }
}