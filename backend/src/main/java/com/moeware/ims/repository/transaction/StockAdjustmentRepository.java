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
 * Repository interface for StockAdjustment entity
 * Manages stock adjustment requests with approval workflow
 */
@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {

        /**
         * Find adjustments by status
         */
        Page<StockAdjustment> findByStatus(StockAdjustmentStatus status, Pageable pageable);

        /**
         * Find all pending adjustments
         */
        @Query("SELECT s FROM StockAdjustment s WHERE s.status = 'PENDING' ORDER BY s.adjustmentDate ASC")
        List<StockAdjustment> findPendingAdjustments();

        /**
         * Find adjustments by product
         */
        Page<StockAdjustment> findByProduct(Product product, Pageable pageable);

        /**
         * Find adjustments by warehouse
         */
        Page<StockAdjustment> findByWarehouse(Warehouse warehouse, Pageable pageable);

        /**
         * Find adjustments by reason
         */
        Page<StockAdjustment> findByReason(AdjustmentReason reason, Pageable pageable);

        /**
         * Find adjustments by date range
         */
        @Query("SELECT s FROM StockAdjustment s WHERE s.adjustmentDate BETWEEN :startDate AND :endDate")
        Page<StockAdjustment> findByAdjustmentDateBetween(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        /**
         * Find adjustments with filters
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
         * Count pending adjustments for a warehouse
         */
        @Query("SELECT COUNT(s) FROM StockAdjustment s WHERE s.warehouse.id = :warehouseId AND s.status = 'PENDING'")
        Long countPendingByWarehouse(@Param("warehouseId") Long warehouseId);

        /**
         * Get adjustment statistics
         */
        @Query("SELECT s.status, COUNT(s), SUM(ABS(s.quantityChange)) FROM StockAdjustment s GROUP BY s.status")
        List<Object[]> getAdjustmentStatistics();

        /**
         * Find recent adjustments requiring approval
         */
        @Query("SELECT s FROM StockAdjustment s WHERE s.status = 'PENDING' AND s.adjustmentDate >= :sinceDate ORDER BY s.adjustmentDate ASC")
        List<StockAdjustment> findRecentPendingAdjustments(@Param("sinceDate") LocalDateTime sinceDate);
}