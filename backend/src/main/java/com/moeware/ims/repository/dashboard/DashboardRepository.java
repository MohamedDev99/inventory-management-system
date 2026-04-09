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
 */
public interface DashboardRepository {

        // ─── Overview Metrics ────────────────────────────────────────────────────

        int countActiveProducts();

        int countActiveWarehouses();

        int countActiveUsers();

        BigDecimal totalInventoryValue();

        /**
         * Count of active products whose total warehouse stock is <= reorderLevel but >
         * 0.
         */
        int countLowStockProducts();

        /** Count of active products whose total warehouse stock is 0. */
        int countOutOfStockProducts();

        // ─── Order Counts ────────────────────────────────────────────────────────

        int countPendingSalesOrders();

        int countConfirmedSalesOrders();

        /** Counts purchase orders with status SUBMITTED (awaiting manager approval). */
        int countPendingPurchaseOrders();

        int countApprovedPurchaseOrders();

        // ─── Today's Activity ────────────────────────────────────────────────────

        int countSalesOrdersToday(LocalDate today);

        int countPurchaseOrdersToday(LocalDate today);

        int countShipmentsToday(LocalDate today);

        int countPurchaseOrdersReceivedToday(LocalDate today);

        // ─── Alerts ──────────────────────────────────────────────────────────────

        int countPendingStockAdjustments();

        int countOverdueInvoices(LocalDate today);

        // ─── Revenue ─────────────────────────────────────────────────────────────

        BigDecimal revenueOnDate(LocalDate date);

        BigDecimal revenueBetween(LocalDate start, LocalDate end);

        // ─── Purchase Spend ───────────────────────────────────────────────────────

        /** Total PO spend (non-cancelled POs) in the date range. */
        BigDecimal purchaseSpendBetween(LocalDate start, LocalDate end);

        /**
         * Sum of quantity_ordered across all non-cancelled PO items in the date range.
         */
        int totalItemsOrdered(LocalDate start, LocalDate end);

        // ─── Sales Analytics ─────────────────────────────────────────────────────

        /**
         * Daily sales trend in the date range.
         * <p>
         * Columns: [0] orderDate(LocalDate) · [1] orderCount(Long) ·
         * [2] revenue(BigDecimal) · [3] itemsSold(Long)
         */
        List<Object[]> dailySalesTrend(LocalDate start, LocalDate end);

        /**
         * Top selling products by revenue, limited by pageable.
         * <p>
         * Columns: [0] productId(Long) · [1] sku(String) · [2] name(String) ·
         * [3] unitsSold(Long) · [4] revenue(BigDecimal)
         */
        List<Object[]> topSellingProducts(LocalDate start, LocalDate end, Pageable pageable);

        /**
         * Top customers by total spend, limited by pageable.
         * <p>
         * Columns: [0] customerId(Long) · [1] contactName(String) ·
         * [2] orderCount(Long) · [3] totalSpent(BigDecimal)
         */
        List<Object[]> topCustomers(LocalDate start, LocalDate end, Pageable pageable);

        /**
         * Sales order count grouped by status.
         * <p>
         * Columns: [0] status(String) · [1] count(Long)
         */
        List<Object[]> salesOrderCountByStatus(LocalDate start, LocalDate end);

        // ─── Purchase Analytics ───────────────────────────────────────────────────

        /**
         * Monthly PO count and spend (native query, PostgreSQL TO_CHAR).
         * <p>
         * Columns: [0] month(String "YYYY-MM") · [1] count(Long) · [2]
         * totalSpent(BigDecimal)
         */
        List<Object[]> monthlyPurchaseTrend(LocalDate start, LocalDate end);

        /**
         * Top suppliers by total spend, limited by pageable.
         * <p>
         * Columns: [0] supplierId(Long) · [1] supplierName(String) ·
         * [2] orderCount(Long) · [3] totalSpent(BigDecimal)
         */
        List<Object[]> topSuppliers(LocalDate start, LocalDate end, Pageable pageable);

