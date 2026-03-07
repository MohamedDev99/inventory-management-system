package com.moeware.ims.dto.transaction.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a sales order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for creating or updating a sales order")
public class SalesOrderRequest {

    @NotNull(message = "Customer ID is required")
    @Schema(description = "ID of the customer placing the order", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long customerId;

    @NotNull(message = "Warehouse ID is required")
    @Schema(description = "ID of the warehouse responsible for fulfillment", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long warehouseId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 255)
    @Schema(description = "Customer name at time of order (denormalized)", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email
    @Size(max = 255)
    @Schema(description = "Customer email at time of order", example = "john.doe@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerEmail;

    @Size(max = 50)
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone must be in E.164 format")
    @Schema(description = "Customer phone at time of order (E.164 format)", example = "+15550100")
    private String customerPhone;

    @NotBlank(message = "Shipping address is required")
    @Schema(description = "Full shipping street address", example = "789 Customer St, Apt 4B", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shippingAddress;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    @Schema(description = "Shipping city", example = "New York", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20)
    @Schema(description = "Shipping postal/ZIP code", example = "10001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String postalCode;

    @NotNull(message = "Order date is required")
    @Schema(description = "Date when the customer placed the order", example = "2026-02-20", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate orderDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount must be non-negative")
    @Digits(integer = 10, fraction = 2)
    @Schema(description = "Sales tax amount", example = "104.00", defaultValue = "0.00")
    private BigDecimal taxAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Shipping cost must be non-negative")
    @Digits(integer = 10, fraction = 2)
    @Schema(description = "Shipping cost", example = "15.00", defaultValue = "0.00")
    private BigDecimal shippingCost;

    @Schema(description = "Special delivery instructions or order notes", example = "Please ring doorbell")
    private String notes;

    @NotEmpty(message = "Sales order must have at least one line item")
    @Valid
    @Schema(description = "List of line items (products) in this order", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SalesOrderItemRequest> items;
}