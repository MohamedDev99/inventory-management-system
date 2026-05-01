package com.moeware.ims.dto.transaction.inventoryMovement;

import java.time.LocalDateTime;
import java.util.List;

import com.moeware.ims.enums.transaction.MovementType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Summary DTO for the GET /api/inventory-movements/summary endpoint.
 *
 * <p>
 * Groups movement totals by type and by warehouse over a given period.
 * </p>
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Aggregated summary of inventory movements over a specified date range")
public class InventoryMovementSummaryDTO {

    @Schema(description = "Reporting period covered by this summary")
    private PeriodDTO period;

    @Schema(description = "Total quantities broken down by movement type")
    private TotalsDTO totals;

    @Schema(description = "Per movement-type breakdown showing count and total quantity")
    private List<ByTypeDTO> byMovementType;

    @Schema(description = "Per warehouse breakdown showing receipts, shipments, and net change")
    private List<ByWarehouseDTO> byWarehouse;

    // ---- Nested DTOs ----

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Date range for the summary")
    public static class PeriodDTO {

        @Schema(description = "Start of the reporting period (inclusive)", example = "2026-01-01T00:00:00")
        private LocalDateTime startDate;

        @Schema(description = "End of the reporting period (inclusive)", example = "2026-02-09T23:59:59")
        private LocalDateTime endDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Total quantities by movement type across the entire period")
    public static class TotalsDTO {

        @Schema(description = "Total units received into any warehouse (RECEIPT movements)", example = "2500")
        private Long receipts;

        @Schema(description = "Total units shipped out to customers (SHIPMENT movements)", example = "1800")
        private Long shipments;

        @Schema(description = "Total units transferred between warehouses (TRANSFER movements)", example = "350")
        private Long transfers;

        @Schema(description = "Total units affected by manual adjustments (ADJUSTMENT movements)", example = "45")
        private Long adjustments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Movement count and total quantity for a single movement type")
    public static class ByTypeDTO {

        @Schema(description = "Movement type", example = "RECEIPT", allowableValues = { "TRANSFER", "ADJUSTMENT",
                "RECEIPT", "SHIPMENT" })
        private MovementType movementType;

        @Schema(description = "Number of individual movement records of this type", example = "125")
        private Long count;

        @Schema(description = "Sum of quantities across all records of this type", example = "2500")
        private Long totalQuantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Movement breakdown for a specific warehouse")
    public static class ByWarehouseDTO {

        @Schema(description = "Warehouse ID", example = "1")
        private Long warehouseId;

        @Schema(description = "Warehouse name", example = "Main Warehouse")
        private String warehouseName;

        @Schema(description = "Total units received into this warehouse (RECEIPT movements)", example = "1500")
        private Long receipts;

        @Schema(description = "Total units shipped out of this warehouse (SHIPMENT movements)", example = "1200")
        private Long shipments;

        @Schema(description = "Net quantity change — receipts minus shipments (positive = net gain)", example = "300")
        private Long netChange;
    }
}