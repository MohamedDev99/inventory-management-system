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
import com.moeware.ims.entity.transaction.InventoryMovement;
import com.moeware.ims.enums.transaction.MovementType;

/**
 * Repository interface for InventoryMovement entity
 * Tracks all inventory movements including transfers, receipts, and shipments
 */
@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

        /**
         * Find all movements for a specific product
         */
        Page<InventoryMovement> findByProduct(Product product, Pageable pageable);

        /**
         * Find all movements from a specific warehouse
         */
        Page<InventoryMovement> findByFromWarehouse(Warehouse warehouse, Pageable pageable);

        /**
         * Find all movements to a specific warehouse
         */
        Page<InventoryMovement> findByToWarehouse(Warehouse warehouse, Pageable pageable);

        /**
         * Find all movements involving a warehouse (either from or to)
         */
        @Query("SELECT m FROM InventoryMovement m WHERE m.fromWarehouse = :warehouse OR m.toWarehouse = :warehouse")
        Page<InventoryMovement> findByWarehouse(@Param("warehouse") Warehouse warehouse, Pageable pageable);

        /**
         * Find movements by type
         */
        Page<InventoryMovement> findByMovementType(MovementType movementType, Pageable pageable);

        /**
         * Find movements by date range
         */
        @Query("SELECT m FROM InventoryMovement m WHERE m.movementDate BETWEEN :startDate AND :endDate")
        Page<InventoryMovement> findByMovementDateBetween(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        /**
         * Find movements with filters
         */
        @Query("SELECT m FROM InventoryMovement m WHERE " +
                        "(:productId IS NULL OR m.product.id = :productId) AND " +
                        "(:warehouseId IS NULL OR m.fromWarehouse.id = :warehouseId OR m.toWarehouse.id = :warehouseId) AND "
                        +
                        "(:movementType IS NULL OR m.movementType = :movementType) AND " +
                        "(:startDate IS NULL OR m.movementDate >= :startDate) AND " +
                        "(:endDate IS NULL OR m.movementDate <= :endDate)")
        Page<InventoryMovement> findAllWithFilters(
                        @Param("productId") Long productId,
                        @Param("warehouseId") Long warehouseId,
                        @Param("movementType") MovementType movementType,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        /**
         * Get movement statistics for a product
         */
        @Query("SELECT COUNT(m), SUM(m.quantity) FROM InventoryMovement m WHERE m.product.id = :productId")
        List<Object[]> getProductMovementStats(@Param("productId") Long productId);

        /**
         * Find movements by reference number (PO, SO, etc.)
         */
        List<InventoryMovement> findByReferenceNumber(String referenceNumber);

        /**
         * Get recent movements (last N days)
         */
        @Query("SELECT m FROM InventoryMovement m WHERE m.movementDate >= :sinceDate ORDER BY m.movementDate DESC")
        List<InventoryMovement> findRecentMovements(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);
}