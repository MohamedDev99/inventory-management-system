package com.moeware.ims.dto.transaction.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for a sales order line item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Sales order line item details")
public class SalesOrderItemResponse {

    @Schema(description = "Line item ID", example = "1")
    private Long id;

    @Schema(description = "Product ID", example = "10")
    private Long productId;

    @Schema(description = "Product SKU", example = "LAP-001")
    private String productSku;

    @Schema(description = "Product name", example = "Dell Laptop XPS 15")
    private String productName;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Unit selling price at time of order", example = "1299.99")
    private BigDecimal unitPrice;

    @Schema(description = "Line total (quantity × unit price)", example = "2599.98")
    private BigDecimal lineTotal;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}