package com.moeware.ims.dto.transaction.inventoryMovement;

import java.time.LocalDateTime;

import com.moeware.ims.enums.transaction.MovementType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Inventory movement record")
public class InventoryMovementDTO {

    @Schema(description = "Movement ID", example = "1523")
    private Long id;

    @Schema(description = "Product information")
    private ProductSummaryDTO product;

    @Schema(description = "Source warehouse (null for receipts)")
    private WarehouseSummaryDTO fromWarehouse;

    @Schema(description = "Destination warehouse (null for shipments)")
    private WarehouseSummaryDTO toWarehouse;

    @Schema(description = "Quantity moved", example = "10")
    private Integer quantity;

    @Schema(description = "Movement type", example = "TRANSFER")
    private MovementType movementType;

    @Schema(description = "Reason for movement", example = "Rebalancing stock levels")
    private String reason;

    @Schema(description = "Reference number (PO, SO, etc.)", example = "PO-20260131-0001")
    private String referenceNumber;

    @Schema(description = "User who performed the movement")
    private UserSummaryDTO performedBy;

    @Schema(description = "Movement timestamp", example = "2026-01-31T10:30:00")
    private LocalDateTime movementDate;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Product summary")
    public static class ProductSummaryDTO {
        @Schema(description = "Product ID", example = "10")
        private Long id;

        @Schema(description = "SKU", example = "LAP-001")
        private String sku;

        @Schema(description = "Product name", example = "Dell Laptop XPS 15")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Warehouse summary")
    public static class WarehouseSummaryDTO {
        @Schema(description = "Warehouse ID", example = "1")
        private Long id;

        @Schema(description = "Warehouse name", example = "Main Warehouse")
        private String name;

        @Schema(description = "Warehouse code", example = "WH001")
        private String code;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "User summary")
    public static class UserSummaryDTO {
        @Schema(description = "User ID", example = "5")
        private Long id;

        @Schema(description = "Username", example = "warehouse_staff")
        private String username;
    }
}