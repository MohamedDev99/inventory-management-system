package com.moeware.ims.dto.transaction.inventoryMovement;

import java.time.LocalDateTime;

import com.moeware.ims.enums.transaction.MovementType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for a single inventory movement record.
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Inventory movement record — tracks a single stock movement event")
public class InventoryMovementDTO {

    @Schema(description = "Unique identifier of the movement record", example = "1523", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Product that was moved")
    private ProductSummaryDTO product;

    @Schema(description = "Source warehouse — null for RECEIPT movements (stock arriving from a supplier)")
    private WarehouseSummaryDTO fromWarehouse;

    @Schema(description = "Destination warehouse — null for SHIPMENT movements (stock leaving to a customer)")
    private WarehouseSummaryDTO toWarehouse;

    @Schema(description = "Number of units moved", example = "10", minimum = "1")
    private Integer quantity;

    @Schema(description = "Type of inventory movement", example = "TRANSFER", allowableValues = { "TRANSFER",
            "ADJUSTMENT", "RECEIPT", "SHIPMENT" })
    private MovementType movementType;

    @Schema(description = "Reason or explanation for the movement", example = "Rebalancing stock levels between warehouses")
    private String reason;

    @Schema(description = "Reference to the related document (PO number, SO number, adjustment ID, etc.)", example = "PO-20260131-0001")
    private String referenceNumber;

    @Schema(description = "User who performed or authorized the movement")
    private UserSummaryDTO performedBy;

    @Schema(description = "Date and time when the movement occurred", example = "2026-01-31T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime movementDate;

    @Schema(description = "Record creation timestamp", example = "2026-01-31T10:30:05", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    // ---- Nested summaries ----

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Condensed product information included in a movement record")
    public static class ProductSummaryDTO {

        @Schema(description = "Product ID", example = "10")
        private Long id;

        @Schema(description = "Stock keeping unit", example = "LAP-001")
        private String sku;

        @Schema(description = "Product name", example = "Dell Laptop XPS 15")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Condensed warehouse information included in a movement record")
    public static class WarehouseSummaryDTO {

        @Schema(description = "Warehouse ID", example = "1")
        private Long id;

        @Schema(description = "Warehouse name", example = "Main Warehouse")
        private String name;

        @Schema(description = "Short warehouse code", example = "WH001")
        private String code;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Condensed user information included in a movement record")
    public static class UserSummaryDTO {

        @Schema(description = "User ID", example = "5")
        private Long id;

        @Schema(description = "Username", example = "warehouse_staff")
        private String username;
    }
}