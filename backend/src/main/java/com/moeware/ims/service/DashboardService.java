package com.moeware.ims.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.moeware.ims.entity.inventory.InventoryItem;
import com.moeware.ims.enums.DashboardPeriod;
import com.moeware.ims.repository.dashboard.DashboardRepository;
import com.moeware.ims.repository.inventory.InventoryItemRepository;
import com.moeware.ims.repository.staff.WarehouseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for all dashboard aggregation endpoints.
 * <p>
 * All methods are read-only. Raw query execution is fully delegated to
 * {@link DashboardRepository} so this class is responsible only for
 * orchestration and DTO assembly.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

        private static final int TOP_N = 10;

        private final DashboardRepository dashboardRepository;
        private final InventoryItemRepository inventoryItemRepository;
        private final WarehouseRepository warehouseRepository;

        // ─── 1. Overview ─────────────────────────────────────────────────────────

        /**
         * Returns system-wide KPIs: product counts, inventory value, order queues,
         * today's activity summary, alert totals, and revenue figures.
         */
        public DashboardOverviewResponse getOverview() {
                log.debug("Building dashboard overview");

                LocalDate today = LocalDate.now();
                LocalDate weekStart = today.minusDays(6);
                LocalDate monthStart = today.withDayOfMonth(1);
                LocalDate lastMonthStart = monthStart.minusMonths(1);
                LocalDate lastMonthEnd = monthStart.minusDays(1);

                // ── Metrics ──────────────────────────────────────────────────────────
                int totalProducts = dashboardRepository.countActiveProducts();
                BigDecimal inventoryValue = dashboardRepository.totalInventoryValue();
                int lowStock = dashboardRepository.countLowStockProducts();
                int outOfStock = dashboardRepository.countOutOfStockProducts();
                int warehouses = dashboardRepository.countActiveWarehouses();
                int activeUsers = dashboardRepository.countActiveUsers();

                // ── Orders ────────────────────────────────────────────────────────────
                int pendingSO = dashboardRepository.countPendingSalesOrders();
                int confirmedSO = dashboardRepository.countConfirmedSalesOrders();
                int pendingPO = dashboardRepository.countPendingPurchaseOrders();
                int approvedPO = dashboardRepository.countApprovedPurchaseOrders();

                // ── Today's activity ──────────────────────────────────────────────────
                int soToday = dashboardRepository.countSalesOrdersToday(today);
                int poToday = dashboardRepository.countPurchaseOrdersToday(today);
                int shipmentsToday = dashboardRepository.countShipmentsToday(today);
                int receivedToday = dashboardRepository.countPurchaseOrdersReceivedToday(today);

                // ── Alerts ────────────────────────────────────────────────────────────
                int pendingAdj = dashboardRepository.countPendingStockAdjustments();
                int overdueInvoices = dashboardRepository.countOverdueInvoices(today);

                // ── Revenue ───────────────────────────────────────────────────────────
                BigDecimal revenueToday = dashboardRepository.revenueOnDate(today);
                BigDecimal revenueWeek = dashboardRepository.revenueBetween(weekStart, today);
                BigDecimal revenueThisMonth = dashboardRepository.revenueBetween(monthStart, today);
                BigDecimal revenueLastMonth = dashboardRepository.revenueBetween(lastMonthStart, lastMonthEnd);
                double growth = growthPercent(revenueLastMonth, revenueThisMonth);

                return DashboardOverviewResponse.builder()
                                .metrics(DashboardOverviewResponse.MetricsDTO.builder()
                                                .totalProducts(totalProducts)
                                                .totalInventoryValue(inventoryValue)
                                                .lowStockProducts(lowStock)
                                                .outOfStockProducts(outOfStock)
                                                .totalWarehouses(warehouses)
                                                .activeUsers(activeUsers)
                                                .build())
                                .orders(DashboardOverviewResponse.OrdersDTO.builder()
                                                .pendingSalesOrders(pendingSO)
                                                .confirmedSalesOrders(confirmedSO)
                                                .pendingPurchaseOrders(pendingPO)
                                                .approvedPurchaseOrders(approvedPO)
                                                .build())
                                .recentActivity(DashboardOverviewResponse.RecentActivityDTO.builder()
                                                .salesOrdersToday(soToday)
                                                .purchaseOrdersToday(poToday)
                                                .shipmentsToday(shipmentsToday)
                                                .receivedToday(receivedToday)
                                                .build())
                                .alerts(DashboardOverviewResponse.AlertsDTO.builder()
                                                .lowStockAlerts(lowStock)
                                                .pendingApprovals(pendingPO + pendingAdj)
                                                .overdueInvoices(overdueInvoices)
                                                .pendingAdjustments(pendingAdj)
                                                .build())
                                .revenue(DashboardOverviewResponse.RevenueDTO.builder()
                                                .today(revenueToday)
                                                .thisWeek(revenueWeek)
                                                .thisMonth(revenueThisMonth)
                                                .lastMonth(revenueLastMonth)
                                                .growth(growth)
                                                .build())
                                .build();
        }

        // ─── 2. Inventory Summary ─────────────────────────────────────────────────

        /**
         * Returns inventory totals, stock-status distribution, and per-category /
         * per-warehouse breakdowns. Optionally scoped to one warehouse or category.
         */
        public DashboardInventorySummaryResponse getInventorySummary(Long warehouseId, Long categoryId) {
                log.debug("Building inventory summary warehouseId={} categoryId={}", warehouseId, categoryId);

                // Load inventory items (scoped by warehouse if requested)
                List<InventoryItem> allItems = warehouseId != null
                                ? inventoryItemRepository.findByWarehouse(
                                                warehouseRepository.getReferenceById(warehouseId),
                                                Pageable.unpaged()).getContent()
                                : inventoryItemRepository.findAll();

                // Apply optional category filter
                if (categoryId != null) {
                        allItems = allItems.stream()
                                        .filter(i -> categoryId.equals(i.getProduct().getCategory().getId()))
                                        .toList();
                }

                // ── Aggregate per-product totals ──────────────────────────────────────
                Map<Long, Integer> stockByProduct = new LinkedHashMap<>();
                allItems.forEach(i -> stockByProduct.merge(i.getProduct().getId(), i.getQuantity(),
                                (k, v) -> k + v.intValue()));

                int totalProducts = stockByProduct.size();
                int inStock = 0, lowStockCount = 0, outOfStockCount = 0;
                for (Map.Entry<Long, Integer> e : stockByProduct.entrySet()) {
                        int qty = e.getValue();
                        int reorder = reorderLevelFor(allItems, e.getKey());
                        if (qty == 0)
                                outOfStockCount++;
                        else if (qty <= reorder)
                                lowStockCount++;
                        else
                                inStock++;
                }

                BigDecimal costValue = allItems.stream()
                                .map(i -> i.getProduct().getCostPrice()
                                                .multiply(BigDecimal.valueOf(i.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal retailValue = allItems.stream()
                                .map(i -> i.getProduct().getUnitPrice()
                                                .multiply(BigDecimal.valueOf(i.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // ── By category ───────────────────────────────────────────────────────
                Map<Long, CategoryAgg> catMap = new LinkedHashMap<>();
                for (InventoryItem item : allItems) {
                        var cat = item.getProduct().getCategory();
                        CategoryAgg agg = catMap.computeIfAbsent(cat.getId(), id -> new CategoryAgg(id, cat.getName()));
                        agg.productIds.add(item.getProduct().getId());
                        agg.totalValue = agg.totalValue.add(
                                        item.getProduct().getUnitPrice()
                                                        .multiply(BigDecimal.valueOf(item.getQuantity())));
                        if (item.getQuantity() <= item.getProduct().getReorderLevel()) {
                                agg.lowStockCount++;
                        }
                }

                List<DashboardInventorySummaryResponse.CategoryBreakdownDTO> byCategory = catMap.values().stream()
                                .map(a -> DashboardInventorySummaryResponse.CategoryBreakdownDTO.builder()
                                                .categoryId(a.id)
                                                .categoryName(a.name)
                                                .productCount(a.productIds.size())
                                                .totalValue(a.totalValue)
                                                .lowStockCount(a.lowStockCount)
                                                .build())
                                .toList();

                // ── By warehouse ──────────────────────────────────────────────────────
                Map<Long, WarehouseAgg> whMap = new LinkedHashMap<>();
                for (InventoryItem item : allItems) {
                        var wh = item.getWarehouse();
                        double cap = wh.getCapacity() != null ? wh.getCapacity().doubleValue() : 0.0;
                        WarehouseAgg agg = whMap.computeIfAbsent(wh.getId(),
                                        id -> new WarehouseAgg(id, wh.getName(), cap));
                        agg.productIds.add(item.getProduct().getId());
                        agg.totalValue = agg.totalValue.add(
                                        item.getProduct().getUnitPrice()
                                                        .multiply(BigDecimal.valueOf(item.getQuantity())));
                        agg.totalUnits += item.getQuantity();
                }

                List<DashboardInventorySummaryResponse.WarehouseBreakdownDTO> byWarehouse = whMap.values().stream()
                                .map(a -> DashboardInventorySummaryResponse.WarehouseBreakdownDTO.builder()
                                                .warehouseId(a.id)
                                                .warehouseName(a.name)
                                                .productCount(a.productIds.size())
                                                .totalValue(a.totalValue)
                                                .utilization(a.capacity > 0
                                                                ? Math.min(100.0, a.totalUnits / a.capacity * 100)
                                                                : 0.0)
                                                .build())
                                .toList();

                // ── Top products by retail value ───────────────────────────────────────
                List<DashboardInventorySummaryResponse.TopProductDTO> topProducts = allItems.stream()
                                .collect(Collectors.groupingBy(i -> i.getProduct().getId()))
                                .entrySet().stream()
                                .map(e -> {
                                        InventoryItem first = e.getValue().get(0);
                                        int qty = e.getValue().stream().mapToInt(InventoryItem::getQuantity).sum();
                                        return DashboardInventorySummaryResponse.TopProductDTO.builder()
                                                        .productId(first.getProduct().getId())
                                                        .sku(first.getProduct().getSku())
                                                        .name(first.getProduct().getName())
                                                        .totalQuantity(qty)
                                                        .totalValue(first.getProduct().getUnitPrice()
                                                                        .multiply(BigDecimal.valueOf(qty)))
                                                        .build();
                                })
                                .sorted((a, b) -> b.getTotalValue().compareTo(a.getTotalValue()))
                                .limit(TOP_N)
                                .toList();

                return DashboardInventorySummaryResponse.builder()
                                .totalProducts(totalProducts)
                                .totalValue(DashboardInventorySummaryResponse.TotalValueDTO.builder()
                                                .cost(costValue)
                                                .retail(retailValue)
                                                .potentialProfit(retailValue.subtract(costValue))
                                                .build())
                                .stockStatus(DashboardInventorySummaryResponse.StockStatusDTO.builder()
                                                .inStock(inStock)
                                                .lowStock(lowStockCount)
                                                .outOfStock(outOfStockCount)
                                                .build())
                                .byCategory(byCategory)
                                .byWarehouse(byWarehouse)
                                .topProducts(topProducts)
                                .build();
        }

        // ─── 3. Sales Analytics ───────────────────────────────────────────────────

        /**
         * Returns sales performance for the given period or custom date range.
         * Includes totals, status distribution, top products/customers, daily trend,
         * and period-over-period growth.
         */
        public DashboardSalesAnalyticsResponse getSalesAnalytics(
                        DashboardPeriod period, LocalDate startDate, LocalDate endDate) {

                DateRange range = resolve(period, startDate, endDate);
                log.debug("Sales analytics {} → {}", range.start, range.end);

                // ── Daily trend (drives summary totals) ───────────────────────────────
                List<Object[]> trendRows = dashboardRepository.dailySalesTrend(range.start, range.end);

                int totalOrders = trendRows.stream().mapToInt(r -> toLong(r[1]).intValue()).sum();
                BigDecimal totalRevenue = trendRows.stream()
                                .map(r -> (BigDecimal) r[2]).reduce(BigDecimal.ZERO, BigDecimal::add);
                int totalItemsSold = trendRows.stream().mapToInt(r -> toLong(r[3]).intValue()).sum();
                BigDecimal avgOrderValue = totalOrders == 0 ? BigDecimal.ZERO
                                : totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);

                // ── Status map ────────────────────────────────────────────────────────
                Map<String, Integer> byStatus = new LinkedHashMap<>();
                dashboardRepository.salesOrderCountByStatus(range.start, range.end)
                                .forEach(r -> byStatus.put((String) r[0], toLong(r[1]).intValue()));

                // ── Top products ──────────────────────────────────────────────────────
                List<DashboardSalesAnalyticsResponse.TopProductDTO> topProducts = dashboardRepository
                                .topSellingProducts(range.start, range.end, PageRequest.of(0, TOP_N))
                                .stream().map(r -> DashboardSalesAnalyticsResponse.TopProductDTO.builder()
                                                .productId(toLong(r[0]))
                                                .sku((String) r[1])
                                                .name((String) r[2])
                                                .unitsSold(toLong(r[3]).intValue())
                                                .revenue((BigDecimal) r[4])
                                                .build())
                                .toList();

                // ── Top customers ─────────────────────────────────────────────────────
                List<DashboardSalesAnalyticsResponse.TopCustomerDTO> topCustomers = dashboardRepository
                                .topCustomers(range.start, range.end, PageRequest.of(0, TOP_N))
                                .stream().map(r -> DashboardSalesAnalyticsResponse.TopCustomerDTO.builder()
                                                .customerId(toLong(r[0]))
                                                .customerName((String) r[1])
                                                .orderCount(toLong(r[2]).intValue())
                                                .totalSpent((BigDecimal) r[3])
                                                .build())
                                .toList();

                // ── Daily trend DTOs ──────────────────────────────────────────────────
                List<DashboardSalesAnalyticsResponse.DailyTrendDTO> dailyTrend = trendRows.stream()
                                .map(r -> DashboardSalesAnalyticsResponse.DailyTrendDTO.builder()
                                                .date((LocalDate) r[0])
                                                .orders(toLong(r[1]).intValue())
                                                .revenue((BigDecimal) r[2])
                                                .itemsSold(toLong(r[3]).intValue())
                                                .build())
                                .toList();

                // ── Period-over-period growth ─────────────────────────────────────────
                DateRange prev = previous(range);
                List<Object[]> prevRows = dashboardRepository.dailySalesTrend(prev.start, prev.end);
                int prevOrders = prevRows.stream().mapToInt(r -> toLong(r[1]).intValue()).sum();
                BigDecimal prevRevenue = prevRows.stream()
                                .map(r -> (BigDecimal) r[2]).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal prevAvg = prevOrders == 0 ? BigDecimal.ZERO
                                : prevRevenue.divide(BigDecimal.valueOf(prevOrders), 2, RoundingMode.HALF_UP);

                return DashboardSalesAnalyticsResponse.builder()
                                .period(period)
                                .startDate(range.start)
                                .endDate(range.end)
                                .summary(DashboardSalesAnalyticsResponse.SummaryDTO.builder()
                                                .totalOrders(totalOrders)
                                                .totalRevenue(totalRevenue)
                                                .averageOrderValue(avgOrderValue)
                                                .totalItemsSold(totalItemsSold)
                                                .build())
                                .byStatus(byStatus)
                                .topProducts(topProducts)
                                .topCustomers(topCustomers)
                                .dailyTrend(dailyTrend)
                                .growthMetrics(DashboardSalesAnalyticsResponse.GrowthMetricsDTO.builder()
                                                .revenueGrowth(growthPercent(prevRevenue, totalRevenue))
                                                .orderGrowth(growthPercent(BigDecimal.valueOf(prevOrders),
                                                                BigDecimal.valueOf(totalOrders)))
                                                .averageOrderValueGrowth(growthPercent(prevAvg, avgOrderValue))
                                                .build())
                                .build();
        }

        // ─── 4. Purchase Analytics ────────────────────────────────────────────────

        /**
         * Returns purchase-order performance for the given period.
         * Includes PO totals, status distribution, top suppliers, category spend, and
         * monthly trend.
         */
        public DashboardPurchaseAnalyticsResponse getPurchaseAnalytics(
                        DashboardPeriod period, LocalDate startDate, LocalDate endDate) {

                DateRange range = resolve(period, startDate, endDate);
                log.debug("Purchase analytics {} → {}", range.start, range.end);

                // ── Status map (drives totalOrders) ───────────────────────────────────
                Map<String, Integer> byStatus = new LinkedHashMap<>();
                dashboardRepository.purchaseOrderCountByStatus(range.start, range.end)
                                .forEach(r -> byStatus.put((String) r[0], toLong(r[1]).intValue()));

                int totalOrders = byStatus.values().stream().mapToInt(Integer::intValue).sum();
                int totalItemsOrdered = dashboardRepository.totalItemsOrdered(range.start, range.end);

                BigDecimal totalSpent = dashboardRepository.purchaseSpendBetween(range.start, range.end);
                BigDecimal avgOrderValue = totalOrders == 0 ? BigDecimal.ZERO
                                : totalSpent.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);

                // ── Top suppliers ─────────────────────────────────────────────────────
                List<DashboardPurchaseAnalyticsResponse.TopSupplierDTO> topSuppliers = dashboardRepository
                                .topSuppliers(range.start, range.end, PageRequest.of(0, TOP_N))
                                .stream().map(r -> DashboardPurchaseAnalyticsResponse.TopSupplierDTO.builder()
                                                .supplierId(toLong(r[0]))
                                                .supplierName((String) r[1])
                                                .orderCount(toLong(r[2]).intValue())
                                                .totalSpent((BigDecimal) r[3])
                                                .onTimeDeliveryRate(0.0) // extended via supplier-performance endpoint
                                                .build())
                                .toList();

                // ── Category spending ─────────────────────────────────────────────────
                List<DashboardPurchaseAnalyticsResponse.CategorySpendingDTO> categorySpending = dashboardRepository
                                .categorySpending(range.start, range.end)
                                .stream().map(r -> DashboardPurchaseAnalyticsResponse.CategorySpendingDTO.builder()
                                                .categoryId(toLong(r[0]))
                                                .categoryName((String) r[1])
                                                .totalSpent((BigDecimal) r[2])
                                                .orderCount(toLong(r[3]).intValue())
                                                .build())
                                .toList();

                // ── Monthly trend ─────────────────────────────────────────────────────
                List<DashboardPurchaseAnalyticsResponse.MonthlyTrendDTO> monthlyTrend = dashboardRepository
                                .monthlyPurchaseTrend(range.start, range.end)
                                .stream().map(r -> DashboardPurchaseAnalyticsResponse.MonthlyTrendDTO.builder()
                                                .month((String) r[0])
                                                .orders(((Number) r[1]).intValue())
                                                .totalSpent(new BigDecimal(r[2].toString()))
                                                .build())
                                .toList();

                return DashboardPurchaseAnalyticsResponse.builder()
                                .period(period)
                                .startDate(range.start)
                                .endDate(range.end)
                                .summary(DashboardPurchaseAnalyticsResponse.SummaryDTO.builder()
                                                .totalOrders(totalOrders)
                                                .totalSpent(totalSpent)
                                                .averageOrderValue(avgOrderValue)
                                                .totalItemsOrdered(totalItemsOrdered)
                                                .build())
                                .byStatus(byStatus)
                                .topSuppliers(topSuppliers)
                                .categorySpending(categorySpending)
                                .monthlyTrend(monthlyTrend)
                                .build();
        }

        // ─── 5. Low Stock Alerts ──────────────────────────────────────────────────

        /**
         * Returns every active product at or below its reorder level,
         * with per-warehouse stock breakdown and CRITICAL / WARNING severity.
         */
        public DashboardLowStockAlertsResponse getLowStockAlerts() {
                log.debug("Building low stock alerts");

                List<Object[]> rows = dashboardRepository.lowStockProductsWithWarehouseBreakdown();

                // Group rows by productId
                Map<Long, LowStockAgg> aggMap = new LinkedHashMap<>();
                for (Object[] r : rows) {
                        Long productId = toLong(r[0]);
                        LowStockAgg agg = aggMap.computeIfAbsent(productId, id -> {
                                LowStockAgg a = new LowStockAgg();
                                a.productId = id;
                                a.sku = (String) r[1];
                                a.name = (String) r[2];
                                a.reorderLevel = ((Number) r[3]).intValue();
                                a.minStockLevel = ((Number) r[4]).intValue();
                                return a;
                        });
                        int qty = ((Number) r[7]).intValue();
                        agg.totalStock += qty;
                        agg.warehouses.add(DashboardLowStockAlertsResponse.WarehouseStockDTO.builder()
                                        .warehouseId(toLong(r[5]))
                                        .warehouseName((String) r[6])
                                        .quantity(qty)
                                        .build());
                }

                List<DashboardLowStockAlertsResponse.LowStockProductDTO> products = aggMap.values().stream()
                                .map(a -> DashboardLowStockAlertsResponse.LowStockProductDTO.builder()
                                                .productId(a.productId)
                                                .sku(a.sku)
                                                .name(a.name)
                                                .totalStock(a.totalStock)
                                                .reorderLevel(a.reorderLevel)
                                                .shortage(Math.max(0, a.reorderLevel - a.totalStock))
                                                .severity(a.totalStock <= a.minStockLevel ? "CRITICAL" : "WARNING")
                                                .warehouses(a.warehouses)
                                                .build())
                                .toList();

                int criticalCount = (int) products.stream()
                                .filter(p -> "CRITICAL".equals(p.getSeverity())).count();

                return DashboardLowStockAlertsResponse.builder()
                                .totalAlerts(products.size())
                                .criticalAlerts(criticalCount)
                                .products(products)
                                .build();
        }

        // ─── 6. Pending Actions ───────────────────────────────────────────────────

        /**
         * Returns all items requiring immediate attention: PO/adjustment approvals,
         * overdue invoices, pending shipments, and low-stock count.
         */
        public DashboardPendingActionsResponse getPendingActions() {
                log.debug("Building pending actions");

                LocalDate today = LocalDate.now();

                int pendingPO = dashboardRepository.countPendingPurchaseOrders();
                int pendingAdj = dashboardRepository.countPendingStockAdjustments();

                // ── Overdue invoices ───────────────────────────────────────────────────
                List<Object[]> overdueRows = dashboardRepository.overdueInvoiceDetails(today);
                BigDecimal overdueTotal = overdueRows.stream()
                                .map(r -> (BigDecimal) r[2]).reduce(BigDecimal.ZERO, BigDecimal::add);

                // ── Pending shipments ─────────────────────────────────────────────────
                int pendingShipCount = dashboardRepository.countPendingShipments();
                List<Object[]> oldest = dashboardRepository.oldestPendingShipment(PageRequest.of(0, 1));

                DashboardPendingActionsResponse.OldestShipmentDTO oldestShipmentDTO = null;
                if (!oldest.isEmpty()) {
                        Object[] sr = oldest.get(0);
                        LocalDate createdDate = ((LocalDateTime) sr[2]).toLocalDate();
                        int daysWaiting = (int) ChronoUnit.DAYS.between(createdDate, today);
                        oldestShipmentDTO = DashboardPendingActionsResponse.OldestShipmentDTO.builder()
                                        .id(toLong(sr[0]))
                                        .shipmentNumber((String) sr[1])
                                        .daysWaiting(daysWaiting)
                                        .build();
                }

                // ── Action item list ──────────────────────────────────────────────────
                List<DashboardPendingActionsResponse.ActionItemDTO> items = new ArrayList<>();

                // PO approvals (first 5)
                dashboardRepository.pendingPurchaseOrderApprovals(PageRequest.of(0, 5))
                                .forEach(r -> items.add(DashboardPendingActionsResponse.ActionItemDTO.builder()
                                                .type("PURCHASE_ORDER_APPROVAL")
                                                .id(toLong(r[0]))
                                                .title("Purchase Order " + r[1] + " awaiting approval")
                                                .priority("HIGH")
                                                .amount((BigDecimal) r[2])
                                                .createdAt((LocalDateTime) r[3])
                                                .actionUrl("/purchase-orders/" + toLong(r[0]))
                                                .build()));

                // Overdue invoices (first 5)
                overdueRows.stream().limit(5)
                                .forEach(r -> items.add(DashboardPendingActionsResponse.ActionItemDTO.builder()
                                                .type("OVERDUE_INVOICE")
                                                .id(toLong(r[0]))
                                                .title("Invoice " + r[1] + " is overdue")
                                                .priority("CRITICAL")
                                                .amount((BigDecimal) r[2])
                                                .customer((String) r[5])
                                                .createdAt((LocalDateTime) r[6])
                                                .actionUrl("/invoices/" + toLong(r[0]))
                                                .build()));

                return DashboardPendingActionsResponse.builder()
                                .pendingApprovals(DashboardPendingActionsResponse.PendingApprovalsDTO.builder()
                                                .purchaseOrders(pendingPO)
                                                .stockAdjustments(pendingAdj)
                                                .total(pendingPO + pendingAdj)
                                                .build())
                                .overdueInvoices(DashboardPendingActionsResponse.OverdueInvoicesDTO.builder()
                                                .count(overdueRows.size())
                                                .totalAmount(overdueTotal)
                                                .build())
                                .pendingShipments(DashboardPendingActionsResponse.PendingShipmentsDTO.builder()
                                                .count(pendingShipCount)
                                                .oldestShipment(oldestShipmentDTO)
                                                .build())
                                .lowStockAlerts(dashboardRepository.countLowStockProducts())
                                .items(items)
                                .build();
        }

        // ─── 7. Activity Feed ─────────────────────────────────────────────────────

        /**
         * Returns a merged, time-sorted feed of recent system events:
         * sales order changes, PO status updates, and delivered shipments.
         */
        public DashboardActivityFeedResponse getActivityFeed(int limit) {
                log.debug("Building activity feed limit={}", limit);

                int perType = Math.max(1, limit / 3);
                Pageable p = PageRequest.of(0, perType);

                List<DashboardActivityFeedResponse.ActivityDTO> all = new ArrayList<>();

                // Sales orders
                dashboardRepository.recentSalesOrderActivity(p).forEach(r -> {
                        String status = (String) r[2];
                        all.add(DashboardActivityFeedResponse.ActivityDTO.builder()
                                        .id(toLong(r[0]))
                                        .type(soActivityType(status))
                                        .title(soActivityTitle(status))
                                        .description(r[1] + " " + status.toLowerCase() + " by " + r[7])
                                        .user(DashboardActivityFeedResponse.UserSummaryDTO.builder()
                                                        .id(toLong(r[6])).username((String) r[7]).build())
                                        .metadata(DashboardActivityFeedResponse.ActivityMetadataDTO.builder()
                                                        .orderId(toLong(r[0]))
                                                        .orderNumber((String) r[1])
                                                        .customerName((String) r[3])
                                                        .totalAmount((BigDecimal) r[4])
                                                        .build())
                                        .timestamp((LocalDateTime) r[5])
                                        .build());
                });

                // Purchase orders
                dashboardRepository.recentPurchaseOrderActivity(p).forEach(r -> {
                        String status = (String) r[2];
                        all.add(DashboardActivityFeedResponse.ActivityDTO.builder()
                                        .id(toLong(r[0]))
                                        .type(poActivityType(status))
                                        .title(poActivityTitle(status))
                                        .description(r[1] + " " + status.toLowerCase() + " by " + r[7])
                                        .user(DashboardActivityFeedResponse.UserSummaryDTO.builder()
                                                        .id(toLong(r[6])).username((String) r[7]).build())
                                        .metadata(DashboardActivityFeedResponse.ActivityMetadataDTO.builder()
                                                        .orderId(toLong(r[0]))
                                                        .orderNumber((String) r[1])
                                                        .supplierName((String) r[3])
                                                        .totalAmount((BigDecimal) r[4])
                                                        .build())
                                        .timestamp((LocalDateTime) r[5])
                                        .build());
                });

                // Deliveries
                dashboardRepository.recentDeliveries(p)
                                .forEach(r -> all.add(DashboardActivityFeedResponse.ActivityDTO.builder()
                                                .id(toLong(r[0]))
                                                .type("SHIPMENT_DELIVERED")
                                                .title("Shipment delivered")
                                                .description("Shipment " + r[1] + " delivered to customer")
                                                .user(DashboardActivityFeedResponse.UserSummaryDTO.builder()
                                                                .id(toLong(r[4])).username((String) r[5]).build())
                                                .metadata(DashboardActivityFeedResponse.ActivityMetadataDTO.builder()
                                                                .shipmentId(toLong(r[0]))
                                                                .shipmentNumber((String) r[1])
                                                                .customerName((String) r[2])
                                                                .build())
                                                .timestamp((LocalDateTime) r[3])
                                                .build()));

                // Merge, sort newest-first, trim to requested limit
                all.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
                return DashboardActivityFeedResponse.builder()
                                .activities(all.stream().limit(limit).toList())
                                .build();
        }

        // ─── 8. Inventory Trend Chart ─────────────────────────────────────────────

        /**
         * Returns per-day inventory value/count snapshots for the requested period.
         * Uses current snapshot values as a baseline; a full historical reconstruction
         * would require dedicated snapshot tables (see future enhancements).
         */
        public DashboardInventoryTrendResponse getInventoryTrend(DashboardPeriod period, Long warehouseId) {
                log.debug("Building inventory trend period={} warehouseId={}", period, warehouseId);

                DateRange range = resolve(period, null, null);

                // Current totals used as approximation baseline
                BigDecimal currentValue = dashboardRepository.totalInventoryValue();
                int currentProductCount = dashboardRepository.countActiveProducts();
                int currentLowStock = dashboardRepository.currentLowStockCount();

                // Generate one data point per calendar day in the range
                List<DashboardInventoryTrendResponse.DataPointDTO> dataPoints = new ArrayList<>();
                LocalDate cursor = range.start;
                while (!cursor.isAfter(range.end)) {
                        dataPoints.add(DashboardInventoryTrendResponse.DataPointDTO.builder()
                                        .date(cursor)
                                        .totalValue(currentValue)
                                        .productCount(currentProductCount)
                                        .lowStockCount(currentLowStock)
                                        .build());
                        cursor = cursor.plusDays(1);
                }

                return DashboardInventoryTrendResponse.builder()
                                .period(period)
                                .dataPoints(dataPoints)
                                .build();
        }

        // ─── 9. Sales Trend Chart ─────────────────────────────────────────────────

        /**
         * Returns per-day order/revenue data with a period-over-period growth
         * comparison.
         */
        public DashboardSalesTrendResponse getSalesTrend(DashboardPeriod period) {
                log.debug("Building sales trend period={}", period);

                DateRange range = resolve(period, null, null);
                DateRange prev = previous(range);

                List<Object[]> rows = dashboardRepository.dailySalesTrend(range.start, range.end);

                List<DashboardSalesTrendResponse.DataPointDTO> dataPoints = rows.stream()
                                .map(r -> DashboardSalesTrendResponse.DataPointDTO.builder()
                                                .date((LocalDate) r[0])
                                                .orders(toLong(r[1]).intValue())
                                                .revenue((BigDecimal) r[2])
                                                .itemsSold(toLong(r[3]).intValue())
                                                .build())
                                .toList();

                // Previous period comparison
                List<Object[]> prevRows = dashboardRepository.dailySalesTrend(prev.start, prev.end);
                int prevOrders = prevRows.stream().mapToInt(r -> toLong(r[1]).intValue()).sum();
                BigDecimal prevRevenue = prevRows.stream()
                                .map(r -> (BigDecimal) r[2]).reduce(BigDecimal.ZERO, BigDecimal::add);
                int curOrders = rows.stream().mapToInt(r -> toLong(r[1]).intValue()).sum();
                BigDecimal curRevenue = rows.stream()
                                .map(r -> (BigDecimal) r[2]).reduce(BigDecimal.ZERO, BigDecimal::add);

                return DashboardSalesTrendResponse.builder()
                                .period(period)
                                .dataPoints(dataPoints)
                                .comparison(DashboardSalesTrendResponse.ComparisonDTO.builder()
                                                .previousPeriod(DashboardSalesTrendResponse.PreviousPeriodDTO.builder()
                                                                .totalOrders(prevOrders)
                                                                .totalRevenue(prevRevenue)
                                                                .build())
                                                .growth(DashboardSalesTrendResponse.GrowthDTO.builder()
                                                                .ordersGrowth(growthPercent(
                                                                                BigDecimal.valueOf(prevOrders),
                                                                                BigDecimal.valueOf(curOrders)))
                                                                .revenueGrowth(growthPercent(prevRevenue, curRevenue))
                                                                .build())
                                                .build())
                                .build();
        }

        // ─── 10. Top Selling Products Chart ──────────────────────────────────────

        /**
         * Returns the top N products by revenue with their percentage share of total
         * period revenue.
         */
        public DashboardTopSellingProductsResponse getTopSellingProducts(DashboardPeriod period, int limit) {
                log.debug("Building top selling products period={} limit={}", period, limit);

                DateRange range = resolve(period, null, null);
                List<Object[]> rows = dashboardRepository.topSellingProducts(
                                range.start, range.end, PageRequest.of(0, limit));

                BigDecimal totalRevenue = rows.stream()
                                .map(r -> (BigDecimal) r[4]).reduce(BigDecimal.ZERO, BigDecimal::add);

                List<DashboardTopSellingProductsResponse.TopProductDTO> products = rows.stream()
                                .map(r -> {
                                        BigDecimal rev = (BigDecimal) r[4];
                                        double pct = totalRevenue.compareTo(BigDecimal.ZERO) == 0 ? 0.0
                                                        : rev.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                                                                        .multiply(BigDecimal.valueOf(100))
                                                                        .doubleValue();
                                        return DashboardTopSellingProductsResponse.TopProductDTO.builder()
                                                        .productId(toLong(r[0]))
                                                        .sku((String) r[1])
                                                        .name((String) r[2])
                                                        .unitsSold(toLong(r[3]).intValue())
                                                        .revenue(rev)
                                                        .percentage(pct)
                                                        .build();
                                })
                                .toList();

                return DashboardTopSellingProductsResponse.builder()
                                .period(period)
                                .products(products)
                                .build();
        }

        // ─── Private helpers ──────────────────────────────────────────────────────

        /**
         * Resolves a period enum + optional custom dates into a concrete date range.
         */
        private DateRange resolve(DashboardPeriod period, LocalDate startDate, LocalDate endDate) {
                if (startDate != null && endDate != null) {
                        return new DateRange(startDate, endDate);
                }
                LocalDate today = LocalDate.now();
                return switch (period == null ? DashboardPeriod.MONTH : period) {
                        case TODAY -> new DateRange(today, today);
                        case WEEK -> new DateRange(today.minusDays(6), today);
                        case MONTH -> new DateRange(today.withDayOfMonth(1), today);
                        case QUARTER -> new DateRange(today.minusMonths(3).withDayOfMonth(1), today);
                        case YEAR -> new DateRange(today.withDayOfYear(1), today);
                };
        }

        /** Returns the equal-length period immediately before the given range. */
        private DateRange previous(DateRange current) {
                long days = ChronoUnit.DAYS.between(current.start, current.end) + 1;
                return new DateRange(current.start.minusDays(days), current.start.minusDays(1));
        }

        /** Period-over-period growth percentage; returns 0.0 when previous is zero. */
        private double growthPercent(BigDecimal previous, BigDecimal current) {
                if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0)
                        return 0.0;
                return current.subtract(previous)
                                .divide(previous, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                                .doubleValue();
        }

        /**
         * Safely reads the reorder level for a product from the already-loaded item
         * list.
         */
        private int reorderLevelFor(List<InventoryItem> items, Long productId) {
                return items.stream()
                                .filter(i -> i.getProduct().getId().equals(productId))
                                .findFirst()
                                .map(i -> i.getProduct().getReorderLevel())
                                .orElse(0);
        }

        /** Null-safe Number → Long cast from JPQL COUNT / SUM results. */
        private Long toLong(Object o) {
                return o == null ? 0L : ((Number) o).longValue();
        }

        private String soActivityType(String status) {
                return "PENDING".equals(status) ? "SALES_ORDER_CREATED" : "SALES_ORDER_UPDATED";
        }

        private String soActivityTitle(String status) {
                return switch (status) {
                        case "PENDING" -> "New sales order created";
                        case "CONFIRMED" -> "Sales order confirmed";
                        case "FULFILLED" -> "Sales order fulfilled";
                        case "SHIPPED" -> "Sales order shipped";
                        case "DELIVERED" -> "Sales order delivered";
                        case "CANCELLED" -> "Sales order cancelled";
                        default -> "Sales order updated";
                };
        }

        private String poActivityType(String status) {
                return "APPROVED".equals(status) ? "PURCHASE_ORDER_APPROVED" : "PURCHASE_ORDER_UPDATED";
        }

        private String poActivityTitle(String status) {
                return switch (status) {
                        case "DRAFT" -> "Purchase order created";
                        case "SUBMITTED" -> "Purchase order submitted for approval";
                        case "APPROVED" -> "Purchase order approved";
                        case "RECEIVED" -> "Purchase order received";
                        case "CANCELLED" -> "Purchase order cancelled";
                        default -> "Purchase order updated";
                };
        }

        // ─── Private value types ──────────────────────────────────────────────────

        private record DateRange(LocalDate start, LocalDate end) {
        }

        private static class CategoryAgg {
                final Long id;
                final String name;
                final Set<Long> productIds = new HashSet<>();
                BigDecimal totalValue = BigDecimal.ZERO;
                int lowStockCount;

                CategoryAgg(Long id, String name) {
                        this.id = id;
                        this.name = name;
                }
        }

        private static class WarehouseAgg {
                final Long id;
                final String name;
                final double capacity;
                final Set<Long> productIds = new HashSet<>();
                BigDecimal totalValue = BigDecimal.ZERO;
                int totalUnits;

                WarehouseAgg(Long id, String name, double capacity) {
                        this.id = id;
                        this.name = name;
                        this.capacity = capacity;
                }
        }

        private static class LowStockAgg {
                Long productId;
                String sku;
                String name;
                int reorderLevel;
                int minStockLevel;
                int totalStock;
                final List<DashboardLowStockAlertsResponse.WarehouseStockDTO> warehouses = new ArrayList<>();
        }
}