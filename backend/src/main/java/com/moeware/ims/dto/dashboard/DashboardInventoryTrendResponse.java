package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.moeware.ims.enums.DashboardPeriod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chart data for the inventory-trend widget.
 * Each data point captures total value, product count, and low-stock count on a
 * given date.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardInventoryTrendResponse {

    private DashboardPeriod period;
    private List<DataPointDTO> dataPoints;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPointDTO {
        private LocalDate date;
        private BigDecimal totalValue;
        private int productCount;
        private int lowStockCount;
    }
}