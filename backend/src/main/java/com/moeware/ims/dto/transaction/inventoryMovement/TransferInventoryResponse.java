package com.moeware.ims.dto.transaction.inventoryMovement;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response for inventory transfer operation")
public class TransferInventoryResponse {

    @Schema(description = "Movement record ID", example = "1523")
    private Long movementId;

    @Schema(description = "Product ID", example = "10")
    private Long productId;

    @Schema(description = "Source warehouse information")
    private WarehouseTransferInfo fromWarehouse;

    @Schema(description = "Destination warehouse information")
    private WarehouseTransferInfo toWarehouse;

    @Schema(description = "Quantity transferred", example = "10")
    private Integer quantityTransferred;

    @Schema(description = "Movement timestamp", example = "2026-02-09T10:45:00")
    private LocalDateTime movementDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Warehouse transfer information")
    public static class WarehouseTransferInfo {
        @Schema(description = "Warehouse ID", example = "1")
        private Long id;

        @Schema(description = "Warehouse name", example = "Main Warehouse")
        private String name;

        @Schema(description = "New quantity after transfer", example = "35")
        private Integer newQuantity;
    }
}