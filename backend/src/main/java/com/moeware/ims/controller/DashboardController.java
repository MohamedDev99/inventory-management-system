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
import com.moeware.ims.exception.handler.GlobalExceptionHandler;
import com.moeware.ims.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for all dashboard aggregation endpoints.
 * <p>
 * Base path: {@code /api/dashboard}
 * <p>
 * All endpoints are read-only. Role restrictions are applied per endpoint:
 * financial data (analytics, pending actions) requires ADMIN or MANAGER;
 * operational views (overview, alerts, activity feed) are open to all
 * authenticated roles.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard", description = "Aggregated KPIs, analytics, alerts, and chart data for the MoeWare dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    // ─── 1. Overview ─────────────────────────────────────────────────────────

    @Operation(summary = "Get dashboard overview", description = "Returns system-wide KPIs: total products, inventory value, order queue sizes, "
            +
            "today's activity counts, alert totals, and revenue figures for today / week / month.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Overview data returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthenticated request", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/overview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<DashboardOverviewResponse>> getOverview() {
        log.debug("GET /dashboard/overview");
        return ResponseEntity.ok(ApiResponseWpp.success(dashboardService.getOverview()));
    }

    // ─── 2. Inventory Summary ─────────────────────────────────────────────────

    @Operation(summary = "Get inventory summary", description = "Returns inventory cost/retail value, potential profit, stock-status distribution "
            +
            "(in-stock / low-stock / out-of-stock), and breakdowns by category and warehouse. " +
            "Optionally scoped to a single warehouse or category.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inventory summary returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthenticated request", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/inventory-summary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<DashboardInventorySummaryResponse>> getInventorySummary(
            @Parameter(description = "Scope results to a single warehouse ID") @RequestParam(required = false) Long warehouseId,
            @Parameter(description = "Scope results to a single category ID") @RequestParam(required = false) Long categoryId) {

        log.debug("GET /dashboard/inventory-summary warehouseId={} categoryId={}", warehouseId, categoryId);
        return ResponseEntity.ok(ApiResponseWpp.success(
                dashboardService.getInventorySummary(warehouseId, categoryId)));
    }

    // ─── 3. Sales Analytics ───────────────────────────────────────────────────

    @Operation(summary = "Get sales analytics", description = "Returns sales performance for the requested period: order totals, revenue, "
            +
            "status distribution, top products and customers, daily trend data, and " +
            "period-over-period growth metrics. " +
            "When both startDate and endDate are provided they override the period parameter.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sales analytics returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied — ADMIN or MANAGER role required", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/sales-analytics")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponseWpp<DashboardSalesAnalyticsResponse>> getSalesAnalytics(
            @Parameter(description = "Predefined time window. Ignored when startDate + endDate are both supplied.", schema = @Schema(implementation = DashboardPeriod.class, defaultValue = "MONTH")) @RequestParam(required = false, defaultValue = "MONTH") DashboardPeriod period,
            @Parameter(description = "Custom range start date (yyyy-MM-dd). Must be paired with endDate.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Custom range end date (yyyy-MM-dd). Must be paired with startDate.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.debug("GET /dashboard/sales-analytics period={} start={} end={}", period, startDate, endDate);
        return ResponseEntity.ok(ApiResponseWpp.success(
                dashboardService.getSalesAnalytics(period, startDate, endDate)));
    }

    // ─── 4. Purchase Analytics ────────────────────────────────────────────────

    @Operation(summary = "Get purchase analytics", description = "Returns purchase-order performance for the requested period: PO totals, spend, "
            +
            "status distribution, top suppliers ranked by spend, category spending breakdown, " +
            "and a monthly trend. When both startDate and endDate are provided they override the period.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Purchase analytics returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied — ADMIN or MANAGER role required", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/purchase-analytics")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponseWpp<DashboardPurchaseAnalyticsResponse>> getPurchaseAnalytics(
            @Parameter(description = "Predefined time window. Ignored when startDate + endDate are both supplied.", schema = @Schema(implementation = DashboardPeriod.class, defaultValue = "MONTH")) @RequestParam(required = false, defaultValue = "MONTH") DashboardPeriod period,
            @Parameter(description = "Custom range start date (yyyy-MM-dd). Must be paired with endDate.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Custom range end date (yyyy-MM-dd). Must be paired with startDate.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.debug("GET /dashboard/purchase-analytics period={} start={} end={}", period, startDate, endDate);
        return ResponseEntity.ok(ApiResponseWpp.success(
                dashboardService.getPurchaseAnalytics(period, startDate, endDate)));
    }

    // ─── 5. Low Stock Alerts ──────────────────────────────────────────────────

    @Operation(summary = "Get low stock alerts", description = "Returns all active products whose total warehouse stock is at or below their reorder level. "
            +
            "Each product includes a per-warehouse quantity breakdown. " +
            "Severity is CRITICAL when stock ≤ minStockLevel, otherwise WARNING.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Low stock alerts returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthenticated request", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/low-stock-alerts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<DashboardLowStockAlertsResponse>> getLowStockAlerts() {
        log.debug("GET /dashboard/low-stock-alerts");
        return ResponseEntity.ok(ApiResponseWpp.success(dashboardService.getLowStockAlerts()));
    }

    // ─── 6. Pending Actions ───────────────────────────────────────────────────

    @Operation(summary = "Get pending actions", description = "Returns all items requiring immediate user attention: purchase orders and stock "
            +
            "adjustments awaiting approval, overdue invoices with outstanding balance, " +
            "pending shipments with the oldest entry highlighted, and the current low-stock count.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pending actions returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied — ADMIN or MANAGER role required", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/pending-actions")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponseWpp<DashboardPendingActionsResponse>> getPendingActions() {
        log.debug("GET /dashboard/pending-actions");
        return ResponseEntity.ok(ApiResponseWpp.success(dashboardService.getPendingActions()));
    }

    // ─── 7. Activity Feed ─────────────────────────────────────────────────────

    @Operation(summary = "Get activity feed", description = "Returns a reverse-chronological feed of recent system events merged from "
            +
            "sales orders, purchase order status changes, and delivered shipments. " +
            "Maximum 100 entries per request.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Activity feed returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthenticated request", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/activity-feed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<DashboardActivityFeedResponse>> getActivityFeed(
            @Parameter(description = "Maximum number of activities to return. Capped at 100.", example = "20") @RequestParam(required = false, defaultValue = "20") int limit) {

        limit = Math.min(limit, 100);
        log.debug("GET /dashboard/activity-feed limit={}", limit);
        return ResponseEntity.ok(ApiResponseWpp.success(dashboardService.getActivityFeed(limit)));
    }

    // ─── 8. Inventory Trend Chart ─────────────────────────────────────────────

    @Operation(summary = "Get inventory trend chart data", description = "Returns one data point per calendar day in the requested period. "
            +
            "Each point contains total inventory value, active product count, and low-stock count. " +
            "Optionally scoped to a single warehouse.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inventory trend data returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthenticated request", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/charts/inventory-trend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<DashboardInventoryTrendResponse>> getInventoryTrend(
            @Parameter(description = "Time window for the chart.", schema = @Schema(implementation = DashboardPeriod.class, defaultValue = "MONTH")) @RequestParam(required = false, defaultValue = "MONTH") DashboardPeriod period,
            @Parameter(description = "Scope chart data to a single warehouse ID") @RequestParam(required = false) Long warehouseId) {

        log.debug("GET /dashboard/charts/inventory-trend period={} warehouseId={}", period, warehouseId);
        return ResponseEntity.ok(ApiResponseWpp.success(
                dashboardService.getInventoryTrend(period, warehouseId)));
    }

    // ─── 9. Sales Trend Chart ─────────────────────────────────────────────────

    @Operation(summary = "Get sales trend chart data", description = "Returns daily order count, revenue, and items-sold figures for the requested period, "
            +
            "plus a period-over-period comparison showing growth percentages for orders and revenue.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sales trend data returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied — ADMIN or MANAGER role required", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/charts/sales-trend")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponseWpp<DashboardSalesTrendResponse>> getSalesTrend(
            @Parameter(description = "Time window for the chart.", schema = @Schema(implementation = DashboardPeriod.class, defaultValue = "MONTH")) @RequestParam(required = false, defaultValue = "MONTH") DashboardPeriod period) {

        log.debug("GET /dashboard/charts/sales-trend period={}", period);
        return ResponseEntity.ok(ApiResponseWpp.success(dashboardService.getSalesTrend(period)));
    }

    // ─── 10. Top Selling Products Chart ──────────────────────────────────────

    @Operation(summary = "Get top selling products chart data", description = "Returns the top N products ranked by revenue in the requested period. "
            +
            "Each entry includes units sold, revenue, and percentage share of total period revenue. " +
            "Default limit is 10.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Top selling products returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied — ADMIN or MANAGER role required", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    @GetMapping("/charts/top-selling-products")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponseWpp<DashboardTopSellingProductsResponse>> getTopSellingProducts(
            @Parameter(description = "Time window for the chart.", schema = @Schema(implementation = DashboardPeriod.class, defaultValue = "MONTH")) @RequestParam(required = false, defaultValue = "MONTH") DashboardPeriod period,
            @Parameter(description = "Number of top products to return.", example = "10") @RequestParam(required = false, defaultValue = "10") int limit) {

        log.debug("GET /dashboard/charts/top-selling-products period={} limit={}", period, limit);
        return ResponseEntity.ok(ApiResponseWpp.success(
                dashboardService.getTopSellingProducts(period, limit)));
    }
}