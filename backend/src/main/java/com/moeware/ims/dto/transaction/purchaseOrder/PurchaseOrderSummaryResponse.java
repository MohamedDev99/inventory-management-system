package com.moeware.ims.dto.transaction.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.moeware.ims.enums.transaction.PurchaseOrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Summary DTO for purchase orders used in paginated list responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Purchase order summary for list views")
public class PurchaseOrderSummaryResponse {

    @Schema(description = "Purchase order ID", example = "123")
    private Long id;

    @Schema(description = "Purchase order number", example = "PO-20260220-0001")
    private String poNumber;

    @Schema(description = "Supplier ID", example = "1")
    private Long supplierId;

    @Schema(description = "Supplier name", example = "Tech Supplies Inc")
    private String supplierName;

    @Schema(description = "Supplier code", example = "SUP001")
    private String supplierCode;

    @Schema(description = "Warehouse ID", example = "1")
    private Long warehouseId;

    @Schema(description = "Warehouse name", example = "Main Warehouse")
    private String warehouseName;

    @Schema(description = "Current status", example = "APPROVED")
    private PurchaseOrderStatus status;

    @Schema(description = "Order date", example = "2026-02-20")
    private LocalDate orderDate;

    @Schema(description = "Expected delivery date", example = "2026-03-05")
    private LocalDate expectedDeliveryDate;

    @Schema(description = "Actual delivery date", example = "2026-03-04")
    private LocalDate actualDeliveryDate;

    @Schema(description = "Number of line items", example = "3")
    private int itemCount;

    @Schema(description = "Subtotal before tax and discount", example = "8999.90")
    private BigDecimal subtotal;

    @Schema(description = "Tax amount", example = "720.00")
    private BigDecimal taxAmount;

    @Schema(description = "Discount amount", example = "50.00")
    private BigDecimal discountAmount;

    @Schema(description = "Total order amount", example = "9669.90")
    private BigDecimal totalAmount;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}