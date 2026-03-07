package com.moeware.ims.repository.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.transaction.Payment;
import com.moeware.ims.enums.transaction.PaymentMethod;
import com.moeware.ims.enums.transaction.PaymentStatus;

/**
 * Repository interface for Payment entity
 * Tracks payment transactions for sales orders and customers
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by unique payment number
     */
    Optional<Payment> findByPaymentNumber(String paymentNumber);

    /**
     * Find all payments for a specific sales order
     */
    List<Payment> findBySalesOrderId(Long salesOrderId);

    /**
     * Find all payments for a specific customer (paginated)
     */
    Page<Payment> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Find payments by status
     */
    Page<Payment> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);

    /**
     * Find payments by method
     */
    Page<Payment> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);

    /**
     * Find payments with filters (paginated)
     */
    @Query("SELECT p FROM Payment p WHERE " +
            "(:search IS NULL OR LOWER(p.paymentNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.referenceNumber) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:customerId IS NULL OR p.customer.id = :customerId) AND " +
            "(:salesOrderId IS NULL OR p.salesOrder.id = :salesOrderId) AND " +
            "(:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod) AND " +
            "(:paymentStatus IS NULL OR p.paymentStatus = :paymentStatus) AND " +
            "(:startDate IS NULL OR p.paymentDate >= :startDate) AND " +
            "(:endDate IS NULL OR p.paymentDate <= :endDate)")
    Page<Payment> findAllWithFilters(
            @Param("search") String search,
            @Param("customerId") Long customerId,
            @Param("salesOrderId") Long salesOrderId,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    /**
     * Check if payment number already exists
     */
    boolean existsByPaymentNumber(String paymentNumber);

    /**
     * Count payments by payment_date for sequence generation
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentDate = :paymentDate")
    long countByPaymentDate(@Param("paymentDate") LocalDate paymentDate);

    /**
     * Get total amount paid for a specific sales order
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
            "WHERE p.salesOrder.id = :salesOrderId AND p.paymentStatus = 'COMPLETED'")
    java.math.BigDecimal getTotalPaidBySalesOrderId(@Param("salesOrderId") Long salesOrderId);
}