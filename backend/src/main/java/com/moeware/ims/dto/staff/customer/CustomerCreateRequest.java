package com.moeware.ims.dto.staff.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for creating a new customer.
 * Contains only fields that a client is allowed to supply on creation.
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating a new customer")
public class CustomerCreateRequest {

    // -------------------------------------------------------------------------
    // Identity
    // -------------------------------------------------------------------------

    @Schema(description = "Unique customer code", example = "CUST-001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Customer code is required")
    @Size(max = 50, message = "Customer code must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z0-9\\-]+$", message = "Customer code must contain only uppercase letters, digits, or hyphens")
    private String customerCode;

    @Schema(description = "Company name for corporate customers", example = "ABC Corporation")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String companyName;

    @Schema(description = "Primary contact person name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Contact name is required")
    @Size(max = 255, message = "Contact name must not exceed 255 characters")
    private String contactName;

    @Schema(description = "Contact email address", example = "john.doe@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Schema(description = "Primary phone number (E.164 format)", example = "+15550100")
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone must be in E.164 format (e.g. +15550100)")
    private String phone;

    @Schema(description = "Mobile phone number (E.164 format)", example = "+15550101")
    @Size(max = 50, message = "Mobile must not exceed 50 characters")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Mobile must be in E.164 format (e.g. +15550101)")
    private String mobile;

    // -------------------------------------------------------------------------
    // Billing Address
    // -------------------------------------------------------------------------

    @Schema(description = "Billing street address", example = "123 Business St")
    private String billingAddress;

    @Schema(description = "Billing city", example = "New York")
    @Size(max = 100, message = "Billing city must not exceed 100 characters")
    private String billingCity;

    @Schema(description = "Billing state or province", example = "NY")
    @Size(max = 100, message = "Billing state must not exceed 100 characters")
    private String billingState;

    @Schema(description = "Billing country", example = "USA")
    @Size(max = 100, message = "Billing country must not exceed 100 characters")
    private String billingCountry;

    @Schema(description = "Billing postal code", example = "10001")
    @Size(max = 20, message = "Billing postal code must not exceed 20 characters")
    private String billingPostalCode;

    // -------------------------------------------------------------------------
    // Shipping Address
    // -------------------------------------------------------------------------

    @Schema(description = "Shipping street address", example = "456 Delivery Ave")
    private String shippingAddress;

    @Schema(description = "Shipping city", example = "Boston")
    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    private String shippingCity;

    @Schema(description = "Shipping state or province", example = "MA")
    @Size(max = 100, message = "Shipping state must not exceed 100 characters")
    private String shippingState;

    @Schema(description = "Shipping country", example = "USA")
    @Size(max = 100, message = "Shipping country must not exceed 100 characters")
    private String shippingCountry;

    @Schema(description = "Shipping postal code", example = "02101")
    @Size(max = 20, message = "Shipping postal code must not exceed 20 characters")
    private String shippingPostalCode;

    // -------------------------------------------------------------------------
    // Commercial
    // -------------------------------------------------------------------------

    @Schema(description = "Customer credit limit", example = "10000.00")
    @DecimalMin(value = "0.0", inclusive = true, message = "Credit limit must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Credit limit must have at most 10 integer digits and 2 decimal places")
    private BigDecimal creditLimit;

    @Schema(description = "Payment terms", example = "Net 30")
    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    private String paymentTerms;

    @Schema(description = "Customer type classification", example = "CORPORATE", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
            "RETAIL", "WHOLESALE", "CORPORATE" })
    @NotBlank(message = "Customer type is required")
    @Pattern(regexp = "^(RETAIL|WHOLESALE|CORPORATE)$", message = "Customer type must be RETAIL, WHOLESALE, or CORPORATE")
    private String customerType;

    @Schema(description = "Tax identification number", example = "12-3456789")
    @Size(max = 50, message = "Tax ID must not exceed 50 characters")
    private String taxId;

    @Schema(description = "Whether the customer account is active", example = "true", defaultValue = "true")
    @Builder.Default
    private Boolean isActive = true;
}