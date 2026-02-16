package com.moeware.ims.dto.inventory.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for creating a new product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for creating a new product")
public class ProductCreateRequest {

    @Schema(description = "Stock Keeping Unit - unique product identifier", example = "LAP-001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    @Schema(description = "Product name", example = "Dell Laptop XPS 15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;

    @Schema(description = "Detailed product description", example = "15.6 inch laptop with Intel i7 processor and 16GB RAM")
    private String description;

    @Schema(description = "Category ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Category is required")
    private Long categoryId;

    @Schema(description = "Unit of measure", example = "PIECE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Unit is required")
    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit;

    @Schema(description = "Selling price per unit", example = "1299.99", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;

    @Schema(description = "Cost/purchase price per unit", example = "899.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.01", message = "Cost price must be greater than 0")
    private BigDecimal costPrice;

    @Schema(description = "Stock level threshold for reorder alerts", example = "10")
    @Min(value = 0, message = "Reorder level must be non-negative")
    @Builder.Default
    private Integer reorderLevel = 10;

    @Schema(description = "Minimum stock level before critical alert", example = "5")
    @Min(value = 0, message = "Minimum stock level must be non-negative")
    @Builder.Default
    private Integer minStockLevel = 5;

    @Schema(description = "Product barcode (EAN, UPC, etc.)", example = "1234567890123")
    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    private String barcode;

    @Schema(description = "URL to product image", example = "https://example.com/images/product.jpg")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @Schema(description = "Whether the product is active in the catalog", example = "true")
    @Builder.Default
    private Boolean isActive = true;
}