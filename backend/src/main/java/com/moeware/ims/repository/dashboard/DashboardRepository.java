package com.moeware.ims.repository.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

/**
 * Dedicated read-only query contract for all dashboard aggregations.
 * <p>
 * This is intentionally <em>not</em> a {@code JpaRepository} extension —
 * it is a plain interface whose sole implementation
 * ({@link DashboardRepositoryImpl}) executes queries directly via
 * {@code EntityManager}. Keeping it as an interface lets tests swap in a stub
 * without spinning up a database.
 *
 * <h3>Projection records</h3>
 * Every method that previously returned {@code List<Object[]>} now returns a
 * {@code List} of a typed record defined as a nested type here. This gives
 * compile-time safety: if a query's column set changes, the mapping in
 * {@link DashboardRepositoryImpl} and the consumption in
 * {@link com.moeware.ims.service.dashboard.DashboardService} both fail at
 * compile time rather than throwing {@link ClassCastException} at runtime.
 */
public interface DashboardRepository {

        // ═══════════════════════════════════════════════════════════════════════════
        // PROJECTION RECORDS
        // Each record corresponds exactly to the column set of one query group.
        // ═══════════════════════════════════════════════════════════════════════════

        /** One calendar-day data point from the daily sales trend query. */
        record DailySalesTrendRow(
                        LocalDate orderDate,
                        long orderCount,
                        BigDecimal revenue,
                        long itemsSold) {
        }

        /** One product entry from the top-selling-products query. */
        record TopProductRow(
                        Long productId,
                        String sku,
                        String name,
                        long unitsSold,
                        BigDecimal revenue) {
        }

        /** One customer entry from the top-customers query. */
        record TopCustomerRow(
                        Long customerId,
                        String contactName,
                        long orderCount,
                        BigDecimal totalSpent) {
        }

        /** One status-count entry from the order-count-by-status query. */
        record StatusCountRow(String status, long count) {
        }

        /** One month entry from the monthly purchase trend query. */
        record MonthlyPurchaseTrendRow(
                        /** YYYY-MM formatted string, e.g. {@code "2026-02"}. */
                        String month,
                        long orderCount,
                        BigDecimal totalSpent) {
        }

        /** One supplier entry from the top-suppliers query. */
        record TopSupplierRow(
                        Long supplierId,
                        String supplierName,
                        long orderCount,
                        BigDecimal totalSpent) {
        }

        /** One category entry from the category-spending query. */
        record CategorySpendingRow(
                        Long categoryId,
                        String categoryName,
                        BigDecimal totalSpent,
                        long orderCount) {
        }

        /** One product row from the low-stock-with-warehouse-breakdown query. */
        record LowStockWarehouseRow(
                        Long productId,
                        String sku,
                        String name,
                        int reorderLevel,
                        int minStockLevel,
                        Long warehouseId,
                        String warehouseName,
                        int warehouseQty) {
        }

        /** One sales-order row for the activity feed. */
        record SalesOrderActivityRow(
                        Long soId,
                        String soNumber,
                        String status,
                        String customerName,
                        BigDecimal totalAmount,
                        LocalDateTime createdAt,
                        Long userId,
                        String username) {
        }

        /** One purchase-order row for the activity feed. */
        record PurchaseOrderActivityRow(
                        Long poId,
                        String poNumber,
                        String status,
                        String supplierName,
                        BigDecimal totalAmount,
                        LocalDateTime updatedAt,
                        Long userId,
                        String username) {
        }

        /** One shipment delivery row for the activity feed. */
        record DeliveryRow(
                        Long shipmentId,
                        String shipmentNumber,
                        String customerName,
                        LocalDateTime updatedAt,
                        Long userId,
                        String username) {
        }

        /** The oldest pending shipment row. */
        record PendingShipmentRow(
                        Long id,
                        String shipmentNumber,
                        LocalDateTime createdAt) {
        }

        /** One overdue invoice detail row. */
        record OverdueInvoiceRow(
                        Long invoiceId,
                        String invoiceNumber,
                        BigDecimal balanceDue,
                        LocalDate dueDate,
                        Long customerId,
                        String contactName,
                        LocalDateTime createdAt) {
        }

        /** One purchase-order approval queue row. */
        record PendingPoApprovalRow(
                        Long poId,
                        String poNumber,
                        BigDecimal totalAmount,
                        LocalDateTime createdAt,
                        Long userId,
                        String username) {
        }

        // ═══════════════════════════════════════════════════════════════════════════
        // OVERVIEW METRICS
        // ═══════════════════════════════════════════════════════════════════════════

        int countActiveProducts();

        int countActiveWarehouses();

        int countActiveUsers();

        BigDecimal totalInventoryValue();

        /**
         * Count of active products whose total warehouse stock is ≤ reorderLevel but >
         * 0.
         * Uses a dedicated COUNT query — does not load product entities.
         */
        int countLowStockProducts();

        /**
         * Count of active products whose total warehouse stock is 0.
         * Uses a dedicated COUNT query — does not load product entities.
         */
        int countOutOfStockProducts();

