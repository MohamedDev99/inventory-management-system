package com.moeware.ims.repository.transaction;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.transaction.PurchaseOrder;
import com.moeware.ims.enums.transaction.PurchaseOrderStatus;

/**
 * Repository for PurchaseOrder entity
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByPoNumber(String poNumber);

    boolean existsByPoNumber(String poNumber);

    /**
     * Find all purchase orders with optional filters and pagination
     */
    @Query("""
            SELECT po FROM PurchaseOrder po
            WHERE (:search IS NULL OR po.poNumber LIKE %:search%)
            AND (:supplierId IS NULL OR po.supplier.id = :supplierId)
            AND (:warehouseId IS NULL OR po.warehouse.id = :warehouseId)
            AND (:status IS NULL OR po.status = :status)
            AND (:createdByUserId IS NULL OR po.createdByUser.id = :createdByUserId)
            AND (:startDate IS NULL OR po.orderDate >= :startDate)
            AND (:endDate IS NULL OR po.orderDate <= :endDate)
            """)
    Page<PurchaseOrder> findAllWithFilters(
            @Param("search") String search,
            @Param("supplierId") Long supplierId,
            @Param("warehouseId") Long warehouseId,
            @Param("status") PurchaseOrderStatus status,
            @Param("createdByUserId") Long createdByUserId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    /**
     * Find all purchase orders pending approval (SUBMITTED status)
     */
    Page<PurchaseOrder> findByStatus(PurchaseOrderStatus status, Pageable pageable);

    /**
     * Find purchase orders by supplier
     */
    @Query("""
            SELECT po FROM PurchaseOrder po
            WHERE po.supplier.id = :supplierId
            AND (:status IS NULL OR po.status = :status)
            AND (:startDate IS NULL OR po.orderDate >= :startDate)
            AND (:endDate IS NULL OR po.orderDate <= :endDate)
            """)
    Page<PurchaseOrder> findBySupplierIdWithFilters(
            @Param("supplierId") Long supplierId,
            @Param("status") PurchaseOrderStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    /**
     * Count POs created on a given date - used for PO number sequence generation
     */
    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.orderDate = :date")
    long countByOrderDate(@Param("date") LocalDate date);
}