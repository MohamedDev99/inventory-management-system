package com.moeware.ims.repository.transaction;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.transaction.SalesOrder;
import com.moeware.ims.enums.transaction.SalesOrderStatus;

/**
 * Repository for SalesOrder entity
 */
@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    Optional<SalesOrder> findBySoNumber(String soNumber);

    boolean existsBySoNumber(String soNumber);

    /**
     * Find all sales orders with optional filters and pagination
     */
    @Query("""
            SELECT so FROM SalesOrder so
            WHERE (:search IS NULL OR so.soNumber LIKE %:search% OR so.customerName LIKE %:search%)
            AND (:customerId IS NULL OR so.customer.id = :customerId)
            AND (:warehouseId IS NULL OR so.warehouse.id = :warehouseId)
            AND (:status IS NULL OR so.status = :status)
            AND (:createdByUserId IS NULL OR so.createdByUser.id = :createdByUserId)
            AND (:startDate IS NULL OR so.orderDate >= :startDate)
            AND (:endDate IS NULL OR so.orderDate <= :endDate)
            """)
    Page<SalesOrder> findAllWithFilters(
            @Param("search") String search,
            @Param("customerId") Long customerId,
            @Param("warehouseId") Long warehouseId,
            @Param("status") SalesOrderStatus status,
            @Param("createdByUserId") Long createdByUserId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    /**
     * Find sales orders by customer
     */
    @Query("""
            SELECT so FROM SalesOrder so
            WHERE so.customer.id = :customerId
            AND (:status IS NULL OR so.status = :status)
            AND (:startDate IS NULL OR so.orderDate >= :startDate)
            AND (:endDate IS NULL OR so.orderDate <= :endDate)
            """)
    Page<SalesOrder> findByCustomerIdWithFilters(
            @Param("customerId") Long customerId,
            @Param("status") SalesOrderStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    /**
     * Count SOs created on a given date - used for SO number sequence generation
     */
    @Query("SELECT COUNT(so) FROM SalesOrder so WHERE so.orderDate = :date")
    long countByOrderDate(@Param("date") LocalDate date);
}