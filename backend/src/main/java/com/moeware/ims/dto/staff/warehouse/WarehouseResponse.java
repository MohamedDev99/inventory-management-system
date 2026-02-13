package com.moeware.ims.dto.staff.warehouse;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for warehouse response data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO containing warehouse information")
public class WarehouseResponse {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Warehouse name", example = "Main Warehouse")
    private String name;

    @Schema(description = "Warehouse code", example = "WH001")
    private String code;

    @Schema(description = "Street address", example = "123 Storage Street")
    private String address;

    @Schema(description = "City", example = "New York")
    private String city;

    @Schema(description = "State or province", example = "NY")
    private String state;

    @Schema(description = "Country", example = "USA")
    private String country;

    @Schema(description = "Postal or ZIP code", example = "10001")
    private String postalCode;

    @Schema(description = "Full formatted address")
    private String fullAddress;

    @Schema(description = "Warehouse manager information")
    private ManagerSummary manager;

    @Schema(description = "Storage capacity in square feet", example = "50000.00")
    private BigDecimal capacity;

    @Schema(description = "Active status", example = "true")
    private Boolean isActive;

    @Schema(description = "Number of different product types", example = "125")
    private Integer totalProductTypes;

    @Schema(description = "Total stock units", example = "5432")
    private Integer totalStockUnits;

    @Schema(description = "Creation timestamp")
    private java.time.LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private java.time.LocalDateTime updatedAt;

    @Schema(description = "Version for optimistic locking")
    private Long version;

    /**
     * Nested DTO for manager summary
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Summary of warehouse manager information")
    public static class ManagerSummary {
        @Schema(description = "User ID", example = "2")
        private Long id;

        @Schema(description = "Username", example = "john_manager")
        private String username;

        @Schema(description = "Email", example = "john@inventory.com")
        private String email;
    }
}