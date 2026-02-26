package com.moeware.ims.dto.transaction.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for a purchase order line item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Purchase order line item details")
public class PurchaseOrderItemResponse {

    @Schema(description = "Line item ID", example = "1")
    private Long id;

    @Schema(description = "Product ID", example = "10")
    private Long productId;

    @Schema(description = "Product SKU", example = "LAP-001")
    private String productSku;

    @Schema(description = "Product name", example = "Dell Laptop XPS 15")
    private String productName;

    @Schema(description = "Quantity ordered", example = "10")
    private Integer quantityOrdered;

    @Schema(description = "Quantity received so far", example = "0")
    private Integer quantityReceived;

    @Schema(description = "Unit price at time of order", example = "899.99")
    private BigDecimal unitPrice;

    @Schema(description = "Line total (quantity × unit price)", example = "8999.90")
    private BigDecimal lineTotal;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}