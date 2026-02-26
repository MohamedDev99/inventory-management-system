package com.moeware.ims.dto.transaction.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.moeware.ims.enums.transaction.SalesOrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Summary DTO for sales orders used in paginated list responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Sales order summary for list views")
public class SalesOrderSummaryResponse {

    @Schema(description = "Sales order ID", example = "456")
    private Long id;

    @Schema(description = "Sales order number", example = "SO-20260220-0042")
    private String soNumber;

    @Schema(description = "Customer ID", example = "15")
    private Long customerId;

    @Schema(description = "Customer code", example = "CUST-015")
    private String customerCode;

    @Schema(description = "Customer name at time of order", example = "John Doe")
    private String customerName;

    @Schema(description = "Customer email at time of order", example = "john.doe@email.com")
    private String customerEmail;

    @Schema(description = "Warehouse ID", example = "1")
    private Long warehouseId;

    @Schema(description = "Warehouse name", example = "Main Warehouse")
    private String warehouseName;

    @Schema(description = "Current status", example = "CONFIRMED")
    private SalesOrderStatus status;

    @Schema(description = "Order date", example = "2026-02-20")
    private LocalDate orderDate;

    @Schema(description = "Fulfillment date", example = "2026-02-21")
    private LocalDate fulfillmentDate;

    @Schema(description = "Shipping date", example = "2026-02-22")
    private LocalDate shippingDate;

    @Schema(description = "Delivery date", example = "2026-02-25")
    private LocalDate deliveryDate;

    @Schema(description = "Number of line items", example = "2")
    private int itemCount;

    @Schema(description = "Subtotal before tax and shipping", example = "2599.98")
    private BigDecimal subtotal;

    @Schema(description = "Tax amount", example = "207.99")
    private BigDecimal taxAmount;

    @Schema(description = "Shipping cost", example = "15.00")
    private BigDecimal shippingCost;

    @Schema(description = "Total order amount", example = "2822.97")
    private BigDecimal totalAmount;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}