package com.moeware.ims.repository.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.moeware.ims.enums.transaction.PurchaseOrderStatus;
import com.moeware.ims.enums.transaction.SalesOrderStatus;
import com.moeware.ims.enums.transaction.ShipmentStatus;
import com.moeware.ims.enums.transaction.StockAdjustmentStatus;
import com.moeware.ims.repository.UserRepository;
import com.moeware.ims.repository.inventory.InventoryItemRepository;
import com.moeware.ims.repository.inventory.ProductRepository;
import com.moeware.ims.repository.staff.WarehouseRepository;
import com.moeware.ims.repository.transaction.InvoiceRepository;
import com.moeware.ims.repository.transaction.PurchaseOrderRepository;
import com.moeware.ims.repository.transaction.SalesOrderRepository;
import com.moeware.ims.repository.transaction.ShipmentRepository;
import com.moeware.ims.repository.transaction.StockAdjustmentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link DashboardRepository}.
 *
 * <h3>Design rules applied</h3>
 * <ol>
 * <li><strong>Delegate to domain repos first.</strong> If a domain repository
 * already exposes a method that answers the question, use it. The
 * {@link EntityManager} is reserved for true multi-table aggregations
 * (GROUP BY / SUM / trend queries) that have no domain-repo equivalent
 * and would pollute those repos if added there.</li>
 * <li><strong>Use COUNT queries for counting.</strong> Methods that only need
 * a count never load full entity lists — they use dedicated JPQL
 * {@code COUNT} queries or domain-repo count methods.</li>
 * <li><strong>Bind enum values, never string literals.</strong> All JPQL
 * status comparisons pass typed enum parameters so the query breaks at
 * compile time if an enum value is renamed.</li>
 * <li><strong>Let the database sort.</strong> Ordering is expressed in
 * {@link Pageable} or in the JPQL/native query. In-memory re-sorting
 * after a paginated fetch is never used.</li>
 * <li><strong>Typed projection records.</strong> Every query returns a named
 * record from {@link DashboardRepository}; {@code Object[]} is not used
 * as a return type.</li>
 * </ol>
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class DashboardRepositoryImpl implements DashboardRepository {

    // ─── Domain repositories ──────────────────────────────────────────────────

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ShipmentRepository shipmentRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final InvoiceRepository invoiceRepository;

    // ─── EntityManager: reserved for aggregation-only queries ────────────────

    @PersistenceContext
    private EntityManager em;

    // ═══════════════════════════════════════════════════════════════════════════
    // OVERVIEW METRICS – delegate to domain repos
    // ═══════════════════════════════════════════════════════════════════════════

    /** Delegates to {@link ProductRepository#countByIsActive(Boolean)}. */
    @Override
    public int countActiveProducts() {
        return (int) productRepository.countByIsActive(true);
    }

    /** Delegates to {@link WarehouseRepository#countByIsActive(Boolean)}. */
    @Override
    public int countActiveWarehouses() {
        return (int) warehouseRepository.countByIsActive(true);
    }

    /** Delegates to {@link UserRepository#countByIsActiveTrue()}. */
    @Override
    public int countActiveUsers() {
        return (int) userRepository.countByIsActiveTrue();
    }

    /**
     * Aggregation query – no domain-repo equivalent.
     * Sums (unitPrice × quantity) across all active products.getTotalElements
     */
    @Override
    public BigDecimal totalInventoryValue() {
        return coalesce(em.createQuery("""
                SELECT SUM(CAST(ii.quantity AS big_decimal) * p.unitPrice)
                FROM InventoryItem ii
                JOIN ii.product p
                WHERE p.isActive = true
                """).getSingleResult());
    }

    /**
     * Delegates to {@link ProductRepository#countLowStockProducts()}.
     * Counts active products where the summed warehouse stock is ≤ reorderLevel but
     * > 0.
     * No entity loading; the domain repo issues a pure COUNT query.
     */
    @Override
    public int countLowStockProducts() {
        return (int) productRepository.countLowStockProducts();
    }

    /**
     * Delegates to {@link ProductRepository#countOutOfStockProducts()}.
     * Counts active products where the summed warehouse stock is 0.
     * No entity loading; the domain repo issues a pure COUNT query.
     */
    @Override
    public int countOutOfStockProducts() {
        return (int) productRepository.countOutOfStockProducts();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ORDER COUNTS – delegate to domain repos
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Delegates to {@link SalesOrderRepository#countByStatus(SalesOrderStatus)}.
     * Spring Data derived COUNT — no page fetch, no entity loading.
     */
    @Override
    public int countPendingSalesOrders() {
        return (int) salesOrderRepository.countByStatus(SalesOrderStatus.PENDING);
    }

    /**
     * Delegates to {@link SalesOrderRepository#countByStatus(SalesOrderStatus)}.
     * Spring Data derived COUNT — no page fetch, no entity loading.
     */
    @Override
    public int countConfirmedSalesOrders() {
        return (int) salesOrderRepository.countByStatus(SalesOrderStatus.CONFIRMED);
    }

    /**
     * Delegates to
     * {@link PurchaseOrderRepository#countByStatus(PurchaseOrderStatus)}.
     * Spring Data derived COUNT — no page fetch, no entity loading.
     */
    @Override
    public int countPendingPurchaseOrders() {
        return (int) purchaseOrderRepository.countByStatus(PurchaseOrderStatus.SUBMITTED);
    }

    /**
     * Delegates to
     * {@link PurchaseOrderRepository#countByStatus(PurchaseOrderStatus)}.
     * Spring Data derived COUNT — no page fetch, no entity loading.
     */
    @Override
    public int countApprovedPurchaseOrders() {
        return (int) purchaseOrderRepository.countByStatus(PurchaseOrderStatus.APPROVED);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TODAY'S ACTIVITY – delegate to domain repos
    // ═══════════════════════════════════════════════════════════════════════════

    /** Delegates to {@link SalesOrderRepository#countByOrderDate(LocalDate)}. */
    @Override
    public int countSalesOrdersToday(LocalDate today) {
        return (int) salesOrderRepository.countByOrderDate(today);
    }

    /** Delegates to {@link PurchaseOrderRepository#countByOrderDate(LocalDate)}. */
    @Override
    public int countPurchaseOrdersToday(LocalDate today) {
        return (int) purchaseOrderRepository.countByOrderDate(today);
    }

    /**
     * {@link ShipmentRepository} has no createdAt-date filter, so the
     * EntityManager COUNT query is the correct tool here.
     */
    @Override
    public int countShipmentsToday(LocalDate today) {
        return toInt(em.createQuery("""
                SELECT COUNT(s) FROM Shipment s
                WHERE FUNCTION('DATE', s.createdAt) = :today
                """)
                .setParameter("today", today)
                .getSingleResult());
    }

    /**
     * Delegates to
     * {@link PurchaseOrderRepository#countByStatusAndActualDeliveryDate(PurchaseOrderStatus, LocalDate)}.
     * Spring Data derived COUNT — no page fetch, no entity loading.
     */
    @Override
    public int countPurchaseOrdersReceivedToday(LocalDate today) {
        return (int) purchaseOrderRepository
                .countByStatusAndActualDeliveryDate(PurchaseOrderStatus.RECEIVED, today);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ALERTS – dedicated COUNT queries, no entity loading
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Delegates to
     * {@link StockAdjustmentRepository#countByStatus(StockAdjustmentStatus)}.
     * This is a Spring Data derived COUNT query — no entity loading, no
     * EntityManager call.
     */
    @Override
    public int countPendingStockAdjustments() {
        return (int) stockAdjustmentRepository.countByStatus(
                com.moeware.ims.enums.transaction.StockAdjustmentStatus.PENDING);
    }

    /**
     * Delegates to {@link InvoiceRepository#countOverdueInvoices(LocalDate)}.
     * The domain repo method issues a pure COUNT query — no invoice entities are
     * loaded.
     * Excluded statuses (PAID, CANCELLED) are encapsulated inside the repo default
     * method.
     */
    @Override
    public int countOverdueInvoices(LocalDate today) {
        return (int) invoiceRepository.countOverdueInvoices(today);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // REVENUE – aggregation queries
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    public BigDecimal revenueOnDate(LocalDate date) {
        return coalesce(em.createQuery("""
                SELECT SUM(so.totalAmount) FROM SalesOrder so
                WHERE so.status NOT IN (:excludedStatuses)
                AND so.orderDate = :date
                """)
                .setParameter("excludedStatuses",
                        List.of(SalesOrderStatus.CANCELLED, SalesOrderStatus.PENDING))
                .setParameter("date", date)
                .getSingleResult());
    }

    @Override
    public BigDecimal revenueBetween(LocalDate start, LocalDate end) {
        return coalesce(em.createQuery("""
                SELECT SUM(so.totalAmount) FROM SalesOrder so
                WHERE so.status NOT IN (:excludedStatuses)
                AND so.orderDate BETWEEN :start AND :end
                """)
                .setParameter("excludedStatuses",
                        List.of(SalesOrderStatus.CANCELLED, SalesOrderStatus.PENDING))
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PURCHASE SPEND – aggregation queries
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    public BigDecimal purchaseSpendBetween(LocalDate start, LocalDate end) {
        return coalesce(em.createQuery("""
                SELECT SUM(po.totalAmount) FROM PurchaseOrder po
                WHERE po.status != :cancelled
                AND po.orderDate BETWEEN :start AND :end
                """)
                .setParameter("cancelled", PurchaseOrderStatus.CANCELLED)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult());
    }

    @Override
    public int totalItemsOrdered(LocalDate start, LocalDate end) {
        Object r = em.createQuery("""
                SELECT SUM(poi.quantityOrdered) FROM PurchaseOrderItem poi
                JOIN poi.purchaseOrder po
                WHERE po.status != :cancelled
                AND po.orderDate BETWEEN :start AND :end
                """)
                .setParameter("cancelled", PurchaseOrderStatus.CANCELLED)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
        return r == null ? 0 : ((Number) r).intValue();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SALES ANALYTICS – aggregation, typed projection records
    // ═══════════════════════════════════════════════════════════════════════════

    // In DashboardRepositoryImpl — replace the dailySalesTrend method

    @Override
    @SuppressWarnings("unchecked")
    public List<DailySalesTrendRow> dailySalesTrend(LocalDate start, LocalDate end) {
        // Query 1: order count and revenue — no join to items, so no row multiplication
        List<Object[]> orderRows = em.createQuery("""
                SELECT so.orderDate,
                       COUNT(so),
                       COALESCE(SUM(so.totalAmount), 0)
                FROM SalesOrder so
                WHERE so.status != :cancelled
                AND so.orderDate BETWEEN :start AND :end
                GROUP BY so.orderDate
                ORDER BY so.orderDate ASC
                """)
                .setParameter("cancelled", SalesOrderStatus.CANCELLED)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();

        // Query 2: items sold — joined to items, grouped by date
        List<Object[]> itemRows = em.createQuery("""
                SELECT soi.salesOrder.orderDate,
                       COALESCE(SUM(soi.quantity), 0)
                FROM SalesOrderItem soi
                JOIN soi.salesOrder so
                WHERE so.status != :cancelled
                AND so.orderDate BETWEEN :start AND :end
                GROUP BY soi.salesOrder.orderDate
                """)
                .setParameter("cancelled", SalesOrderStatus.CANCELLED)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();

        // Build a date → itemsSold lookup map from query 2
        Map<LocalDate, Long> itemsByDate = itemRows.stream()
                .collect(java.util.stream.Collectors.toMap(
                        r -> (LocalDate) r[0],
                        r -> toLong(r[1])));

        // Merge: order count + revenue from query 1, items sold from the map
        return orderRows.stream()
                .map(r -> {
                    LocalDate date = (LocalDate) r[0];
                    return new DailySalesTrendRow(
                            date,
                            toLong(r[1]),
                            (BigDecimal) r[2],
                            itemsByDate.getOrDefault(date, 0L));
                })
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TopProductRow> topSellingProducts(LocalDate start, LocalDate end, Pageable pageable) {
        return em.createQuery("""
                SELECT p.id, p.sku, p.name,
                       COALESCE(SUM(soi.quantity), 0),
                       COALESCE(SUM(soi.lineTotal), 0)
                FROM SalesOrderItem soi
                JOIN soi.product p
                JOIN soi.salesOrder so
                WHERE so.status != :cancelled
                AND so.orderDate BETWEEN :start AND :end
                GROUP BY p.id, p.sku, p.name
                ORDER BY SUM(soi.lineTotal) DESC
                """)
                .setParameter("cancelled", SalesOrderStatus.CANCELLED)
                .setParameter("start", start)
                .setParameter("end", end)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new TopProductRow(
                            toLong(r[0]),
                            (String) r[1],
                            (String) r[2],
                            toLong(r[3]),
                            (BigDecimal) r[4]);
                })
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TopCustomerRow> topCustomers(LocalDate start, LocalDate end, Pageable pageable) {
        return em.createQuery("""
                SELECT c.id, c.contactName, COUNT(so), COALESCE(SUM(so.totalAmount), 0)
                FROM SalesOrder so
                JOIN so.customer c
                WHERE so.status != :cancelled
                AND so.orderDate BETWEEN :start AND :end
                GROUP BY c.id, c.contactName
                ORDER BY SUM(so.totalAmount) DESC
                """)
                .setParameter("cancelled", SalesOrderStatus.CANCELLED)
                .setParameter("start", start)
                .setParameter("end", end)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new TopCustomerRow(
                            toLong(r[0]),
                            (String) r[1],
                            toLong(r[2]),
                            (BigDecimal) r[3]);
                })
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<StatusCountRow> salesOrderCountByStatus(LocalDate start, LocalDate end) {
        return em.createQuery("""
                SELECT CAST(so.status AS string), COUNT(so)
                FROM SalesOrder so
                WHERE so.orderDate BETWEEN :start AND :end
                GROUP BY so.status
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new StatusCountRow((String) r[0], toLong(r[1]));
                })
                .toList();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PURCHASE ANALYTICS – aggregation, typed projection records
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Native SQL query required: {@code TO_CHAR(date, 'YYYY-MM')} groups rows by
     * calendar month efficiently inside PostgreSQL. The equivalent JPQL would
     * require
     * {@code FUNCTION('TO_CHAR', ...)} which is non-standard and produces less
     * readable
     * execution plans. The project targets PostgreSQL 15+ exclusively (see
     * {@code database-schema-v1_2.md}), so native syntax is appropriate here.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<MonthlyPurchaseTrendRow> monthlyPurchaseTrend(LocalDate start, LocalDate end) {
        return em.createNativeQuery("""
                SELECT TO_CHAR(po.order_date, 'YYYY-MM') AS month,
                       COUNT(po.id)                       AS order_count,
                       COALESCE(SUM(po.total_amount), 0)  AS total_spent
                FROM purchase_orders po
                WHERE po.status != 'CANCELLED'
                  AND po.order_date BETWEEN :start AND :end
                GROUP BY TO_CHAR(po.order_date, 'YYYY-MM')
                ORDER BY month ASC
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new MonthlyPurchaseTrendRow(
                            (String) r[0],
                            toLong(r[1]),
                            new BigDecimal(r[2].toString()));
                })
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TopSupplierRow> topSuppliers(LocalDate start, LocalDate end, Pageable pageable) {
        return em.createQuery("""
                SELECT s.id, s.name, COUNT(po), COALESCE(SUM(po.totalAmount), 0)
                FROM PurchaseOrder po
                JOIN po.supplier s
                WHERE po.status != :cancelled
                AND po.orderDate BETWEEN :start AND :end
                GROUP BY s.id, s.name
                ORDER BY SUM(po.totalAmount) DESC
                """)
                .setParameter("cancelled", PurchaseOrderStatus.CANCELLED)
                .setParameter("start", start)
                .setParameter("end", end)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new TopSupplierRow(
                            toLong(r[0]),
                            (String) r[1],
                            toLong(r[2]),
                            (BigDecimal) r[3]);
                })
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CategorySpendingRow> categorySpending(LocalDate start, LocalDate end) {
        return em.createQuery("""
                SELECT cat.id, cat.name,
                       COALESCE(SUM(poi.lineTotal), 0),
                       COUNT(DISTINCT po.id)
                FROM PurchaseOrderItem poi
                JOIN poi.product p
                JOIN p.category cat
                JOIN poi.purchaseOrder po
                WHERE po.status != :cancelled
                AND po.orderDate BETWEEN :start AND :end
                GROUP BY cat.id, cat.name
                ORDER BY SUM(poi.lineTotal) DESC
                """)
                .setParameter("cancelled", PurchaseOrderStatus.CANCELLED)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new CategorySpendingRow(
                            toLong(r[0]),
                            (String) r[1],
                            (BigDecimal) r[2],
                            toLong(r[3]));
                })
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<StatusCountRow> purchaseOrderCountByStatus(LocalDate start, LocalDate end) {
        return em.createQuery("""
                SELECT CAST(po.status AS string), COUNT(po)
                FROM PurchaseOrder po
                WHERE po.orderDate BETWEEN :start AND :end
                GROUP BY po.status
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new StatusCountRow((String) r[0], toLong(r[1]));
                })
                .toList();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // INVENTORY TREND
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> dailyInventoryNetChange(LocalDateTime start, LocalDateTime end) {
        return em.createNativeQuery("""
                SELECT DATE(movement_date) AS snap_date,
                       SUM(CASE WHEN to_warehouse_id   IS NOT NULL THEN quantity ELSE 0 END)
                     - SUM(CASE WHEN from_warehouse_id IS NOT NULL THEN quantity ELSE 0 END) AS net_change
                FROM inventory_movements
                WHERE movement_date BETWEEN :start AND :end
                GROUP BY DATE(movement_date)
                ORDER BY snap_date ASC
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    /**
     * Delegates to {@link ProductRepository#countAtOrBelowReorderLevel()}.
     * Counts active products whose total warehouse stock ≤ reorderLevel,
     * including zero-stock products. No entity loading.
     */
    @Override
    public int currentLowStockCount() {
        return (int) productRepository.countAtOrBelowReorderLevel();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // LOW STOCK DETAIL – delegates to InventoryItemRepository, typed records
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Delegates to {@link InventoryItemRepository#findLowStockItems(Pageable)} and
     * maps the returned entities to typed {@link LowStockWarehouseRow} records.
     */
    @Override
    public List<LowStockWarehouseRow> lowStockProductsWithWarehouseBreakdown() {
        return inventoryItemRepository
                .findLowStockItems(Pageable.unpaged())
                .getContent()
                .stream()
                .map(ii -> new LowStockWarehouseRow(
                        ii.getProduct().getId(),
                        ii.getProduct().getSku(),
                        ii.getProduct().getName(),
                        ii.getProduct().getReorderLevel(),
                        ii.getProduct().getMinStockLevel(),
                        ii.getWarehouse().getId(),
                        ii.getWarehouse().getName(),
                        ii.getQuantity()))
                .toList();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ACTIVITY FEED – aggregation projections, typed records
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    @SuppressWarnings("unchecked")
    public List<SalesOrderActivityRow> recentSalesOrderActivity(Pageable pageable) {
        return em.createQuery("""
                SELECT so.id, so.soNumber, CAST(so.status AS string),
                       so.customerName, so.totalAmount,
                       so.createdAt, u.id, u.username
                FROM SalesOrder so
                JOIN so.createdByUser u
                ORDER BY so.createdAt DESC
                """)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new SalesOrderActivityRow(
                            toLong(r[0]),
                            (String) r[1],
                            (String) r[2],
                            (String) r[3],
                            (BigDecimal) r[4],
                            (LocalDateTime) r[5],
                            toLong(r[6]),
                            (String) r[7]);
                })
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PurchaseOrderActivityRow> recentPurchaseOrderActivity(Pageable pageable) {
        return em.createQuery("""
                SELECT po.id, po.poNumber, CAST(po.status AS string),
                       s.name, po.totalAmount,
                       po.updatedAt, u.id, u.username
                FROM PurchaseOrder po
                JOIN po.supplier s
                JOIN po.createdByUser u
                ORDER BY po.updatedAt DESC
                """)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new PurchaseOrderActivityRow(
                            toLong(r[0]),
                            (String) r[1],
                            (String) r[2],
                            (String) r[3],
                            (BigDecimal) r[4],
                            (LocalDateTime) r[5],
                            toLong(r[6]),
                            (String) r[7]);
                })
                .toList();
    }

    /**
     * Delegates to {@link ShipmentRepository#findByStatus} with
     * {@link ShipmentStatus#DELIVERED}, ordered by {@code updatedAt DESC} via a
     * sorted {@link Pageable} — no in-memory re-sorting.
     */
    @Override
    public List<DeliveryRow> recentDeliveries(Pageable pageable) {
        Pageable sortedByUpdatedAtDesc = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "updatedAt"));

        return shipmentRepository
                .findByStatus(ShipmentStatus.DELIVERED, sortedByUpdatedAtDesc)
                .getContent()
                .stream()
                .map(sh -> new DeliveryRow(
                        sh.getId(),
                        sh.getShipmentNumber(),
                        sh.getSalesOrder().getCustomerName(),
                        sh.getUpdatedAt(),
                        sh.getShippedBy().getId(),
                        sh.getShippedBy().getUsername()))
                .toList();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PENDING ACTIONS – domain repo delegation + EM aggregation, typed records
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Delegates to {@link ShipmentRepository#findPendingShipments()} and sorts
     * by {@code createdAt ASC} in memory — acceptable here because
     * {@code findPendingShipments()} returns all pending shipments (expected to
     * be a small, operationally-managed set) and there is no paginated variant.
     */
    @Override
    public List<PendingShipmentRow> oldestPendingShipment(Pageable pageable) {
        return shipmentRepository.findPendingShipments()
                .stream()
                .sorted(Comparator.comparing(sh -> sh.getCreatedAt()))
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .map(sh -> new PendingShipmentRow(
                        sh.getId(),
                        sh.getShipmentNumber(),
                        sh.getCreatedAt()))
                .toList();
    }

    /**
     * Delegates to {@link ShipmentRepository#countByStatus(ShipmentStatus)}.
     * Spring Data derived COUNT — no page fetch, no entity loading.
     */
    @Override
    public int countPendingShipments() {
        return (int) shipmentRepository.countByStatus(ShipmentStatus.PENDING);
    }

    /**
     * Delegates to {@link InvoiceRepository#findOverdueInvoices(LocalDate)} and
     * maps entities to typed {@link OverdueInvoiceRow} records.
     */
    @Override
    public List<OverdueInvoiceRow> overdueInvoiceDetails(LocalDate today) {
        return invoiceRepository.findOverdueInvoices(today)
                .stream()
                .map(i -> new OverdueInvoiceRow(
                        i.getId(),
                        i.getInvoiceNumber(),
                        i.getBalanceDue(),
                        i.getDueDate(),
                        i.getCustomer().getId(),
                        i.getCustomer().getContactName(),
                        i.getCreatedAt()))
                .toList();
    }

    /**
     * Aggregation projection — no PurchaseOrderRepository equivalent.
     * Status bound via {@link PurchaseOrderStatus#SUBMITTED} enum — not a string
     * literal.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PendingPoApprovalRow> pendingPurchaseOrderApprovals(Pageable pageable) {
        return em.createQuery("""
                SELECT po.id, po.poNumber, po.totalAmount, po.createdAt, u.id, u.username
                FROM PurchaseOrder po
                JOIN po.createdByUser u
                WHERE po.status = :submitted
                ORDER BY po.createdAt ASC
                """)
                .setParameter("submitted", PurchaseOrderStatus.SUBMITTED)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;
                    return new PendingPoApprovalRow(
                            toLong(r[0]),
                            (String) r[1],
                            (BigDecimal) r[2],
                            (LocalDateTime) r[3],
                            toLong(r[4]),
                            (String) r[5]);
                })
                .toList();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Utilities
    // ═══════════════════════════════════════════════════════════════════════════

    private int toInt(Object o) {
        return o == null ? 0 : ((Number) o).intValue();
    }

    private long toLong(Object o) {
        return o == null ? 0L : ((Number) o).longValue();
    }

    private BigDecimal coalesce(Object o) {
        return o == null ? BigDecimal.ZERO : (BigDecimal) o;
    }
}