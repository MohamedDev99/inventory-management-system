package com.moeware.ims.dto.inventory.supplier;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Supplier (GET endpoints).
 * Includes audit/meta fields. No validation annotations needed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Supplier response payload")
public class SupplierResponse {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Company name of the supplier", example = "Tech Supplies Inc")
    private String name;

    @Schema(description = "Unique code identifying the supplier", example = "SUP001")
    private String code;

    @Schema(description = "Primary contact person at supplier", example = "John Smith")
    private String contactPerson;

    @Schema(description = "Primary email address for supplier", example = "orders@techsupplies.com")
    private String email;

    @Schema(description = "Primary phone number", example = "+1-555-0100")
    private String phone;

    @Schema(description = "Full street address of supplier", example = "123 Business Park Drive")
    private String address;

    @Schema(description = "City", example = "New York")
    private String city;

    @Schema(description = "Country", example = "USA")
    private String country;

    @Schema(description = "Payment terms with supplier", example = "Net 30")
    private String paymentTerms;

    @Schema(description = "Supplier rating from 1 (poor) to 5 (excellent)", example = "4")
    private Integer rating;

    @Schema(description = "Whether the supplier is currently active", example = "true")
    private Boolean isActive;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Created by user")
    private String createdBy;

    @Schema(description = "Last modified by user")
    private String updatedBy;

    @Schema(description = "Version for optimistic locking")
    private Long version;
}