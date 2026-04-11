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
 * Repository interface for Invoice entity.
 * Manages invoice generation and tracking for sales orders.
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

        /**
         * Find invoice by unique invoice number.
         */
        Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

        /**
         * Find all invoices for a specific sales order.
         */
        List<Invoice> findBySalesOrderId(Long salesOrderId);

        /**
         * Find all invoices for a specific customer.
         */
        Page<Invoice> findByCustomerId(Long customerId, Pageable pageable);

        /**
         * Find invoices by status.
         */
        Page<Invoice> findByInvoiceStatus(InvoiceStatus invoiceStatus, Pageable pageable);

        /**
         * Find overdue invoices — those past their due date and not in a terminal
         * status.
         * <p>
         * Uses typed {@link InvoiceStatus} enum parameters instead of string literals
         * so
         * that any rename of an enum value causes a compile error rather than a silent
         * query mismatch.
         *
         * @param currentDate      the reference date; invoices with {@code dueDate}
         *                         before
         *                         this value are considered overdue
         * @param excludedStatuses terminal statuses to exclude (typically PAID and
         *                         CANCELLED)
         * @return invoices that are overdue and not in any of the excluded statuses
         */
        @Query("SELECT i FROM Invoice i " +
                        "WHERE i.invoiceStatus NOT IN :excludedStatuses " +
                        "AND i.dueDate < :currentDate")
        List<Invoice> findOverdueInvoices(
                        @Param("currentDate") LocalDate currentDate,
                        @Param("excludedStatuses") List<InvoiceStatus> excludedStatuses);

        /**
         * Convenience overload that excludes {@link InvoiceStatus#PAID} and
         * {@link InvoiceStatus#CANCELLED} — the standard definition of overdue.
         *
         * @param currentDate the reference date
         * @return overdue invoices excluding PAID and CANCELLED
         */
        default List<Invoice> findOverdueInvoices(LocalDate currentDate) {
                return findOverdueInvoices(currentDate,
                                List.of(InvoiceStatus.PAID, InvoiceStatus.CANCELLED));
        }

        /**
         * Count overdue invoices without loading entity data.
         * <p>
         * Preferred over {@code findOverdueInvoices(date).size()} for dashboard
         * aggregations where only the count is needed.
         *
         * @param currentDate      the reference date
         * @param excludedStatuses terminal statuses to exclude
         * @return count of invoices past their due date and not in any excluded status
         */
        @Query("SELECT COUNT(i) FROM Invoice i " +
                        "WHERE i.invoiceStatus NOT IN :excludedStatuses " +
                        "AND i.dueDate < :currentDate")
        long countOverdueInvoices(
                        @Param("currentDate") LocalDate currentDate,
                        @Param("excludedStatuses") List<InvoiceStatus> excludedStatuses);

        /**
         * Convenience overload that counts overdue invoices excluding
         * {@link InvoiceStatus#PAID} and {@link InvoiceStatus#CANCELLED}.
         *
         * @param currentDate the reference date
         * @return count of standard overdue invoices
         */
        default long countOverdueInvoices(LocalDate currentDate) {
                return countOverdueInvoices(currentDate,
                                List.of(InvoiceStatus.PAID, InvoiceStatus.CANCELLED));
        }

        /**
         * Find invoices with filters (paginated).
         */
        @Query("SELECT i FROM Invoice i WHERE " +
                        "(:search IS NULL OR LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                        "(:customerId IS NULL OR i.customer.id = :customerId) AND " +
                        "(:salesOrderId IS NULL OR i.salesOrder.id = :salesOrderId) AND " +
                        "(:invoiceStatus IS NULL OR i.invoiceStatus = :invoiceStatus) AND " +
                        "(:startDate IS NULL OR i.invoiceDate >= :startDate) AND " +
                        "(:endDate IS NULL OR i.invoiceDate <= :endDate) AND " +
                        "(:dueDate IS NULL OR i.dueDate <= :dueDate) AND " +
                        "(:overdue IS NULL OR (:overdue = true AND i.invoiceStatus NOT IN :overdueExcluded AND i.dueDate < CURRENT_DATE))")
        Page<Invoice> findAllWithFilters(
                        @Param("search") String search,
                        @Param("customerId") Long customerId,
                        @Param("salesOrderId") Long salesOrderId,
                        @Param("invoiceStatus") InvoiceStatus invoiceStatus,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("dueDate") LocalDate dueDate,
                        @Param("overdue") Boolean overdue,
                        @Param("overdueExcluded") List<InvoiceStatus> overdueExcluded,
                        Pageable pageable);

        /**
         * Convenience overload with standard overdue-excluded statuses
         * (PAID and CANCELLED). Callers that do not need to customise the excluded
         * set should prefer this overload.
         */
        default Page<Invoice> findAllWithFilters(
                        String search,
                        Long customerId,
                        Long salesOrderId,
                        InvoiceStatus invoiceStatus,
                        LocalDate startDate,
                        LocalDate endDate,
                        LocalDate dueDate,
                        Boolean overdue,
                        Pageable pageable) {
                return findAllWithFilters(search, customerId, salesOrderId, invoiceStatus,
                                startDate, endDate, dueDate, overdue,
                                List.of(InvoiceStatus.PAID, InvoiceStatus.CANCELLED),
                                pageable);
        }

        /**
         * Check if an invoice number already exists.
         */
        boolean existsByInvoiceNumber(String invoiceNumber);

        /**
         * Check if an invoice already exists for a specific sales order.
         */
        boolean existsBySalesOrderId(Long salesOrderId);

        /**
         * Count invoices by invoice date — used for PO number sequence generation.
         */
        @Query("SELECT COUNT(i) FROM Invoice i WHERE i.invoiceDate = :invoiceDate")
        long countByInvoiceDate(@Param("invoiceDate") LocalDate invoiceDate);

        /**
         * Get invoices with a remaining balance due that have not been cancelled.
         * <p>
         * Uses a typed enum parameter instead of the string literal
         * {@code 'CANCELLED'}.
         */
        @Query("SELECT i FROM Invoice i " +
                        "WHERE i.balanceDue > 0 " +
                        "AND i.invoiceStatus != :excludedStatus")
        Page<Invoice> findUnpaidInvoices(
                        @Param("excludedStatus") InvoiceStatus excludedStatus,
                        Pageable pageable);

        /**
         * Convenience overload that excludes {@link InvoiceStatus#CANCELLED}.
         */
        default Page<Invoice> findUnpaidInvoices(Pageable pageable) {
                return findUnpaidInvoices(InvoiceStatus.CANCELLED, pageable);
        }
}