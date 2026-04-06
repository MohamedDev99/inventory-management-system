package com.moeware.ims.dto.staff.warehouse;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing warehouse
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for updating an existing warehouse")
public class WarehouseUpdateRequest {

    @Schema(description = "Warehouse name", example = "Main Warehouse")
    @Size(max = 100, message = "Warehouse name must not exceed 100 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Warehouse name must not be blank if provided")
    private String name;

    @Schema(description = "Street address", example = "123 Storage Street")
    private String address;

    @Schema(description = "City", example = "New York")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Warehouse name must not be blank if provided")
    private String city;

    @Schema(description = "State or province", example = "NY")
    @Size(max = 100, message = "State must not exceed 100 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Warehouse name must not be blank if provided")
    private String state;

    @Schema(description = "Country", example = "USA")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Warehouse name must not be blank if provided")
    private String country;

    @Schema(description = "Postal or ZIP code", example = "10001")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @Schema(description = "Warehouse manager user ID", example = "2")
    private Long managerId;

    @Schema(description = "Storage capacity in square feet", example = "50000.00")
    @DecimalMin(value = "0.01", message = "Capacity must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Capacity must have at most 10 integer digits and 2 decimal places")
    private BigDecimal capacity;

    @Schema(description = "Whether the warehouse is operational", example = "true")
    private Boolean isActive;

    @AssertTrue(message = "At least one field must be provided for update")
    public boolean isAtLeastOneFieldPresent() {
        return name != null || address != null || city != null ||
                state != null || country != null || postalCode != null ||
                managerId != null || capacity != null || isActive != null;
    }
}