        /**
         * Spending per product category.
         * <p>
         * Columns: [0] categoryId(Long) · [1] categoryName(String) ·
         * [2] totalSpent(BigDecimal) · [3] orderCount(Long)
         */
        List<Object[]> categorySpending(LocalDate start, LocalDate end);

        /**
         * Purchase order count grouped by status.
         * <p>
         * Columns: [0] status(String) · [1] count(Long)
         */
        List<Object[]> purchaseOrderCountByStatus(LocalDate start, LocalDate end);

        // ─── Inventory Trend ─────────────────────────────────────────────────────

        /**
         * Daily net inventory quantity change from movement records (native query).
         * <p>
         * Columns: [0] snapDate(Date) · [1] netChange(Long)
         */
        List<Object[]> dailyInventoryNetChange(LocalDateTime start, LocalDateTime end);

        /** Current count of active products at or below their reorder level. */
        int currentLowStockCount();

        // ─── Low Stock Detail ────────────────────────────────────────────────────

        /**
         * All products below reorder level with per-warehouse quantity.
         * <p>
         * Columns: [0] productId(Long) · [1] sku(String) · [2] name(String) ·
         * [3] reorderLevel(Integer) · [4] minStockLevel(Integer) ·
         * [5] warehouseId(Long) · [6] warehouseName(String) · [7] warehouseQty(Integer)
         */
        List<Object[]> lowStockProductsWithWarehouseBreakdown();

        // ─── Activity Feed ────────────────────────────────────────────────────────

        /**
         * Most recent sales orders for the activity feed.
         * <p>
         * Columns: [0] soId(Long) · [1] soNumber(String) · [2] status(String) ·
         * [3] customerName(String) · [4] totalAmount(BigDecimal) ·
         * [5] createdAt(LocalDateTime) · [6] userId(Long) · [7] username(String)
         */
        List<Object[]> recentSalesOrderActivity(Pageable pageable);

        /**
         * Most recent purchase order events for the activity feed.
         * <p>
         * Columns: [0] poId(Long) · [1] poNumber(String) · [2] status(String) ·
         * [3] supplierName(String) · [4] totalAmount(BigDecimal) ·
         * [5] updatedAt(LocalDateTime) · [6] userId(Long) · [7] username(String)
         */
        List<Object[]> recentPurchaseOrderActivity(Pageable pageable);

        /**
         * Most recent shipment deliveries for the activity feed.
         * <p>
         * Columns: [0] shipId(Long) · [1] shipNumber(String) · [2] customerName(String)
         * ·
         * [3] updatedAt(LocalDateTime) · [4] userId(Long) · [5] username(String)
         */
        List<Object[]> recentDeliveries(Pageable pageable);

        // ─── Pending Actions ─────────────────────────────────────────────────────

        /**
         * Oldest pending shipment(s), ordered by createdAt ASC.
         * <p>
         * Columns: [0] id(Long) · [1] shipmentNumber(String) · [2]
         * createdAt(LocalDateTime)
         */
        List<Object[]> oldestPendingShipment(Pageable pageable);

        int countPendingShipments();

        /**
         * Detail rows for overdue invoices.
         * <p>
         * Columns: [0] invoiceId(Long) · [1] invoiceNumber(String) · [2]
         * balanceDue(BigDecimal) ·
         * [3] dueDate(LocalDate) · [4] customerId(Long) · [5] contactName(String) ·
         * [6] createdAt(LocalDateTime)
         */
        List<Object[]> overdueInvoiceDetails(LocalDate today);

        /**
         * Purchase orders currently awaiting approval (status = SUBMITTED).
         * <p>
         * Columns: [0] poId(Long) · [1] poNumber(String) · [2] totalAmount(BigDecimal)
         * ·
         * [3] createdAt(LocalDateTime) · [4] userId(Long) · [5] username(String)
         */
        List<Object[]> pendingPurchaseOrderApprovals(Pageable pageable);
}