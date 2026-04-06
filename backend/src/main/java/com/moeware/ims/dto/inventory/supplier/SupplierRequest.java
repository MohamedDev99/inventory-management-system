package com.moeware.ims.dto.inventory.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or fully updating a Supplier (POST / PUT).
 * All required fields are validated.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating a supplier")
public class SupplierRequest {

    @NotBlank(message = "Supplier name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Schema(description = "Company name of the supplier", example = "Tech Supplies Inc", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Supplier code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    @Schema(description = "Unique code identifying the supplier", example = "SUP001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    @Schema(description = "Primary contact person at supplier", example = "John Smith")
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Schema(description = "Primary email address for supplier", example = "orders@techsupplies.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Size(max = 50, message = "Phone must not exceed 50 characters")
    @Schema(description = "Primary phone number", example = "+1-555-0100")
    private String phone;

    @Schema(description = "Full street address of supplier", example = "123 Business Park Drive")
    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Schema(description = "City", example = "New York")
    private String city;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Schema(description = "Country", example = "USA")
    private String country;

    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    @Schema(description = "Payment terms with supplier", example = "Net 30")
    private String paymentTerms;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    @Schema(description = "Supplier rating from 1 (poor) to 5 (excellent)", example = "4", minimum = "1", maximum = "5")
    private Integer rating;

    @Schema(description = "Whether the supplier is currently active", example = "true", defaultValue = "true")
    private Boolean isActive;
}