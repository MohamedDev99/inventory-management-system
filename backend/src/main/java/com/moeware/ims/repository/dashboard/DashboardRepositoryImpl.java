package com.moeware.ims.repository.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.moeware.ims.enums.transaction.PurchaseOrderStatus;
import com.moeware.ims.enums.transaction.ShipmentStatus;
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
 * <p>
 * <strong>Design rule:</strong> if a domain repository already exposes a method
 * that answers the question, delegate to it. Only reach for the
 * {@link EntityManager} for true multi-table aggregations (GROUP BY / SUM /
 * trend queries) that do not exist on any domain repository and would be wrong
 * to add there (they belong to the dashboard layer, not to domain queries).
 * <p>
 * 
 * <pre>
 * Delegates to domain repos  →  simple counts, status-filtered lists, low-stock
 * Uses EntityManager directly →  revenue sums, daily trends, top-N rankings,
 *                                activity feed projections, overdue detail rows
 * </pre>
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class DashboardRepositoryImpl implements DashboardRepository {

    // ─── Domain repositories (delegate where possible) ────────────────────────

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ShipmentRepository shipmentRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final InvoiceRepository invoiceRepository;

    // ─── EntityManager: used only for aggregation queries ────────────────────

    @PersistenceContext
    private EntityManager em;

    // ═══════════════════════════════════════════════════════════════════════════
    // OVERVIEW METRICS – all delegate to domain repos
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Delegates to {@link ProductRepository#countByIsActive(Boolean)}.
     */
    @Override
    public int countActiveProducts() {
        return (int) productRepository.countByIsActive(true);
    }

    /**
     * Delegates to {@link WarehouseRepository#countByIsActive(Boolean)}.
     */
    @Override
    public int countActiveWarehouses() {
        return (int) warehouseRepository.countByIsActive(true);
    }

    /**
     * Delegates to {@link UserRepository#countByIsActiveTrue()}.
     */
    @Override
    public int countActiveUsers() {
        return (int) userRepository.countByIsActiveTrue();
    }

    /**
     * Aggregation query – no equivalent exists on any domain repo.
     * Sums (unitPrice × quantity) across all active products.
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
     * Delegates to {@link ProductRepository#findLowStockProducts()}.
     * Returns products where any warehouse stock ≤ reorderLevel (but > 0).
     */
    @Override
    public int countLowStockProducts() {
        return productRepository.findLowStockProducts().size();
    }

    /**
     * Delegates to {@link ProductRepository#findOutOfStockProducts()}.
     */
    @Override
    public int countOutOfStockProducts() {
        return productRepository.findOutOfStockProducts().size();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ORDER COUNTS – delegate to domain repos
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Delegates to {@link SalesOrderRepository#findAllWithFilters} filtered to
     * PENDING.
     */
    @Override
    public int countPendingSalesOrders() {
        return (int) salesOrderRepository
                .findAllWithFilters(null, null, null,
                        com.moeware.ims.enums.transaction.SalesOrderStatus.PENDING,
                        null, null, null, Pageable.unpaged())
                .getTotalElements();
    }

    /**
     * Delegates to {@link SalesOrderRepository#findAllWithFilters} filtered to
     * CONFIRMED.
     */
    @Override
    public int countConfirmedSalesOrders() {
        return (int) salesOrderRepository
                .findAllWithFilters(null, null, null,
                        com.moeware.ims.enums.transaction.SalesOrderStatus.CONFIRMED,
                        null, null, null, Pageable.unpaged())
                .getTotalElements();
    }

    /**
     * Delegates to {@link PurchaseOrderRepository#findByStatus} filtered to
     * SUBMITTED.
     */
    @Override
    public int countPendingPurchaseOrders() {
        return (int) purchaseOrderRepository
                .findByStatus(PurchaseOrderStatus.SUBMITTED, Pageable.unpaged())
                .getTotalElements();
    }

    /**
     * Delegates to {@link PurchaseOrderRepository#findByStatus} filtered to
     * APPROVED.
     */
    @Override
    public int countApprovedPurchaseOrders() {
        return (int) purchaseOrderRepository
                .findByStatus(PurchaseOrderStatus.APPROVED, Pageable.unpaged())
                .getTotalElements();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TODAY'S ACTIVITY – delegate to domain repos
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Delegates to {@link SalesOrderRepository#countByOrderDate(LocalDate)}.
     */
    @Override
    public int countSalesOrdersToday(LocalDate today) {
        return (int) salesOrderRepository.countByOrderDate(today);
    }

    /**
     * Delegates to {@link PurchaseOrderRepository#countByOrderDate(LocalDate)}.
     */
    @Override
    public int countPurchaseOrdersToday(LocalDate today) {
        return (int) purchaseOrderRepository.countByOrderDate(today);
    }

    /**
     * Delegates to {@link ShipmentRepository#findByStatus} with PENDING +
     * IN_TRANSIT,
     * using the count-by-status convenience. Falls back to EM for today's date
     * filter
     * because ShipmentRepository has no createdAt-date query.
     */
    @Override
    public int countShipmentsToday(LocalDate today) {
        // ShipmentRepository has no createdAt-date filter; EM is the right tool here.
        return toInt(em.createQuery("""
                SELECT COUNT(s) FROM Shipment s
                WHERE FUNCTION('DATE', s.createdAt) = :today
                """)
                .setParameter("today", today)
                .getSingleResult());
    }

    /**
     * Delegates to {@link PurchaseOrderRepository#findAllWithFilters}
     * filtered to RECEIVED with actualDeliveryDate = today.
     */
    @Override
    public int countPurchaseOrdersReceivedToday(LocalDate today) {
        return (int) purchaseOrderRepository
                .findAllWithFilters(null, null, null,
                        PurchaseOrderStatus.RECEIVED, null, today, today, Pageable.unpaged())
                .getTotalElements();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ALERTS – delegate to domain repos
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Delegates to {@link StockAdjustmentRepository#findPendingAdjustments()}.
     */
    @Override
    public int countPendingStockAdjustments() {
        return stockAdjustmentRepository.findPendingAdjustments().size();
    }

    /**
     * Delegates to {@link InvoiceRepository#findOverdueInvoices(LocalDate)}.
     */
    @Override
    public int countOverdueInvoices(LocalDate today) {
        return invoiceRepository.findOverdueInvoices(today).size();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // REVENUE – aggregation queries, EntityManager required
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    public BigDecimal revenueOnDate(LocalDate date) {
        return coalesce(em.createQuery("""
                SELECT SUM(so.totalAmount) FROM SalesOrder so
                WHERE so.status NOT IN ('CANCELLED', 'PENDING')
                AND so.orderDate = :date
                """)
                .setParameter("date", date)
                .getSingleResult());
    }

    @Override
    public BigDecimal revenueBetween(LocalDate start, LocalDate end) {
        return coalesce(em.createQuery("""
                SELECT SUM(so.totalAmount) FROM SalesOrder so
                WHERE so.status NOT IN ('CANCELLED', 'PENDING')
                AND so.orderDate BETWEEN :start AND :end
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PURCHASE SPEND – aggregation queries, EntityManager required
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    public BigDecimal purchaseSpendBetween(LocalDate start, LocalDate end) {
        return coalesce(em.createQuery("""
                SELECT SUM(po.totalAmount) FROM PurchaseOrder po
                WHERE po.status != 'CANCELLED'
                AND po.orderDate BETWEEN :start AND :end
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult());
    }

    @Override
    public int totalItemsOrdered(LocalDate start, LocalDate end) {
        Object r = em.createQuery("""
                SELECT SUM(poi.quantityOrdered) FROM PurchaseOrderItem poi
                JOIN poi.purchaseOrder po
                WHERE po.status != 'CANCELLED'
                AND po.orderDate BETWEEN :start AND :end
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
        return r == null ? 0 : ((Number) r).intValue();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SALES ANALYTICS – aggregation queries, EntityManager required
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> dailySalesTrend(LocalDate start, LocalDate end) {
        return em.createQuery("""
                SELECT so.orderDate,
                       COUNT(so),
                       COALESCE(SUM(so.totalAmount), 0),
                       COALESCE(SUM(soi.quantity), 0)
                FROM SalesOrder so
                LEFT JOIN so.items soi
                WHERE so.status != 'CANCELLED'
                AND so.orderDate BETWEEN :start AND :end
                GROUP BY so.orderDate
                ORDER BY so.orderDate ASC
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> topSellingProducts(LocalDate start, LocalDate end, Pageable pageable) {
        return em.createQuery("""
                SELECT p.id, p.sku, p.name,
                       COALESCE(SUM(soi.quantity), 0),
                       COALESCE(SUM(soi.lineTotal), 0)
                FROM SalesOrderItem soi
                JOIN soi.product p
                JOIN soi.salesOrder so
                WHERE so.status != 'CANCELLED'
                AND so.orderDate BETWEEN :start AND :end
                GROUP BY p.id, p.sku, p.name
                ORDER BY SUM(soi.lineTotal) DESC
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> topCustomers(LocalDate start, LocalDate end, Pageable pageable) {
        return em.createQuery("""
                SELECT c.id, c.contactName, COUNT(so), COALESCE(SUM(so.totalAmount), 0)
                FROM SalesOrder so
                JOIN so.customer c
                WHERE so.status != 'CANCELLED'
                AND so.orderDate BETWEEN :start AND :end
                GROUP BY c.id, c.contactName
                ORDER BY SUM(so.totalAmount) DESC
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> salesOrderCountByStatus(LocalDate start, LocalDate end) {
        return em.createQuery("""
                SELECT CAST(so.status AS string), COUNT(so)
                FROM SalesOrder so
                WHERE so.orderDate BETWEEN :start AND :end
                GROUP BY so.status
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PURCHASE ANALYTICS – aggregation queries, EntityManager required
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> monthlyPurchaseTrend(LocalDate start, LocalDate end) {
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
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> topSuppliers(LocalDate start, LocalDate end, Pageable pageable) {
        return em.createQuery("""
                SELECT s.id, s.name, COUNT(po), COALESCE(SUM(po.totalAmount), 0)
                FROM PurchaseOrder po
                JOIN po.supplier s
                WHERE po.status != 'CANCELLED'
                AND po.orderDate BETWEEN :start AND :end
                GROUP BY s.id, s.name
                ORDER BY SUM(po.totalAmount) DESC
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> categorySpending(LocalDate start, LocalDate end) {
        return em.createQuery("""
                SELECT cat.id, cat.name,
                       COALESCE(SUM(poi.lineTotal), 0),
                       COUNT(DISTINCT po.id)
                FROM PurchaseOrderItem poi
                JOIN poi.product p
                JOIN p.category cat
                JOIN poi.purchaseOrder po
                WHERE po.status != 'CANCELLED'
                AND po.orderDate BETWEEN :start AND :end
                GROUP BY cat.id, cat.name
                ORDER BY SUM(poi.lineTotal) DESC
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> purchaseOrderCountByStatus(LocalDate start, LocalDate end) {
        return em.createQuery("""
                SELECT CAST(po.status AS string), COUNT(po)
                FROM PurchaseOrder po
                WHERE po.orderDate BETWEEN :start AND :end
                GROUP BY po.status
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // INVENTORY TREND – aggregation / native queries
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
     * Delegates to {@link ProductRepository#findLowStockProducts()} for the count.
     */
    @Override
    public int currentLowStockCount() {
        return productRepository.findLowStockProducts().size();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // LOW STOCK DETAIL – delegates to InventoryItemRepository
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Delegates to {@link InventoryItemRepository#findLowStockItems(Pageable)} and
     * maps the returned entities to the raw Object[] projection that the service
     * expects, avoiding a duplicate JPQL query.
     * <p>
     * Projection columns: [0] productId · [1] sku · [2] name · [3] reorderLevel ·
     * [4] minStockLevel · [5] warehouseId · [6] warehouseName · [7] warehouseQty
     */
    @Override
    public List<Object[]> lowStockProductsWithWarehouseBreakdown() {
        return inventoryItemRepository
                .findLowStockItems(Pageable.unpaged())
                .getContent()
                .stream()
                .map(ii -> new Object[] {
                        ii.getProduct().getId(),
                        ii.getProduct().getSku(),
                        ii.getProduct().getName(),
                        ii.getProduct().getReorderLevel(),
                        ii.getProduct().getMinStockLevel(),
                        ii.getWarehouse().getId(),
                        ii.getWarehouse().getName(),
                        ii.getQuantity()
                })
                .toList();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ACTIVITY FEED – aggregation projections, EntityManager required
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> recentSalesOrderActivity(Pageable pageable) {
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
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> recentPurchaseOrderActivity(Pageable pageable) {
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
                .getResultList();
    }

    /**
     * Delegates to {@link ShipmentRepository#findByStatus} with DELIVERED status,
     * then maps entities to the projection the service expects.
     * <p>
     * Projection columns: [0] shipId · [1] shipNumber · [2] customerName ·
     * [3] updatedAt · [4] userId · [5] username
     */
    @Override
    public List<Object[]> recentDeliveries(Pageable pageable) {
        return shipmentRepository
                .findByStatus(ShipmentStatus.DELIVERED, pageable)
                .getContent()
                .stream()
                .sorted(Comparator.comparing(
                        sh -> sh.getUpdatedAt(), Comparator.reverseOrder()))
                .map(sh -> new Object[] {
                        sh.getId(),
                        sh.getShipmentNumber(),
                        sh.getSalesOrder().getCustomerName(),
                        sh.getUpdatedAt(),
                        sh.getShippedBy().getId(),
                        sh.getShippedBy().getUsername()
                })
                .toList();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PENDING ACTIONS – mix of domain repo delegation and EM aggregation
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Delegates to {@link ShipmentRepository#findPendingShipments()} and returns
     * the single oldest entry (sorted by createdAt ASC) as a projection row.
     * <p>
     * Projection columns: [0] id · [1] shipmentNumber · [2] createdAt
     */
    @Override
    public List<Object[]> oldestPendingShipment(Pageable pageable) {
        return shipmentRepository.findPendingShipments()
                .stream()
                .sorted(Comparator.comparing(sh -> sh.getCreatedAt()))
                .limit(pageable.getPageSize())
                .map(sh -> new Object[] {
                        sh.getId(),
                        sh.getShipmentNumber(),
                        sh.getCreatedAt()
                })
                .toList();
    }

    /**
     * Delegates to {@link ShipmentRepository#findByStatus} with PENDING status.
     */
    @Override
    public int countPendingShipments() {
        return (int) shipmentRepository
                .findByStatus(ShipmentStatus.PENDING, Pageable.unpaged())
                .getTotalElements();
    }

    /**
     * Delegates to {@link InvoiceRepository#findOverdueInvoices(LocalDate)} and
     * maps the invoice entities to the projection columns the service expects.
     * <p>
     * Projection columns: [0] invoiceId · [1] invoiceNumber · [2] balanceDue ·
     * [3] dueDate · [4] customerId · [5] contactName · [6] createdAt
     */
    @Override
    public List<Object[]> overdueInvoiceDetails(LocalDate today) {
        return invoiceRepository.findOverdueInvoices(today)
                .stream()
                .map(i -> new Object[] {
                        i.getId(),
                        i.getInvoiceNumber(),
                        i.getBalanceDue(),
                        i.getDueDate(),
                        i.getCustomer().getId(),
                        i.getCustomer().getContactName(),
                        i.getCreatedAt()
                })
                .toList();
    }

    /**
     * Aggregation projection – no equivalent on PurchaseOrderRepository.
     * Returns PO id, number, total, createdAt, and creator info for the approval
     * queue.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> pendingPurchaseOrderApprovals(Pageable pageable) {
        return em.createQuery("""
                SELECT po.id, po.poNumber, po.totalAmount, po.createdAt, u.id, u.username
                FROM PurchaseOrder po
                JOIN po.createdByUser u
                WHERE po.status = 'SUBMITTED'
                ORDER BY po.createdAt ASC
                """)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Utilities
    // ═══════════════════════════════════════════════════════════════════════════

    private int toInt(Object o) {
        return o == null ? 0 : ((Number) o).intValue();
    }

    private BigDecimal coalesce(Object o) {
        return o == null ? BigDecimal.ZERO : (BigDecimal) o;
    }
}