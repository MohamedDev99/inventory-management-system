package com.moeware.ims.entity.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_sku", columnList = "sku", unique = true),
        @Index(name = "idx_products_barcode", columnList = "barcode"),
        @Index(name = "idx_products_category", columnList = "category_id"),
        @Index(name = "idx_products_active_category", columnList = "is_active, category_id"),
        @Index(name = "idx_products_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    private Category category;

    @NotBlank(message = "Unit is required")
    @Size(max = 20, message = "Unit must not exceed 20 characters")
    @Column(nullable = false, length = 20)
    private String unit; // PIECE, KG, LITER, etc.

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost price must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice;

    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level must be non-negative")
    @Column(nullable = false)
    @Builder.Default
    private Integer reorderLevel = 10;

    @NotNull(message = "Minimum stock level is required")
    @Min(value = 0, message = "Minimum stock level must be non-negative")
    @Column(nullable = false)
    @Builder.Default
    private Integer minStockLevel = 5;

    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    @Column(unique = true, length = 50)
    private String barcode;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<InventoryItem> inventoryItems = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Helper methods
    public BigDecimal getProfitMargin() {
        if (costPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return unitPrice.subtract(costPrice)
                .divide(costPrice, 2, RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.valueOf(100));
    }

    public boolean isLowStock(Integer currentStock) {
        return currentStock != null && currentStock <= reorderLevel;
    }

    public boolean isBelowMinimum(Integer currentStock) {
        return currentStock != null && currentStock <= minStockLevel;
    }
}