package com.moeware.ims.dto.staff.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for updating an existing customer.
 * All fields are optional — only non-null fields will be applied.
 * At least one field must be provided.
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for updating an existing customer (all fields optional)")
public class CustomerUpdateRequest {

    // -------------------------------------------------------------------------
    // Identity
    // -------------------------------------------------------------------------

    @Schema(description = "Company name for corporate customers", example = "ABC Corporation")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String companyName;

    @Schema(description = "Primary contact person name", example = "John Doe")
    @Size(max = 255, message = "Contact name must not exceed 255 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Contact name must not be blank if provided")
    private String contactName;

    @Schema(description = "Contact email address", example = "john.doe@email.com")
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
    @Pattern(regexp = "^(?!\\s*$).+", message = "Billing city must not be blank if provided")
    private String billingCity;

    @Schema(description = "Billing state or province", example = "NY")
    @Size(max = 100, message = "Billing state must not exceed 100 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Billing state must not be blank if provided")
    private String billingState;

    @Schema(description = "Billing country", example = "USA")
    @Size(max = 100, message = "Billing country must not exceed 100 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Billing country must not be blank if provided")
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
    @Pattern(regexp = "^(?!\\s*$).+", message = "Shipping city must not be blank if provided")
    private String shippingCity;

    @Schema(description = "Shipping state or province", example = "MA")
    @Size(max = 100, message = "Shipping state must not exceed 100 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Shipping state must not be blank if provided")
    private String shippingState;

    @Schema(description = "Shipping country", example = "USA")
    @Size(max = 100, message = "Shipping country must not exceed 100 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Shipping country must not be blank if provided")
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

    @Schema(description = "Customer type classification", example = "CORPORATE", allowableValues = { "RETAIL",
            "WHOLESALE", "CORPORATE" })
    @Pattern(regexp = "^(RETAIL|WHOLESALE|CORPORATE)$", message = "Customer type must be RETAIL, WHOLESALE, or CORPORATE")
    private String customerType;

    @Schema(description = "Tax identification number", example = "12-3456789")
    @Size(max = 50, message = "Tax ID must not exceed 50 characters")
    private String taxId;

    @Schema(description = "Whether the customer account is active", example = "true")
    private Boolean isActive;

    // -------------------------------------------------------------------------
    // Guard — reject empty body
    // -------------------------------------------------------------------------

    @AssertTrue(message = "At least one field must be provided for update")
    public boolean isAtLeastOneFieldPresent() {
        return companyName != null || contactName != null || email != null ||
                phone != null || mobile != null ||
                billingAddress != null || billingCity != null || billingState != null ||
                billingCountry != null || billingPostalCode != null ||
                shippingAddress != null || shippingCity != null || shippingState != null ||
                shippingCountry != null || shippingPostalCode != null ||
                creditLimit != null || paymentTerms != null ||
                customerType != null || taxId != null || isActive != null;
    }
}