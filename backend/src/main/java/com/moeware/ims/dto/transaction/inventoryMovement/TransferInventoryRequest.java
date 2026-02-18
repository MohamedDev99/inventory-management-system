package com.moeware.ims.dto.transaction.inventoryMovement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to transfer stock between warehouses")
public class TransferInventoryRequest {

    @Schema(description = "Product ID to transfer", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Schema(description = "Source warehouse ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Source warehouse ID is required")
    private Long fromWarehouseId;

    @Schema(description = "Destination warehouse ID", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Destination warehouse ID is required")
    private Long toWarehouseId;

    @Schema(description = "Quantity to transfer", example = "10", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Schema(description = "Reason for transfer", example = "Rebalancing stock levels")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    @Schema(description = "User ID performing the transfer", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Performed by user ID is required")
    private Long performedBy;
}