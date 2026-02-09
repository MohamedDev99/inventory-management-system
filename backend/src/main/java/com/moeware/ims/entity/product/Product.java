package com.moeware.ims.entity.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "Product catalog item with pricing, inventory settings, and category classification")
public class Product extends VersionedEntity {

    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Stock Keeping Unit - unique product identifier", example = "LAP-001", required = true, maxLength = 100)
    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Schema(description = "Product name", example = "Dell Laptop XPS 15", required = true, maxLength = 255)
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String name;

    @Schema(description = "Detailed product description", example = "15.6 inch laptop with Intel i7 processor and 16GB RAM")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Schema(description = "Product category", implementation = Category.class, required = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    private Category category;

    @Schema(description = "Unit of measure", example = "PIECE", required = true, maxLength = 20, allowableValues = {
            "PIECE", "KG", "LITER", "BOX", "PACK" })
    @NotBlank(message = "Unit is required")
    @Size(max = 20, message = "Unit must not exceed 20 characters")
    @Column(nullable = false, length = 20)
    private String unit; // PIECE, KG, LITER, etc.

    @Schema(description = "Selling price per unit", example = "1299.99", required = true, minimum = "0.01")
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Schema(description = "Cost/purchase price per unit", example = "899.00", required = true, minimum = "0.01")
    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost price must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Schema(description = "Stock level threshold for reorder alerts", example = "10", required = true, minimum = "0")
    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level must be non-negative")
    @Column(nullable = false)
    @Builder.Default
    private Integer reorderLevel = 10;

    @Schema(description = "Minimum stock level before critical alert", example = "5", required = true, minimum = "0")
    @NotNull(message = "Minimum stock level is required")
    @Min(value = 0, message = "Minimum stock level must be non-negative")
    @Column(nullable = false)
    @Builder.Default
    private Integer minStockLevel = 5;

    @Schema(description = "Product barcode (EAN, UPC, etc.)", example = "1234567890123", maxLength = 50)
    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    @Column(unique = true, length = 50)
    private String barcode;

    @Schema(description = "URL to product image", example = "https://example.com/images/product.jpg", maxLength = 500)
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @Column(length = 500)
    private String imageUrl;

    @Schema(description = "Whether the product is active in the catalog", example = "true", required = true)
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Schema(description = "Inventory items for this product across warehouses", accessMode = Schema.AccessMode.READ_ONLY)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<InventoryItem> inventoryItems = new HashSet<>();

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