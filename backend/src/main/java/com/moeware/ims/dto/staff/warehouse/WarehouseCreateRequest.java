package com.moeware.ims.dto.staff.warehouse;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new warehouse
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for creating a new warehouse")
public class WarehouseCreateRequest {

    @Schema(description = "Warehouse name", example = "Main Warehouse", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Warehouse name is required")
    @Size(max = 100, message = "Warehouse name must not exceed 100 characters")
    private String name;

    @Schema(description = "Short warehouse code", example = "WH001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Warehouse code is required")
    @Size(max = 20, message = "Warehouse code must not exceed 20 characters")
    private String code;

    @Schema(description = "Street address", example = "123 Storage Street", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Address is required")
    private String address;

    @Schema(description = "City", example = "New York", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Schema(description = "State or province", example = "NY")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Schema(description = "Country", example = "USA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Schema(description = "Postal or ZIP code", example = "10001")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @Schema(description = "Warehouse manager user ID", example = "2")
    private Long managerId;

    @Schema(description = "Storage capacity in square feet", example = "50000.00")
    private BigDecimal capacity;

    @Schema(description = "Whether the warehouse is operational", example = "true")
    @Builder.Default
    private Boolean isActive = true;
}