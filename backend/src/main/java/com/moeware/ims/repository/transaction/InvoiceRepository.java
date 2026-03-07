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

import com.moeware.ims.entity.transaction.Invoice;
import com.moeware.ims.enums.transaction.InvoiceStatus;

/**
 * Repository interface for Invoice entity
 * Manages invoice generation and tracking for sales orders
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Find invoice by unique invoice number
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    /**
     * Find all invoices for a specific sales order
     */
    List<Invoice> findBySalesOrderId(Long salesOrderId);

    /**
     * Find all invoices for a specific customer
     */
    Page<Invoice> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Find invoices by status
     */
    Page<Invoice> findByInvoiceStatus(InvoiceStatus invoiceStatus, Pageable pageable);

    /**
     * Find overdue invoices (past due date and not fully paid)
     */
    @Query("SELECT i FROM Invoice i WHERE i.invoiceStatus NOT IN ('PAID', 'CANCELLED') " +
            "AND i.dueDate < :currentDate")
    List<Invoice> findOverdueInvoices(@Param("currentDate") LocalDate currentDate);

    /**
     * Find invoices with filters (paginated)
     */
    @Query("SELECT i FROM Invoice i WHERE " +
            "(:search IS NULL OR LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:customerId IS NULL OR i.customer.id = :customerId) AND " +
            "(:salesOrderId IS NULL OR i.salesOrder.id = :salesOrderId) AND " +
            "(:invoiceStatus IS NULL OR i.invoiceStatus = :invoiceStatus) AND " +
            "(:startDate IS NULL OR i.invoiceDate >= :startDate) AND " +
            "(:endDate IS NULL OR i.invoiceDate <= :endDate) AND " +
            "(:dueDate IS NULL OR i.dueDate <= :dueDate) AND " +
            "(:overdue IS NULL OR (:overdue = true AND i.invoiceStatus NOT IN ('PAID', 'CANCELLED') AND i.dueDate < CURRENT_DATE))")
    Page<Invoice> findAllWithFilters(
            @Param("search") String search,
            @Param("customerId") Long customerId,
            @Param("salesOrderId") Long salesOrderId,
            @Param("invoiceStatus") InvoiceStatus invoiceStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("dueDate") LocalDate dueDate,
            @Param("overdue") Boolean overdue,
            Pageable pageable);

    /**
     * Check if invoice number already exists
     */
    boolean existsByInvoiceNumber(String invoiceNumber);

    /**
     * Check if an invoice already exists for a specific sales order
     */
    boolean existsBySalesOrderId(Long salesOrderId);

    /**
     * Count invoices by invoice_date for sequence generation
     */
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.invoiceDate = :invoiceDate")
    long countByInvoiceDate(@Param("invoiceDate") LocalDate invoiceDate);

    /**
     * Get invoices with balance due greater than zero
     */
    @Query("SELECT i FROM Invoice i WHERE i.balanceDue > 0 AND i.invoiceStatus != 'CANCELLED'")
    Page<Invoice> findUnpaidInvoices(Pageable pageable);
}