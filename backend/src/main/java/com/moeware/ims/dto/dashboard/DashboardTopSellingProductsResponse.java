package com.moeware.ims.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

import com.moeware.ims.enums.DashboardPeriod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chart data for the top-selling-products widget.
 * Each entry shows units sold, revenue, and percentage share of total revenue.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardTopSellingProductsResponse {

    private DashboardPeriod period;
    private List<TopProductDTO> products;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductDTO {
        private Long productId;
        private String sku;
        private String name;
        private int unitsSold;
        private BigDecimal revenue;
        /** Percentage share of total revenue in the period (0-100) */
        private double percentage;
    }
}