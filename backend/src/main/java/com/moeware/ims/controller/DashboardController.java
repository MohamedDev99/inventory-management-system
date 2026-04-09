package com.moeware.ims.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.dashboard.DashboardActivityFeedResponse;
import com.moeware.ims.dto.dashboard.DashboardInventorySummaryResponse;
import com.moeware.ims.dto.dashboard.DashboardInventoryTrendResponse;
import com.moeware.ims.dto.dashboard.DashboardLowStockAlertsResponse;
import com.moeware.ims.dto.dashboard.DashboardOverviewResponse;
import com.moeware.ims.dto.dashboard.DashboardPendingActionsResponse;
import com.moeware.ims.dto.dashboard.DashboardPurchaseAnalyticsResponse;
import com.moeware.ims.dto.dashboard.DashboardSalesAnalyticsResponse;
import com.moeware.ims.dto.dashboard.DashboardSalesTrendResponse;
import com.moeware.ims.dto.dashboard.DashboardTopSellingProductsResponse;
import com.moeware.ims.enums.DashboardPeriod;
import com.moeware.ims.service.DashboardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for all dashboard aggregation endpoints.
 * <p>
 * Base path: {@code /api/dashboard}
 * <p>
 * All endpoints require authentication. Role filtering is applied at the
 * service
 * layer where needed (e.g. a VIEWER only sees their warehouse data).
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    // ─── 1. Overview ─────────────────────────────────────────────────────────

    /**
     * GET /api/dashboard/overview
     * <p>
     * Returns system-wide KPIs: product counts, inventory value, order counts,
     * today's activity, alert summary, and revenue figures.
     * Accessible by all authenticated roles; content is role-filtered in the
     * service.
     */
    @GetMapping("/overview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<DashboardOverviewResponse>> getOverview() {
        log.debug("GET /dashboard/overview");
        DashboardOverviewResponse data = dashboardService.getOverview();
        return ResponseEntity.ok(ApiResponseWpp.success(data));
    }

    // ─── 2. Inventory Summary ─────────────────────────────────────────────────

    /**
     * GET /api/dashboard/inventory-summary
     * <p>
     * Returns inventory totals (cost/retail value, profit), stock-status
     * distribution,
     * and per-warehouse / per-category breakdowns.
     *
     * @param warehouseId optional warehouse filter
     * @param categoryId  optional category filter
     */
    @GetMapping("/inventory-summary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<DashboardInventorySummaryResponse>> getInventorySummary(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long categoryId) {
        log.debug("GET /dashboard/inventory-summary warehouseId={} categoryId={}", warehouseId, categoryId);
        DashboardInventorySummaryResponse data = dashboardService.getInventorySummary(warehouseId, categoryId);
        return ResponseEntity.ok(ApiResponseWpp.success(data));
    }

    // ─── 3. Sales Analytics ───────────────────────────────────────────────────

    /**
     * GET /api/dashboard/sales-analytics
     * <p>
     * Returns sales performance for the given period or custom date range.
     * Includes order totals, status distribution, top products/customers, daily
     * trend,
     * and period-over-period growth metrics.
     *
     * @param period    predefined time window (TODAY | WEEK | MONTH | QUARTER |
     *                  YEAR)
     * @param startDate custom range start (overrides period when both start + end
     *                  supplied)
     * @param endDate   custom range end
     */
    @GetMapping("/sales-analytics")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponseWpp<DashboardSalesAnalyticsResponse>> getSalesAnalytics(
            @RequestParam(required = false, defaultValue = "MONTH") DashboardPeriod period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("GET /dashboard/sales-analytics period={} start={} end={}", period, startDate, endDate);
        DashboardSalesAnalyticsResponse data = dashboardService.getSalesAnalytics(period, startDate, endDate);
        return ResponseEntity.ok(ApiResponseWpp.success(data));
    }

    // ─── 4. Purchase Analytics ────────────────────────────────────────────────

    /**
     * GET /api/dashboard/purchase-analytics
     * <p>
     * Returns purchase-order performance for the given period.
     * Includes PO totals, status distribution, top suppliers, category spend, and
     * monthly trend.
     *
     * @param period    predefined time window
     * @param startDate custom range start
     * @param endDate   custom range end
     */
    @GetMapping("/purchase-analytics")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponseWpp<DashboardPurchaseAnalyticsResponse>> getPurchaseAnalytics(
            @RequestParam(required = false, defaultValue = "MONTH") DashboardPeriod period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("GET /dashboard/purchase-analytics period={} start={} end={}", period, startDate, endDate);
        DashboardPurchaseAnalyticsResponse data = dashboardService.getPurchaseAnalytics(period, startDate, endDate);
        return ResponseEntity.ok(ApiResponseWpp.success(data));
    }

    // ─── 5. Low Stock Alerts ──────────────────────────────────────────────────

    /**
     * GET /api/dashboard/low-stock-alerts
     * <p>
     * Returns all active products at or below their reorder level, with
     * per-warehouse
     * stock breakdown and CRITICAL / WARNING severity classification.
     */
    @GetMapping("/low-stock-alerts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<DashboardLowStockAlertsResponse>> getLowStockAlerts() {
        log.debug("GET /dashboard/low-stock-alerts");
        DashboardLowStockAlertsResponse data = dashboardService.getLowStockAlerts();
        return ResponseEntity.ok(ApiResponseWpp.success(data));
    }

    // ─── 6. Pending Actions ───────────────────────────────────────────────────

    /**
     * GET /api/dashboard/pending-actions
     * <p>
     * Returns all items requiring immediate user attention: PO / adjustment
     * approvals,
     * overdue invoices, pending shipments, and low-stock alert count.
     */
    @GetMapping("/pending-actions")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponseWpp<DashboardPendingActionsResponse>> getPendingActions() {
        log.debug("GET /dashboard/pending-actions");
        DashboardPendingActionsResponse data = dashboardService.getPendingActions();
        return ResponseEntity.ok(ApiResponseWpp.success(data));
    }

    // ─── 7. Activity Feed ─────────────────────────────────────────────────────

    /**
     * GET /api/dashboard/activity-feed
     * <p>
     * Returns a chronological feed of the most recent system events:
     * sales order changes, PO status updates, and shipment deliveries.
     *
     * @param limit maximum number of activities to return (default 20, max 100)
     */
    @GetMapping("/activity-feed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<DashboardActivityFeedResponse>> getActivityFeed(
            @RequestParam(required = false, defaultValue = "20") int limit) {
        limit = Math.min(limit, 100);
        log.debug("GET /dashboard/activity-feed limit={}", limit);
        DashboardActivityFeedResponse data = dashboardService.getActivityFeed(limit);
        return ResponseEntity.ok(ApiResponseWpp.success(data));
    }

    // ─── 8. Inventory Trend Chart ─────────────────────────────────────────────

    /**
     * GET /api/dashboard/charts/inventory-trend
     * <p>
     * Returns per-day inventory-value snapshots for the requested period.
     * Optionally filtered to a single warehouse.
     *
     * @param period      time window (default MONTH)
     * @param warehouseId optional warehouse filter
     */
    @GetMapping("/charts/inventory-trend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<DashboardInventoryTrendResponse>> getInventoryTrend(
            @RequestParam(required = false, defaultValue = "MONTH") DashboardPeriod period,
            @RequestParam(required = false) Long warehouseId) {
        log.debug("GET /dashboard/charts/inventory-trend period={} warehouseId={}", period, warehouseId);
        DashboardInventoryTrendResponse data = dashboardService.getInventoryTrend(period, warehouseId);
        return ResponseEntity.ok(ApiResponseWpp.success(data));
    }

    // ─── 9. Sales Trend Chart ─────────────────────────────────────────────────

    /**
     * GET /api/dashboard/charts/sales-trend
     * <p>
     * Returns per-day order count and revenue data, plus a period-over-period
     * growth comparison.
     *
     * @param period time window (default MONTH)
     */
    @GetMapping("/charts/sales-trend")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponseWpp<DashboardSalesTrendResponse>> getSalesTrend(
            @RequestParam(required = false, defaultValue = "MONTH") DashboardPeriod period) {
        log.debug("GET /dashboard/charts/sales-trend period={}", period);
        DashboardSalesTrendResponse data = dashboardService.getSalesTrend(period);
        return ResponseEntity.ok(ApiResponseWpp.success(data));
    }

    // ─── 10. Top Selling Products Chart ──────────────────────────────────────

    /**
     * GET /api/dashboard/charts/top-selling-products
     * <p>
     * Returns the top N products by revenue for the given period.
     * Each entry includes units sold, revenue, and percentage share of total period
     * revenue.
     *
     * @param period time window (default MONTH)
     * @param limit  number of products to return (default 10)
     */
    @GetMapping("/charts/top-selling-products")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponseWpp<DashboardTopSellingProductsResponse>> getTopSellingProducts(
            @RequestParam(required = false, defaultValue = "MONTH") DashboardPeriod period,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        log.debug("GET /dashboard/charts/top-selling-products period={} limit={}", period, limit);
        DashboardTopSellingProductsResponse data = dashboardService.getTopSellingProducts(period, limit);
        return ResponseEntity.ok(ApiResponseWpp.success(data));
    }
}