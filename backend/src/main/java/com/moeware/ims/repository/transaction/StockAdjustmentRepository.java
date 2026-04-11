package com.moeware.ims.repository.transaction;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.inventory.Product;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.entity.transaction.StockAdjustment;
import com.moeware.ims.enums.transaction.AdjustmentReason;
import com.moeware.ims.enums.transaction.StockAdjustmentStatus;

/**
 * Repository interface for StockAdjustment entity.
 * Manages stock adjustment requests with approval workflow.
 */
@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {

        /**
         * Find adjustments by status (paginated).
         */
        Page<StockAdjustment> findByStatus(StockAdjustmentStatus status, Pageable pageable);

        /**
         * Count adjustments by status without loading entity data.
         * <p>
         * Spring Data derives the COUNT query automatically — no JPQL required.
         * Preferred over {@code findByStatus(status, unpaged()).getTotalElements()} or
         * {@code findPendingAdjustments().size()} when only the count is needed.
         *
         * @param status the status to count
         * @return number of adjustments in the given status
         */
        long countByStatus(StockAdjustmentStatus status);

        /**
         * Find all pending adjustments ordered by adjustment date ascending.
         * <p>
         * Uses a typed {@link StockAdjustmentStatus} enum parameter instead of the
         * string literal {@code 'PENDING'} so that any rename of the enum value causes
         * a compile error rather than a silent query mismatch.
         */
        @Query("SELECT s FROM StockAdjustment s " +
                        "WHERE s.status = :status " +
                        "ORDER BY s.adjustmentDate ASC")
        List<StockAdjustment> findPendingAdjustments(
                        @Param("status") StockAdjustmentStatus status);

        /**
         * Convenience overload that finds adjustments in
         * {@link StockAdjustmentStatus#PENDING} status.
         */
        default List<StockAdjustment> findPendingAdjustments() {
                return findPendingAdjustments(StockAdjustmentStatus.PENDING);
        }

        /**
         * Find adjustments by product.
         */
        Page<StockAdjustment> findByProduct(Product product, Pageable pageable);

        /**
         * Find adjustments by warehouse.
         */
        Page<StockAdjustment> findByWarehouse(Warehouse warehouse, Pageable pageable);

        /**
         * Find adjustments by reason.
         */
        Page<StockAdjustment> findByReason(AdjustmentReason reason, Pageable pageable);

        /**
         * Find adjustments whose adjustment date falls within the given range.
         */
        @Query("SELECT s FROM StockAdjustment s " +
                        "WHERE s.adjustmentDate BETWEEN :startDate AND :endDate")
        Page<StockAdjustment> findByAdjustmentDateBetween(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        /**
         * Find adjustments with optional filters (paginated).
         */
        @Query("SELECT s FROM StockAdjustment s WHERE " +
                        "(:productId IS NULL OR s.product.id = :productId) AND " +
                        "(:warehouseId IS NULL OR s.warehouse.id = :warehouseId) AND " +
                        "(:status IS NULL OR s.status = :status) AND " +
                        "(:reason IS NULL OR s.reason = :reason) AND " +
                        "(:startDate IS NULL OR s.adjustmentDate >= :startDate) AND " +
                        "(:endDate IS NULL OR s.adjustmentDate <= :endDate)")
        Page<StockAdjustment> findAllWithFilters(
                        @Param("productId") Long productId,
                        @Param("warehouseId") Long warehouseId,
                        @Param("status") StockAdjustmentStatus status,
                        @Param("reason") AdjustmentReason reason,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        /**
         * Count pending adjustments for a specific warehouse.
         * <p>
         * Uses a typed enum parameter instead of the string literal {@code 'PENDING'}.
         *
         * @param warehouseId the warehouse to count for
         * @param status      the status to filter by
         * @return count of adjustments in the given status for the warehouse
         */
        @Query("SELECT COUNT(s) FROM StockAdjustment s " +
                        "WHERE s.warehouse.id = :warehouseId " +
                        "AND s.status = :status")
        long countByWarehouseIdAndStatus(
                        @Param("warehouseId") Long warehouseId,
                        @Param("status") StockAdjustmentStatus status);

        /**
         * Convenience overload that counts {@link StockAdjustmentStatus#PENDING}
         * adjustments for a warehouse.
         */
        default long countPendingByWarehouse(Long warehouseId) {
                return countByWarehouseIdAndStatus(warehouseId, StockAdjustmentStatus.PENDING);
        }

        /**
         * Get adjustment statistics grouped by status.
         * <p>
         * Result columns: [0] status(StockAdjustmentStatus) · [1] count(Long) ·
         * [2] totalAbsQuantityChange(Long)
         */
        @Query("SELECT s.status, COUNT(s), SUM(ABS(s.quantityChange)) " +
                        "FROM StockAdjustment s GROUP BY s.status")
        List<Object[]> getAdjustmentStatistics();

        /**
         * Find recent adjustments in a given status ordered by adjustment date
         * ascending.
         * <p>
         * Uses a typed enum parameter instead of the string literal {@code 'PENDING'}.
         *
         * @param sinceDate adjustments on or after this date
         * @param status    the status to filter by
         * @return matching adjustments ordered by adjustmentDate ASC
         */
        @Query("SELECT s FROM StockAdjustment s " +
                        "WHERE s.status = :status " +
                        "AND s.adjustmentDate >= :sinceDate " +
                        "ORDER BY s.adjustmentDate ASC")
        List<StockAdjustment> findRecentAdjustmentsByStatus(
                        @Param("sinceDate") LocalDateTime sinceDate,
                        @Param("status") StockAdjustmentStatus status);

        /**
         * Convenience overload that returns recent
         * {@link StockAdjustmentStatus#PENDING}
         * adjustments — the original {@code findRecentPendingAdjustments} behaviour.
         */
        default List<StockAdjustment> findRecentPendingAdjustments(LocalDateTime sinceDate) {
                return findRecentAdjustmentsByStatus(sinceDate, StockAdjustmentStatus.PENDING);
        }
}