        // ═══════════════════════════════════════════════════════════════════════════
        // ORDER COUNTS
        // ═══════════════════════════════════════════════════════════════════════════

        int countPendingSalesOrders();

        int countConfirmedSalesOrders();

        /** Counts purchase orders with status SUBMITTED (awaiting manager approval). */
        int countPendingPurchaseOrders();

        int countApprovedPurchaseOrders();

        // ═══════════════════════════════════════════════════════════════════════════
        // TODAY'S ACTIVITY
        // ═══════════════════════════════════════════════════════════════════════════

        int countSalesOrdersToday(LocalDate today);

        int countPurchaseOrdersToday(LocalDate today);

        int countShipmentsToday(LocalDate today);

        int countPurchaseOrdersReceivedToday(LocalDate today);

        // ═══════════════════════════════════════════════════════════════════════════
        // ALERTS
        // ═══════════════════════════════════════════════════════════════════════════

        /**
         * Count of stock adjustments in PENDING status.
         * Uses a dedicated COUNT query — does not load adjustment entities.
         */
        int countPendingStockAdjustments();

        /**
         * Count of invoices past their due date that are not PAID or CANCELLED.
         * Uses a dedicated COUNT query — does not load invoice entities.
         */
        int countOverdueInvoices(LocalDate today);

        // ═══════════════════════════════════════════════════════════════════════════
        // REVENUE
        // ═══════════════════════════════════════════════════════════════════════════

        BigDecimal revenueOnDate(LocalDate date);

        BigDecimal revenueBetween(LocalDate start, LocalDate end);

        // ═══════════════════════════════════════════════════════════════════════════
        // PURCHASE SPEND
        // ═══════════════════════════════════════════════════════════════════════════

        /** Total PO spend (non-cancelled POs) in the date range. */
        BigDecimal purchaseSpendBetween(LocalDate start, LocalDate end);

        /**
         * Sum of quantity_ordered across all non-cancelled PO items in the date range.
         */
        int totalItemsOrdered(LocalDate start, LocalDate end);

        // ═══════════════════════════════════════════════════════════════════════════
        // SALES ANALYTICS
        // ═══════════════════════════════════════════════════════════════════════════

        List<DailySalesTrendRow> dailySalesTrend(LocalDate start, LocalDate end);

        List<TopProductRow> topSellingProducts(LocalDate start, LocalDate end, Pageable pageable);

        List<TopCustomerRow> topCustomers(LocalDate start, LocalDate end, Pageable pageable);

        List<StatusCountRow> salesOrderCountByStatus(LocalDate start, LocalDate end);

        // ═══════════════════════════════════════════════════════════════════════════
        // PURCHASE ANALYTICS
        // ═══════════════════════════════════════════════════════════════════════════

        /**
         * Monthly PO count and spend.
         * Implemented as a native PostgreSQL query using {@code TO_CHAR} for date
         * grouping.
         */
        List<MonthlyPurchaseTrendRow> monthlyPurchaseTrend(LocalDate start, LocalDate end);

        List<TopSupplierRow> topSuppliers(LocalDate start, LocalDate end, Pageable pageable);

        List<CategorySpendingRow> categorySpending(LocalDate start, LocalDate end);

        List<StatusCountRow> purchaseOrderCountByStatus(LocalDate start, LocalDate end);

        // ═══════════════════════════════════════════════════════════════════════════
        // INVENTORY TREND
        // ═══════════════════════════════════════════════════════════════════════════

        /**
         * Daily net inventory quantity change derived from movement records.
         * Implemented as a native query.
         */
        List<Object[]> dailyInventoryNetChange(LocalDateTime start, LocalDateTime end);

        /**
         * Current count of active products at or below their reorder level.
         * Uses a dedicated COUNT query — does not load product entities.
         */
        int currentLowStockCount();

        // ═══════════════════════════════════════════════════════════════════════════
        // LOW STOCK DETAIL
        // ═══════════════════════════════════════════════════════════════════════════

        List<LowStockWarehouseRow> lowStockProductsWithWarehouseBreakdown();

        // ═══════════════════════════════════════════════════════════════════════════
        // ACTIVITY FEED
        // ═══════════════════════════════════════════════════════════════════════════

        List<SalesOrderActivityRow> recentSalesOrderActivity(Pageable pageable);

        List<PurchaseOrderActivityRow> recentPurchaseOrderActivity(Pageable pageable);

        List<DeliveryRow> recentDeliveries(Pageable pageable);

        // ═══════════════════════════════════════════════════════════════════════════
        // PENDING ACTIONS
        // ═══════════════════════════════════════════════════════════════════════════

        List<PendingShipmentRow> oldestPendingShipment(Pageable pageable);

        int countPendingShipments();

        List<OverdueInvoiceRow> overdueInvoiceDetails(LocalDate today);

        List<PendingPoApprovalRow> pendingPurchaseOrderApprovals(Pageable pageable);
}