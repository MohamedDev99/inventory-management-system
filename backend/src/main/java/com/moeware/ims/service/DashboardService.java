package com.moeware.ims.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
import com.moeware.ims.enums.dashboard.DashboardActivityType;
import com.moeware.ims.enums.dashboard.DashboardPeriod;
import com.moeware.ims.repository.dashboard.DashboardRepository;
import com.moeware.ims.repository.dashboard.DashboardRepository.DailySalesTrendRow;
import com.moeware.ims.repository.dashboard.DashboardRepository.LowStockWarehouseRow;
import com.moeware.ims.repository.dashboard.DashboardRepository.OverdueInvoiceRow;
import com.moeware.ims.repository.dashboard.DashboardRepository.PendingPoApprovalRow;
import com.moeware.ims.repository.dashboard.DashboardRepository.PendingShipmentRow;
import com.moeware.ims.repository.dashboard.DashboardRepository.TopProductRow;
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
 * <p>
 * The service accesses query results through the named projection records
 * defined in {@link DashboardRepository} (e.g. {@link DailySalesTrendRow},
 * {@link TopProductRow}) — never through positional {@code Object[]} array
 * access.
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

                int totalProducts = dashboardRepository.countActiveProducts();
                BigDecimal inventoryValue = dashboardRepository.totalInventoryValue();
                int lowStock = dashboardRepository.countLowStockProducts();
                int outOfStock = dashboardRepository.countOutOfStockProducts();
                int warehouses = dashboardRepository.countActiveWarehouses();
                int activeUsers = dashboardRepository.countActiveUsers();

                int pendingSO = dashboardRepository.countPendingSalesOrders();
                int confirmedSO = dashboardRepository.countConfirmedSalesOrders();
                int pendingPO = dashboardRepository.countPendingPurchaseOrders();
                int approvedPO = dashboardRepository.countApprovedPurchaseOrders();

                int soToday = dashboardRepository.countSalesOrdersToday(today);
                int poToday = dashboardRepository.countPurchaseOrdersToday(today);
                int shipmentsToday = dashboardRepository.countShipmentsToday(today);
                int receivedToday = dashboardRepository.countPurchaseOrdersReceivedToday(today);

                int pendingAdj = dashboardRepository.countPendingStockAdjustments();
                int overdueInvoices = dashboardRepository.countOverdueInvoices(today);

                BigDecimal revenueToday = dashboardRepository.revenueOnDate(today);
                BigDecimal revenueWeek = dashboardRepository.revenueBetween(weekStart, today);
                BigDecimal revenueThisMonth = dashboardRepository.revenueBetween(monthStart, today);
                BigDecimal revenueLastMonth = dashboardRepository.revenueBetween(lastMonthStart, lastMonthEnd);

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
                                                .growth(growthPercent(revenueLastMonth, revenueThisMonth))
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

                List<InventoryItem> allItems = warehouseId != null
                                ? inventoryItemRepository.findByWarehouse(
                                                warehouseRepository.getReferenceById(warehouseId),
                                                Pageable.unpaged()).getContent()
                                : inventoryItemRepository.findAll();

                if (categoryId != null) {
                        allItems = allItems.stream()
                                        .filter(i -> categoryId.equals(i.getProduct().getCategory().getId()))
                                        .toList();
                }

                Map<Long, Integer> stockByProduct = new LinkedHashMap<>();
                allItems.forEach(i -> stockByProduct.merge(i.getProduct().getId(), i.getQuantity(), Integer::sum));

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
                                .map(i -> i.getProduct().getCostPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal retailValue = allItems.stream()
                                .map(i -> i.getProduct().getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // By category
                Map<Long, CategoryAgg> catMap = new LinkedHashMap<>();
                for (InventoryItem item : allItems) {
                        var cat = item.getProduct().getCategory();
                        CategoryAgg agg = catMap.computeIfAbsent(cat.getId(), id -> new CategoryAgg(id, cat.getName()));
                        agg.productIds.add(item.getProduct().getId());
                        agg.totalValue = agg.totalValue.add(
                                        item.getProduct().getUnitPrice()
                                                        .multiply(BigDecimal.valueOf(item.getQuantity())));
                        if (item.getQuantity() <= item.getProduct().getReorderLevel())
                                agg.lowStockCount++;
                }

                List<DashboardInventorySummaryResponse.CategoryBreakdownDTO> byCategory = catMap.values().stream()
                                .map(a -> DashboardInventorySummaryResponse.CategoryBreakdownDTO.builder()
                                                .categoryId(a.id).categoryName(a.name)
                                                .productCount(a.productIds.size()).totalValue(a.totalValue)
                                                .lowStockCount(a.lowStockCount).build())
                                .toList();

                // By warehouse
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
                                                .warehouseId(a.id).warehouseName(a.name)
                                                .productCount(a.productIds.size())
                                                .totalValue(a.totalValue)
                                                .utilization(a.capacity > 0
                                                                ? Math.min(100.0, a.totalUnits / a.capacity * 100)
                                                                : 0.0)
                                                .build())
                                .toList();

                // Top products by retail value
                List<DashboardInventorySummaryResponse.TopProductDTO> topProducts = allItems.stream()
                                .collect(Collectors.groupingBy(i -> i.getProduct().getId()))
                                .entrySet().stream()
                                .map(e -> {
                                        InventoryItem first = e.getValue().get(0);
                                        int qty = e.getValue().stream().mapToInt(InventoryItem::getQuantity).sum();
                                        return DashboardInventorySummaryResponse.TopProductDTO.builder()
                                                        .productId(first.getProduct().getId())
                                                        .sku(first.getProduct().getSku())
                                                        .name(first.getProduct().getName()).totalQuantity(qty)
                                                        .totalValue(first.getProduct().getUnitPrice()
                                                                        .multiply(BigDecimal.valueOf(qty)))
                                                        .build();
                                })
                                .sorted((a, b) -> b.getTotalValue().compareTo(a.getTotalValue()))
                                .limit(TOP_N).toList();

                return DashboardInventorySummaryResponse.builder()
                                .totalProducts(totalProducts)
                                .totalValue(DashboardInventorySummaryResponse.TotalValueDTO.builder()
                                                .cost(costValue).retail(retailValue)
                                                .potentialProfit(retailValue.subtract(costValue)).build())
                                .stockStatus(DashboardInventorySummaryResponse.StockStatusDTO.builder()
                                                .inStock(inStock).lowStock(lowStockCount).outOfStock(outOfStockCount)
                                                .build())
                                .byCategory(byCategory).byWarehouse(byWarehouse).topProducts(topProducts)
                                .build();
        }

        // ─── 3. Sales Analytics ───────────────────────────────────────────────────

        /**
         * Returns sales performance for the given period or custom date range.
         */
        public DashboardSalesAnalyticsResponse getSalesAnalytics(
                        DashboardPeriod period, LocalDate startDate, LocalDate endDate) {

                DateRange range = resolve(period, startDate, endDate);
                log.debug("Sales analytics {} → {}", range.start, range.end);

                // Daily trend drives summary totals
                List<DailySalesTrendRow> trendRows = fillMissingDates(
                                dashboardRepository.dailySalesTrend(range.start, range.end),
                                range.start, range.end);

                int totalOrders = trendRows.stream().mapToInt(r -> (int) r.orderCount()).sum();
                BigDecimal totalRevenue = trendRows.stream()
                                .map(DailySalesTrendRow::revenue).reduce(BigDecimal.ZERO, BigDecimal::add);
                int totalItemsSold = trendRows.stream().mapToInt(r -> (int) r.itemsSold()).sum();
                BigDecimal avgOrderValue = totalOrders == 0 ? BigDecimal.ZERO
                                : totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);

                // Status map
                Map<String, Integer> byStatus = new LinkedHashMap<>();
                dashboardRepository.salesOrderCountByStatus(range.start, range.end)
                                .forEach(r -> byStatus.put(r.status(), (int) r.count()));

                // Top products
                List<DashboardSalesAnalyticsResponse.TopProductDTO> topProducts = dashboardRepository
                                .topSellingProducts(range.start, range.end, PageRequest.of(0, TOP_N))
                                .stream().map(r -> DashboardSalesAnalyticsResponse.TopProductDTO.builder()
                                                .productId(r.productId()).sku(r.sku()).name(r.name())
                                                .unitsSold((int) r.unitsSold()).revenue(r.revenue())
                                                .build())
                                .toList();

                // Top customers
                List<DashboardSalesAnalyticsResponse.TopCustomerDTO> topCustomers = dashboardRepository
                                .topCustomers(range.start, range.end, PageRequest.of(0, TOP_N))
                                .stream().map(r -> DashboardSalesAnalyticsResponse.TopCustomerDTO.builder()
                                                .customerId(r.customerId()).customerName(r.contactName())
                                                .orderCount((int) r.orderCount()).totalSpent(r.totalSpent())
                                                .build())
                                .toList();

                // Daily trend DTOs
                List<DashboardSalesAnalyticsResponse.DailyTrendDTO> dailyTrend = trendRows.stream()
                                .map(r -> DashboardSalesAnalyticsResponse.DailyTrendDTO.builder()
                                                .date(r.orderDate()).orders((int) r.orderCount())
                                                .revenue(r.revenue()).itemsSold((int) r.itemsSold())
                                                .build())
                                .toList();

                // Period-over-period growth
                DateRange prev = previous(range);

                List<DailySalesTrendRow> prevRows = fillMissingDates(
                                dashboardRepository.dailySalesTrend(prev.start, prev.end),
                                prev.start, prev.end);

                int prevOrders = prevRows.stream().mapToInt(r -> (int) r.orderCount()).sum();
                BigDecimal prevRevenue = prevRows.stream()
                                .map(DailySalesTrendRow::revenue).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal prevAvg = prevOrders == 0 ? BigDecimal.ZERO
                                : prevRevenue.divide(BigDecimal.valueOf(prevOrders), 2, RoundingMode.HALF_UP);

                return DashboardSalesAnalyticsResponse.builder()
                                .period(period).startDate(range.start).endDate(range.end)
                                .summary(DashboardSalesAnalyticsResponse.SummaryDTO.builder()
                                                .totalOrders(totalOrders).totalRevenue(totalRevenue)
                                                .averageOrderValue(avgOrderValue).totalItemsSold(totalItemsSold)
                                                .build())
                                .byStatus(byStatus).topProducts(topProducts).topCustomers(topCustomers)
                                .dailyTrend(dailyTrend)
                                .growthMetrics(DashboardSalesAnalyticsResponse.GrowthMetricsDTO.builder()
                                                .revenueGrowth(growthPercent(prevRevenue, totalRevenue))
                                                .orderGrowth(growthPercent(
                                                                BigDecimal.valueOf(prevOrders),
                                                                BigDecimal.valueOf(totalOrders)))
                                                .averageOrderValueGrowth(growthPercent(prevAvg, avgOrderValue))
                                                .build())
                                .build();
        }

        // ─── 4. Purchase Analytics ────────────────────────────────────────────────

        /**
         * Returns purchase-order performance for the given period.
         */
        public DashboardPurchaseAnalyticsResponse getPurchaseAnalytics(
                        DashboardPeriod period, LocalDate startDate, LocalDate endDate) {

                DateRange range = resolve(period, startDate, endDate);
                log.debug("Purchase analytics {} → {}", range.start, range.end);

                Map<String, Integer> byStatus = new LinkedHashMap<>();
                dashboardRepository.purchaseOrderCountByStatus(range.start, range.end)
                                .forEach(r -> byStatus.put(r.status(), (int) r.count()));

                int totalOrders = byStatus.values().stream().mapToInt(Integer::intValue).sum();
                int totalItemsOrdered = dashboardRepository.totalItemsOrdered(range.start, range.end);
                BigDecimal totalSpent = dashboardRepository.purchaseSpendBetween(range.start, range.end);
                BigDecimal avgOrderValue = totalOrders == 0 ? BigDecimal.ZERO
                                : totalSpent.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);

                List<DashboardPurchaseAnalyticsResponse.TopSupplierDTO> topSuppliers = dashboardRepository
                                .topSuppliers(range.start, range.end, PageRequest.of(0, TOP_N))
                                .stream().map(r -> DashboardPurchaseAnalyticsResponse.TopSupplierDTO.builder()
                                                .supplierId(r.supplierId()).supplierName(r.supplierName())
                                                .orderCount((int) r.orderCount()).totalSpent(r.totalSpent())
                                                .onTimeDeliveryRate(0.0)
                                                .build())
                                .toList();

                List<DashboardPurchaseAnalyticsResponse.CategorySpendingDTO> categorySpending = dashboardRepository
                                .categorySpending(range.start, range.end)
                                .stream().map(r -> DashboardPurchaseAnalyticsResponse.CategorySpendingDTO.builder()
                                                .categoryId(r.categoryId()).categoryName(r.categoryName())
                                                .totalSpent(r.totalSpent()).orderCount((int) r.orderCount())
                                                .build())
                                .toList();

                List<DashboardPurchaseAnalyticsResponse.MonthlyTrendDTO> monthlyTrend = dashboardRepository
                                .monthlyPurchaseTrend(range.start, range.end)
                                .stream().map(r -> DashboardPurchaseAnalyticsResponse.MonthlyTrendDTO.builder()
                                                .month(r.month()).orders((int) r.orderCount())
                                                .totalSpent(r.totalSpent())
                                                .build())
                                .toList();

                return DashboardPurchaseAnalyticsResponse.builder()
                                .period(period).startDate(range.start).endDate(range.end)
                                .summary(DashboardPurchaseAnalyticsResponse.SummaryDTO.builder()
                                                .totalOrders(totalOrders).totalSpent(totalSpent)
                                                .averageOrderValue(avgOrderValue).totalItemsOrdered(totalItemsOrdered)
                                                .build())
                                .byStatus(byStatus).topSuppliers(topSuppliers)
                                .categorySpending(categorySpending).monthlyTrend(monthlyTrend)
                                .build();
        }

        // ─── 5. Low Stock Alerts ──────────────────────────────────────────────────

        /**
         * Returns every active product at or below its reorder level with per-warehouse
         * stock breakdown and CRITICAL / WARNING severity.
         */
        public DashboardLowStockAlertsResponse getLowStockAlerts() {
                log.debug("Building low stock alerts");

                List<LowStockWarehouseRow> rows = dashboardRepository.lowStockProductsWithWarehouseBreakdown();

                // Group rows by productId using a stable insertion-order map
                Map<Long, LowStockAgg> aggMap = new LinkedHashMap<>();
                for (LowStockWarehouseRow row : rows) {
                        LowStockAgg agg = aggMap.computeIfAbsent(row.productId(), id -> {
                                LowStockAgg a = new LowStockAgg();
                                a.productId = id;
                                a.sku = row.sku();
                                a.name = row.name();
                                a.reorderLevel = row.reorderLevel();
                                a.minStockLevel = row.minStockLevel();
                                return a;
                        });
                        agg.totalStock += row.warehouseQty();
                        agg.warehouses.add(DashboardLowStockAlertsResponse.WarehouseStockDTO.builder()
                                        .warehouseId(row.warehouseId())
                                        .warehouseName(row.warehouseName())
                                        .quantity(row.warehouseQty())
                                        .build());
                }

                List<DashboardLowStockAlertsResponse.LowStockProductDTO> products = aggMap.values().stream()
                                .map(a -> DashboardLowStockAlertsResponse.LowStockProductDTO.builder()
                                                .productId(a.productId).sku(a.sku).name(a.name)
                                                .totalStock(a.totalStock).reorderLevel(a.reorderLevel)
                                                .shortage(Math.max(0, a.reorderLevel - a.totalStock))
                                                .severity(a.totalStock <= a.minStockLevel ? "CRITICAL" : "WARNING")
                                                .warehouses(a.warehouses)
                                                .build())
                                .toList();

                int criticalCount = (int) products.stream()
                                .filter(p -> "CRITICAL".equals(p.getSeverity())).count();

                return DashboardLowStockAlertsResponse.builder()
                                .totalAlerts(products.size()).criticalAlerts(criticalCount).products(products)
                                .build();
        }

        // ─── 6. Pending Actions ───────────────────────────────────────────────────

        /**
         * Returns all items requiring immediate attention.
         */
        public DashboardPendingActionsResponse getPendingActions() {
                log.debug("Building pending actions");

                LocalDate today = LocalDate.now();

                int pendingPO = dashboardRepository.countPendingPurchaseOrders();
                int pendingAdj = dashboardRepository.countPendingStockAdjustments();

                List<OverdueInvoiceRow> overdueRows = dashboardRepository.overdueInvoiceDetails(today);
                BigDecimal overdueTotal = overdueRows.stream()
                                .map(OverdueInvoiceRow::balanceDue).reduce(BigDecimal.ZERO, BigDecimal::add);

                int pendingShipCount = dashboardRepository.countPendingShipments();
                List<PendingShipmentRow> oldest = dashboardRepository.oldestPendingShipment(PageRequest.of(0, 1));

                DashboardPendingActionsResponse.OldestShipmentDTO oldestShipmentDTO = null;
                if (!oldest.isEmpty()) {
                        PendingShipmentRow sr = oldest.get(0);
                        int daysWaiting = (int) ChronoUnit.DAYS.between(sr.createdAt().toLocalDate(), today);
                        oldestShipmentDTO = DashboardPendingActionsResponse.OldestShipmentDTO.builder()
                                        .id(sr.id()).shipmentNumber(sr.shipmentNumber()).daysWaiting(daysWaiting)
                                        .build();
                }

                List<DashboardPendingActionsResponse.ActionItemDTO> items = new ArrayList<>();

                // PO approvals (first 5) — named record access, no r[n]
                List<PendingPoApprovalRow> approvals = dashboardRepository
                                .pendingPurchaseOrderApprovals(PageRequest.of(0, 5));
                approvals.forEach(r -> items.add(DashboardPendingActionsResponse.ActionItemDTO.builder()
                                .type("PURCHASE_ORDER_APPROVAL")
                                .id(r.poId())
                                .title("Purchase Order " + r.poNumber() + " awaiting approval")
                                .priority("HIGH")
                                .amount(r.totalAmount())
                                .createdAt(r.createdAt())
                                .actionUrl("/purchase-orders/" + r.poId())
                                .build()));

                // Overdue invoices (first 5) — named record access, no r[n]
                overdueRows.stream().limit(5).forEach(r -> items.add(
                                DashboardPendingActionsResponse.ActionItemDTO.builder()
                                                .type("OVERDUE_INVOICE")
                                                .id(r.invoiceId())
                                                .title("Invoice " + r.invoiceNumber() + " is overdue")
                                                .priority("CRITICAL")
                                                .amount(r.balanceDue())
                                                .customer(r.contactName())
                                                .createdAt(r.createdAt())
                                                .actionUrl("/invoices/" + r.invoiceId())
                                                .build()));

                return DashboardPendingActionsResponse.builder()
                                .pendingApprovals(DashboardPendingActionsResponse.PendingApprovalsDTO.builder()
                                                .purchaseOrders(pendingPO).stockAdjustments(pendingAdj)
                                                .total(pendingPO + pendingAdj).build())
                                .overdueInvoices(DashboardPendingActionsResponse.OverdueInvoicesDTO.builder()
                                                .count(overdueRows.size()).totalAmount(overdueTotal).build())
                                .pendingShipments(DashboardPendingActionsResponse.PendingShipmentsDTO.builder()
                                                .count(pendingShipCount).oldestShipment(oldestShipmentDTO).build())
                                .lowStockAlerts(dashboardRepository.countLowStockProducts())
                                .items(items)
                                .build();
        }

        // ─── 7. Activity Feed ─────────────────────────────────────────────────────

        /**
         * Returns a merged, time-sorted feed of recent system events.
         */
        public DashboardActivityFeedResponse getActivityFeed(int limit) {
                log.debug("Building activity feed limit={}", limit);

                Pageable p = PageRequest.of(0, limit);

                List<DashboardActivityFeedResponse.ActivityDTO> all = new ArrayList<>();

                // Sales orders — named record fields
                dashboardRepository.recentSalesOrderActivity(p)
                                .forEach(r -> all.add(DashboardActivityFeedResponse.ActivityDTO.builder()
                                                .id(r.soId())
                                                .type(soActivityType(r.status()))
                                                .title(soActivityTitle(r.status()))
                                                .description(r.soNumber() + " " + r.status().toLowerCase() + " by "
                                                                + r.username())
                                                .user(DashboardActivityFeedResponse.UserSummaryDTO.builder()
                                                                .id(r.userId()).username(r.username()).build())
                                                .metadata(DashboardActivityFeedResponse.ActivityMetadataDTO.builder()
                                                                .orderId(r.soId()).orderNumber(r.soNumber())
                                                                .customerName(r.customerName())
                                                                .totalAmount(r.totalAmount()).build())
                                                .timestamp(r.createdAt())
                                                .build()));

                // Purchase orders — named record fields
                dashboardRepository.recentPurchaseOrderActivity(p)
                                .forEach(r -> all.add(DashboardActivityFeedResponse.ActivityDTO.builder()
                                                .id(r.poId())
                                                .type(poActivityType(r.status()))
                                                .title(poActivityTitle(r.status()))
                                                .description(r.poNumber() + " " + r.status().toLowerCase() + " by "
                                                                + r.username())
                                                .user(DashboardActivityFeedResponse.UserSummaryDTO.builder()
                                                                .id(r.userId()).username(r.username()).build())
                                                .metadata(DashboardActivityFeedResponse.ActivityMetadataDTO.builder()
                                                                .orderId(r.poId()).orderNumber(r.poNumber())
                                                                .supplierName(r.supplierName())
                                                                .totalAmount(r.totalAmount()).build())
                                                .timestamp(r.updatedAt())
                                                .build()));

                // Deliveries — named record fields
                dashboardRepository.recentDeliveries(p).forEach(r -> all.add(DashboardActivityFeedResponse.ActivityDTO
                                .builder()
                                .id(r.shipmentId())
                                .type(DashboardActivityType.SHIPMENT_DELIVERED)
                                .title("Shipment delivered")
                                .description("Shipment " + r.shipmentNumber() + " delivered to customer")
                                .user(DashboardActivityFeedResponse.UserSummaryDTO.builder()
                                                .id(r.userId()).username(r.username()).build())
                                .metadata(DashboardActivityFeedResponse.ActivityMetadataDTO.builder()
                                                .shipmentId(r.shipmentId()).shipmentNumber(r.shipmentNumber())
                                                .customerName(r.customerName()).build())
                                .timestamp(r.updatedAt())
                                .build()));

                all.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
                return DashboardActivityFeedResponse.builder()
                                .activities(all.stream().limit(limit).toList())
                                .build();
        }

        // ─── 8. Inventory Trend Chart ─────────────────────────────────────────────

        /**
         * Returns per-day inventory value/count snapshots for the requested period.
         */

        /**
         * Returns per-day inventory value/count snapshots for the requested period.
         * <p>
         * The current total inventory value is used as the baseline for the last day
         * in the range. Each prior day's value is back-calculated by subtracting the
         * net movement that occurred on the following day, walking backwards from
         * today. This gives a real trend line rather than a flat synthetic series.
         * <p>
         * When {@code warehouseId} is supplied, only inventory from that warehouse
         * contributes to the per-day values.
         */
        public DashboardInventoryTrendResponse getInventoryTrend(DashboardPeriod period, Long warehouseId) {
                log.debug("Building inventory trend period={} warehouseId={}", period, warehouseId);

                DateRange range = resolve(period, null, null);

                // Current snapshot — scoped to warehouse when requested
                BigDecimal currentValue = warehouseId != null
                                ? inventoryItemRepository.calculateTotalValueByWarehouse(warehouseId) != null
                                                ? BigDecimal.valueOf(inventoryItemRepository
                                                                .calculateTotalValueByWarehouse(warehouseId))
                                                : BigDecimal.ZERO
                                : dashboardRepository.totalInventoryValue();

                int currentProductCount = warehouseId != null
                                ? (int) inventoryItemRepository.findByWarehouse(
                                                warehouseRepository.getReferenceById(warehouseId),
                                                Pageable.unpaged()).stream()
                                                .map(ii -> ii.getProduct().getId()).distinct().count()
                                : dashboardRepository.countActiveProducts();

                int currentLowStock = dashboardRepository.currentLowStockCount();

                // Build a date → netChange map from movement records
                // netChange > 0 means stock went up that day; < 0 means stock went down
                java.time.LocalDateTime rangeStart = range.start.atStartOfDay();
                java.time.LocalDateTime rangeEnd = range.end.atTime(23, 59, 59);

                Map<LocalDate, Long> netChangeByDate = new java.util.LinkedHashMap<>();
                dashboardRepository.dailyInventoryNetChange(rangeStart, rangeEnd)
                                .forEach(r -> netChangeByDate.put(
                                                // r[0] is a java.sql.Date from native query — convert safely
                                                ((java.sql.Date) r[0]).toLocalDate(),
                                                r[1] == null ? 0L : ((Number) r[1]).longValue()));

                // Build dates list in ascending order
                List<LocalDate> dates = new ArrayList<>();
                LocalDate cursor = range.start;
                while (!cursor.isAfter(range.end)) {
                        dates.add(cursor);
                        cursor = cursor.plusDays(1);
                }

                // Walk backwards from currentValue to assign per-day values.
                // value[today] = currentValue
                // value[day-1] = value[day] - netChange[day] (undo the change that happened on
                // "day")
                Map<LocalDate, BigDecimal> valueByDate = new java.util.LinkedHashMap<>();
                BigDecimal runningValue = currentValue;
                for (int i = dates.size() - 1; i >= 0; i--) {
                        LocalDate date = dates.get(i);
                        valueByDate.put(date, runningValue);
                        long netChange = netChangeByDate.getOrDefault(date, 0L);
                        // Approximate: treat each unit as average unit price to convert quantity to
                        // value.
                        // Since we don't have price-history, this is the best approximation without
                        // dedicated snapshot tables. The sign: going backward means undoing that day's
                        // net.
                        BigDecimal avgUnitPrice = currentProductCount > 0 && currentValue.compareTo(BigDecimal.ZERO) > 0
                                        ? currentValue.divide(
                                                        BigDecimal.valueOf(Math.max(1,
                                                                        inventoryItemRepository
                                                                                        .countTotalInventoryItems())),
                                                        2, java.math.RoundingMode.HALF_UP)
                                        : BigDecimal.ZERO;
                        runningValue = runningValue.subtract(
                                        avgUnitPrice.multiply(BigDecimal.valueOf(netChange)));
                        if (runningValue.compareTo(BigDecimal.ZERO) < 0) {
                                runningValue = BigDecimal.ZERO;
                        }
                }

                List<DashboardInventoryTrendResponse.DataPointDTO> dataPoints = dates.stream()
                                .map(date -> DashboardInventoryTrendResponse.DataPointDTO.builder()
                                                .date(date)
                                                .totalValue(valueByDate.getOrDefault(date, BigDecimal.ZERO))
                                                .productCount(currentProductCount)
                                                .lowStockCount(currentLowStock)
                                                .build())
                                .toList();

                return DashboardInventoryTrendResponse.builder().period(period).dataPoints(dataPoints).build();
        }

        // ─── 9. Sales Trend Chart ─────────────────────────────────────────────────

        /**
         * Returns per-day sales data with period-over-period growth comparison.
         */
        public DashboardSalesTrendResponse getSalesTrend(DashboardPeriod period) {
                log.debug("Building sales trend period={}", period);

                DateRange range = resolve(period, null, null);
                DateRange prev = previous(range);

                List<DailySalesTrendRow> rows = dashboardRepository.dailySalesTrend(range.start, range.end);

                List<DashboardSalesTrendResponse.DataPointDTO> dataPoints = rows.stream()
                                .map(r -> DashboardSalesTrendResponse.DataPointDTO.builder()
                                                .date(r.orderDate()).orders((int) r.orderCount())
                                                .revenue(r.revenue()).itemsSold((int) r.itemsSold())
                                                .build())
                                .toList();

                List<DailySalesTrendRow> prevRows = dashboardRepository.dailySalesTrend(prev.start, prev.end);
                int prevOrders = prevRows.stream().mapToInt(r -> (int) r.orderCount()).sum();
                BigDecimal prevRevenue = prevRows.stream()
                                .map(DailySalesTrendRow::revenue).reduce(BigDecimal.ZERO, BigDecimal::add);
                int curOrders = rows.stream().mapToInt(r -> (int) r.orderCount()).sum();
                BigDecimal curRevenue = rows.stream()
                                .map(DailySalesTrendRow::revenue).reduce(BigDecimal.ZERO, BigDecimal::add);

                return DashboardSalesTrendResponse.builder()
                                .period(period).dataPoints(dataPoints)
                                .comparison(DashboardSalesTrendResponse.ComparisonDTO.builder()
                                                .previousPeriod(DashboardSalesTrendResponse.PreviousPeriodDTO.builder()
                                                                .totalOrders(prevOrders).totalRevenue(prevRevenue)
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
                List<TopProductRow> rows = dashboardRepository.topSellingProducts(
                                range.start, range.end, PageRequest.of(0, limit));

                BigDecimal totalRevenue = dashboardRepository.revenueBetween(range.start, range.end);

                List<DashboardTopSellingProductsResponse.TopProductDTO> products = rows.stream()
                                .map(r -> {
                                        double pct = totalRevenue.compareTo(BigDecimal.ZERO) == 0 ? 0.0
                                                        : r.revenue().divide(totalRevenue, 4, RoundingMode.HALF_UP)
                                                                        .multiply(BigDecimal.valueOf(100))
                                                                        .doubleValue();
                                        return DashboardTopSellingProductsResponse.TopProductDTO.builder()
                                                        .productId(r.productId()).sku(r.sku()).name(r.name())
                                                        .unitsSold((int) r.unitsSold()).revenue(r.revenue())
                                                        .percentage(pct)
                                                        .build();
                                })
                                .toList();

                return DashboardTopSellingProductsResponse.builder().period(period).products(products).build();
        }

        // ─── Private helpers ──────────────────────────────────────────────────────

        private DateRange resolve(DashboardPeriod period, LocalDate startDate, LocalDate endDate) {
                // if ((startDate == null) != (endDate == null)) {
                // throw new IllegalArgumentException("startDate and endDate must be provided
                // together");
                // }
                if (startDate != null && endDate != null) {
                        if (startDate.isAfter(endDate)) {
                                throw new IllegalArgumentException("startDate must be on or before endDate");
                        }
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

        /**
         * Fills the full [start, end] calendar range with {@link DailySalesTrendRow}s,
         * inserting zero-valued rows for dates that had no sales orders.
         * <p>
         * Without this, chart consumers receive a sparse list and must infer missing
         * dates themselves — leading to rendering gaps or incorrect interpolation.
         *
         * @param dbRows sparse rows returned by the repository (only dates with orders)
         * @param start  inclusive range start
         * @param end    inclusive range end
         * @return dense list with one entry per calendar day, zero-filled where needed
         */
        private List<DailySalesTrendRow> fillMissingDates(List<DailySalesTrendRow> dbRows, LocalDate start,
                        LocalDate end) {

                // Index DB rows by date for O(1) lookup
                Map<LocalDate, DailySalesTrendRow> rowByDate = dbRows.stream()
                                .collect(Collectors.toMap(DailySalesTrendRow::orderDate, r -> r));

                List<DailySalesTrendRow> dense = new ArrayList<>();
                LocalDate cursor = start;
                while (!cursor.isAfter(end)) {
                        dense.add(rowByDate.getOrDefault(cursor,
                                        new DailySalesTrendRow(cursor, 0L, BigDecimal.ZERO, 0L)));
                        cursor = cursor.plusDays(1);
                }
                return dense;
        }

        private DateRange previous(DateRange current) {
                long days = ChronoUnit.DAYS.between(current.start, current.end) + 1;
                return new DateRange(current.start.minusDays(days), current.start.minusDays(1));
        }

        private double growthPercent(BigDecimal previous, BigDecimal current) {
                if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0)
                        return 0.0;
                return current.subtract(previous)
                                .divide(previous, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100)).doubleValue();
        }

        private int reorderLevelFor(List<InventoryItem> items, Long productId) {
                return items.stream()
                                .filter(i -> i.getProduct().getId().equals(productId))
                                .findFirst()
                                .map(i -> i.getProduct().getReorderLevel())
                                .orElse(0);
        }

        private DashboardActivityType soActivityType(String status) {
                return "PENDING".equals(status) ? DashboardActivityType.SALES_ORDER_CREATED
                                : DashboardActivityType.SALES_ORDER_UPDATED;
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

        private DashboardActivityType poActivityType(String status) {
                return "APPROVED".equals(status) ? DashboardActivityType.PURCHASE_ORDER_APPROVED
                                : DashboardActivityType.PURCHASE_ORDER_UPDATED;
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