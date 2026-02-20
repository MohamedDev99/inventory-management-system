package com.moeware.ims.dto.transaction.salesOrder;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for a sales order line item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for a sales order line item")
public class SalesOrderItemRequest {

    @NotNull(message = "Product ID is required")
    @Schema(description = "ID of the product being ordered", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "Quantity of the product", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price must be non-negative")
    @Digits(integer = 10, fraction = 2)
    @Schema(description = "Selling price per unit", example = "1299.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unitPrice;
}