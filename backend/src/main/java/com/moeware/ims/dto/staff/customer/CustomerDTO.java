package com.moeware.ims.dto.staff.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Customer
 * Used for API request and response payloads
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Customer data transfer object")
public class CustomerDTO {

    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Customer code is required")
    @Size(max = 50, message = "Customer code must not exceed 50 characters")
    @Schema(description = "Unique customer code", example = "CUST-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerCode;

    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Schema(description = "Company name for corporate customers", example = "ABC Corporation")
    private String companyName;

    @NotBlank(message = "Contact name is required")
    @Size(max = 255, message = "Contact name must not exceed 255 characters")
    @Schema(description = "Primary contact person name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contactName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Schema(description = "Contact email address", example = "john.doe@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Size(max = 50, message = "Phone must not exceed 50 characters")
    @Schema(description = "Primary phone number", example = "+1-555-0100")
    private String phone;

    @Size(max = 50, message = "Mobile must not exceed 50 characters")
    @Schema(description = "Mobile phone number", example = "+1-555-0101")
    private String mobile;

    // Billing Address
    @Schema(description = "Billing street address", example = "123 Business St")
    private String billingAddress;

    @Size(max = 100, message = "Billing city must not exceed 100 characters")
    @Schema(description = "Billing city", example = "New York")
    private String billingCity;

    @Size(max = 100, message = "Billing state must not exceed 100 characters")
    @Schema(description = "Billing state or province", example = "NY")
    private String billingState;

    @Size(max = 100, message = "Billing country must not exceed 100 characters")
    @Schema(description = "Billing country", example = "USA")
    private String billingCountry;

    @Size(max = 20, message = "Billing postal code must not exceed 20 characters")
    @Schema(description = "Billing postal code", example = "10001")
    private String billingPostalCode;

    // Shipping Address
    @Schema(description = "Shipping street address", example = "456 Delivery Ave")
    private String shippingAddress;

    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    @Schema(description = "Shipping city", example = "Boston")
    private String shippingCity;

    @Size(max = 100, message = "Shipping state must not exceed 100 characters")
    @Schema(description = "Shipping state or province", example = "MA")
    private String shippingState;

    @Size(max = 100, message = "Shipping country must not exceed 100 characters")
    @Schema(description = "Shipping country", example = "USA")
    private String shippingCountry;

    @Size(max = 20, message = "Shipping postal code must not exceed 20 characters")
    @Schema(description = "Shipping postal code", example = "02101")
    private String shippingPostalCode;

    @DecimalMin(value = "0.0", message = "Credit limit must be non-negative")
    @Schema(description = "Customer credit limit", example = "10000.00")
    private BigDecimal creditLimit;

    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    @Schema(description = "Payment terms", example = "Net 30")
    private String paymentTerms;

    @NotBlank(message = "Customer type is required")
    @Size(max = 20, message = "Customer type must not exceed 20 characters")
    @Pattern(regexp = "RETAIL|WHOLESALE|CORPORATE", message = "Customer type must be RETAIL, WHOLESALE, or CORPORATE")
    @Schema(description = "Customer type classification", example = "CORPORATE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerType;

    @Size(max = 50, message = "Tax ID must not exceed 50 characters")
    @Schema(description = "Tax identification number", example = "12-3456789")
    private String taxId;

    @Schema(description = "Whether the customer account is active", example = "true", defaultValue = "true")
    private Boolean isActive;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    @Schema(description = "Created by user", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdBy;

    @Schema(description = "Last modified by user", accessMode = Schema.AccessMode.READ_ONLY)
    private String updatedBy;

    @Schema(description = "Version for optimistic locking", accessMode = Schema.AccessMode.READ_ONLY)
    private Long version;
}