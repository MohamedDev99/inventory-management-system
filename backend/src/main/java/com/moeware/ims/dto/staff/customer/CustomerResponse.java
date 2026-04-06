package com.moeware.ims.dto.staff.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Read-only DTO returned to clients for customer data.
 * Never used as a request body — contains no validation annotations.
 * Null fields are excluded from the serialized JSON response.
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Customer response payload")
public class CustomerResponse {

    // -------------------------------------------------------------------------
    // Identity
    // -------------------------------------------------------------------------

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Unique customer code", example = "CUST-001")
    private String customerCode;

    @Schema(description = "Company name for corporate customers", example = "ABC Corporation")
    private String companyName;

    @Schema(description = "Primary contact person name", example = "John Doe")
    private String contactName;

    @Schema(description = "Contact email address", example = "john.doe@email.com")
    private String email;

    @Schema(description = "Primary phone number", example = "+15550100")
    private String phone;

    @Schema(description = "Mobile phone number", example = "+15550101")
    private String mobile;

    // -------------------------------------------------------------------------
    // Billing Address
    // -------------------------------------------------------------------------

    @Schema(description = "Billing street address", example = "123 Business St")
    private String billingAddress;

    @Schema(description = "Billing city", example = "New York")
    private String billingCity;

    @Schema(description = "Billing state or province", example = "NY")
    private String billingState;

    @Schema(description = "Billing country", example = "USA")
    private String billingCountry;

    @Schema(description = "Billing postal code", example = "10001")
    private String billingPostalCode;

    // -------------------------------------------------------------------------
    // Shipping Address
    // -------------------------------------------------------------------------

    @Schema(description = "Shipping street address", example = "456 Delivery Ave")
    private String shippingAddress;

    @Schema(description = "Shipping city", example = "Boston")
    private String shippingCity;

    @Schema(description = "Shipping state or province", example = "MA")
    private String shippingState;

    @Schema(description = "Shipping country", example = "USA")
    private String shippingCountry;

    @Schema(description = "Shipping postal code", example = "02101")
    private String shippingPostalCode;

    // -------------------------------------------------------------------------
    // Commercial
    // -------------------------------------------------------------------------

    @Schema(description = "Customer credit limit", example = "10000.00")
    private BigDecimal creditLimit;

    @Schema(description = "Payment terms", example = "Net 30")
    private String paymentTerms;

    @Schema(description = "Customer type classification", example = "CORPORATE", allowableValues = { "RETAIL",
            "WHOLESALE", "CORPORATE" })
    private String customerType;

    @Schema(description = "Tax identification number", example = "12-3456789")
    private String taxId;

    @Schema(description = "Whether the customer account is active", example = "true")
    private Boolean isActive;

    // -------------------------------------------------------------------------
    // Audit (server-generated — never writable by client)
    // -------------------------------------------------------------------------

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Username of the user who created this record")
    private String createdBy;

    @Schema(description = "Username of the user who last modified this record")
    private String updatedBy;

    @Schema(description = "Version for optimistic locking")
    private Long version;
}