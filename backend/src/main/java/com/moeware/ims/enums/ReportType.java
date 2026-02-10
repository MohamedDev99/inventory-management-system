package com.moeware.ims.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Report type enumeration
 */
@Schema(description = "Available report types in the system")
public enum ReportType {
    @Schema(description = "Stock valuation report showing inventory value by warehouse")
    STOCK_VALUATION,

    @Schema(description = "Historical inventory movement tracking report")
    MOVEMENT_HISTORY,

    @Schema(description = "Sales performance and analysis report")
    SALES_ANALYSIS,

    @Schema(description = "Low stock alert report for reorder management")
    LOW_STOCK,

    @Schema(description = "Purchase order history and supplier performance report")
    PURCHASE_HISTORY
}