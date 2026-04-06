package com.moeware.ims.service.staff;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.staff.customer.CustomerCreateRequest;
import com.moeware.ims.dto.staff.customer.CustomerResponse;
import com.moeware.ims.dto.staff.customer.CustomerStatementResponse;
import com.moeware.ims.dto.staff.customer.CustomerUpdateRequest;
import com.moeware.ims.dto.transaction.invoice.InvoiceResponse;
import com.moeware.ims.dto.transaction.payment.PaymentResponse;
import com.moeware.ims.dto.transaction.salesOrder.SalesOrderSummaryResponse;
import com.moeware.ims.entity.staff.Customer;
import com.moeware.ims.entity.transaction.Invoice;
import com.moeware.ims.entity.transaction.Payment;
import com.moeware.ims.entity.transaction.SalesOrder;
import com.moeware.ims.enums.transaction.InvoiceStatus;
import com.moeware.ims.enums.transaction.PaymentMethod;
import com.moeware.ims.enums.transaction.PaymentStatus;
import com.moeware.ims.enums.transaction.SalesOrderStatus;
import com.moeware.ims.exception.staff.customer.CustomerAlreadyExistsException;
import com.moeware.ims.exception.staff.customer.CustomerAlreadyExistsException.ConflictField;
import com.moeware.ims.exception.staff.customer.CustomerHasPendingOrdersException;
import com.moeware.ims.exception.staff.customer.CustomerNotFoundException;
import com.moeware.ims.exception.staff.customer.InvalidCustomerTypeException;
import com.moeware.ims.mapper.staff.customer.CustomerMapper;
import com.moeware.ims.mapper.transaction.InvoiceMapper;
import com.moeware.ims.mapper.transaction.PaymentMapper;
import com.moeware.ims.mapper.transaction.SalesOrderMapper;
import com.moeware.ims.repository.staff.CustomerRepository;
import com.moeware.ims.repository.transaction.InvoiceRepository;
import com.moeware.ims.repository.transaction.PaymentRepository;
import com.moeware.ims.repository.transaction.SalesOrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for Customer management.
 * Implements business logic for customer operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    /**
     * Get all customers with pagination and filters.
     *
     * @param customerType Filter by customer type
     * @param isActive     Filter by active status
     * @param city         Filter by city
     * @param country      Filter by country
     * @param searchTerm   Search term
     * @param pageable     Pagination information
     * @return Page of CustomerResponse
     */
    public Page<CustomerResponse> getAllCustomers(
            String customerType,
            Boolean isActive,
            String city,
            String country,
            String searchTerm,
            Pageable pageable) {

        log.debug("Fetching customers with filters - type: {}, active: {}, city: {}, country: {}, search: {}",
                customerType, isActive, city, country, searchTerm);

        return customerRepository
                .findCustomersWithFilters(customerType, isActive, city, country, searchTerm, pageable)
                .map(customerMapper::toResponse);
    }

    /**
     * Get customer by ID.
     *
     * @param id Customer ID
     * @return CustomerResponse
     * @throws CustomerNotFoundException if customer not found
     */
    public CustomerResponse getCustomerById(Long id) {
        log.debug("Fetching customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return customerMapper.toResponse(customer);
    }

    /**
     * Get customer by customer code.
     *
     * @param customerCode Customer code
     * @return CustomerResponse
     * @throws CustomerNotFoundException if customer not found
     */
    public CustomerResponse getCustomerByCode(String customerCode) {
        log.debug("Fetching customer with code: {}", customerCode);

        Customer customer = customerRepository.findByCustomerCode(customerCode)
                .orElseThrow(() -> new CustomerNotFoundException(customerCode));

        return customerMapper.toResponse(customer);
    }

    /**
     * Create a new customer.
     *
     * @param request Customer creation payload
     * @return Created CustomerResponse
     * @throws CustomerAlreadyExistsException if customer code or email already
     *                                        exists
     */
    @Transactional
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        log.info("Creating new customer with code: {}", request.getCustomerCode());

        // Validate unique constraints
        if (customerRepository.existsByCustomerCode(request.getCustomerCode())) {
            throw new CustomerAlreadyExistsException(ConflictField.CODE, request.getCustomerCode());
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistsException(ConflictField.EMAIL, request.getEmail());
        }

        // customerType is validated by @Pattern in the DTO, but double-check here for
        // safety
        validateCustomerType(request.getCustomerType());

        Customer customer = customerMapper.toEntity(request);
        Customer saved = customerRepository.save(customer);

        log.info("Successfully created customer with ID: {} and code: {}", saved.getId(), saved.getCustomerCode());

        return customerMapper.toResponse(saved);
    }

    /**
     * Full update of an existing customer.
     *
     * @param id      Customer ID
     * @param request Update payload
     * @return Updated CustomerResponse
     * @throws CustomerNotFoundException      if customer not found
     * @throws CustomerAlreadyExistsException if email conflicts with another
     *                                        customer
     */
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request) {
        log.info("Updating customer with ID: {}", id);

        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        // Check for duplicate email (excluding current customer)
        if (request.getEmail() != null
                && !existing.getEmail().equals(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistsException(ConflictField.EMAIL, request.getEmail());
        }

        if (request.getCustomerType() != null) {
            validateCustomerType(request.getCustomerType());
        }

        customerMapper.applyUpdate(existing, request);
        Customer updated = customerRepository.save(existing);

        log.info("Successfully updated customer with ID: {}", id);

        return customerMapper.toResponse(updated);
    }

    /**
     * Partial update of an existing customer.
     * Only non-null fields in the request are applied.
     *
     * @param id      Customer ID
     * @param request Partial update payload
     * @return Updated CustomerResponse
     * @throws CustomerNotFoundException if customer not found
     */
    @Transactional
    public CustomerResponse patchCustomer(Long id, CustomerUpdateRequest request) {
        log.info("Partially updating customer with ID: {}", id);

        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        // Validate unique email constraint only when email is being changed
        if (request.getEmail() != null
                && !existing.getEmail().equals(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistsException(ConflictField.EMAIL, request.getEmail());
        }

        if (request.getCustomerType() != null) {
            validateCustomerType(request.getCustomerType());
        }

        customerMapper.applyUpdate(existing, request);
        Customer updated = customerRepository.save(existing);

        log.info("Successfully patched customer with ID: {}", id);

        return customerMapper.toResponse(updated);
    }

    /**
     * Soft delete a customer (deactivate).
     * Blocked if the customer has any non-final sales orders.
     *
     * @param id Customer ID
     * @throws CustomerNotFoundException         if customer not found
     * @throws CustomerHasPendingOrdersException if active orders exist
     */
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Soft deleting customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        // Probe for at least one non-final order (cheap check)
        Page<SalesOrder> probe = salesOrderRepository.findByCustomerIdWithFilters(
                id, null, null, null, Pageable.ofSize(1));

        boolean hasActiveOrders = probe.stream().anyMatch(this::isActiveOrder);

        if (hasActiveOrders) {
            // Full count for the error message
            long totalActive = salesOrderRepository
                    .findByCustomerIdWithFilters(id, null, null, null, Pageable.unpaged())
                    .stream()
                    .filter(this::isActiveOrder)
                    .count();

            throw new CustomerHasPendingOrdersException(id, (int) totalActive);
        }

        customer.setIsActive(false);
        customerRepository.save(customer);

        log.info("Successfully deactivated customer with ID: {}", id);
    }

    // ── Sub-resource queries ──────────────────────────────────────────────────

    public Page<SalesOrderSummaryResponse> getCustomerOrders(
            Long customerId,
            SalesOrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        // Verify customer exists first
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        return salesOrderRepository
                .findByCustomerIdWithFilters(customerId, status, startDate, endDate, pageable)
                .map(salesOrderMapper::toSummaryResponse);
    }

    public Page<InvoiceResponse> getCustomerInvoices(
            Long customerId,
            InvoiceStatus invoiceStatus,
            LocalDate startDate,
            LocalDate endDate,
            Boolean overdue,
            Pageable pageable) {

        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        return invoiceRepository
                .findAllWithFilters(null, customerId, null, invoiceStatus,
                        startDate, endDate, null, overdue, pageable)
                .map(invoiceMapper::toResponse);
    }

    public Page<PaymentResponse> getCustomerPayments(
            Long customerId,
            PaymentStatus paymentStatus,
            PaymentMethod paymentMethod,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        return paymentRepository
                .findAllWithFilters(null, customerId, null, paymentMethod,
                        paymentStatus, startDate, endDate, pageable)
                .map(paymentMapper::toResponse);
    }

    public CustomerStatementResponse getCustomerStatement(
            Long customerId,
            LocalDate startDate,
            LocalDate endDate) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        // Resolve defaults: last 30 days when no range is provided
        LocalDate resolvedEnd = endDate != null ? endDate : LocalDate.now();
        LocalDate resolvedStart = startDate != null ? startDate : resolvedEnd.minusDays(30);

        // Fetch raw data for the period
        List<Invoice> invoices = invoiceRepository
                .findAllWithFilters(null, customerId, null, null,
                        resolvedStart, resolvedEnd, null, null, Pageable.unpaged())
                .getContent();

        List<Payment> payments = paymentRepository
                .findAllWithFilters(null, customerId, null, null,
                        null, resolvedStart, resolvedEnd, Pageable.unpaged())
                .getContent();

        // Build chronological transaction ledger
        List<CustomerStatementResponse.Transaction> transactions = new ArrayList<>();

        BigDecimal runningBalance = BigDecimal.ZERO;

        // Merge and sort by date
        List<Object> combined = new ArrayList<>();
        combined.addAll(invoices);
        combined.addAll(payments);
        combined.sort(Comparator.comparing(e -> {
            if (e instanceof Invoice i)
                return i.getInvoiceDate().atStartOfDay();
            return ((Payment) e).getPaymentDate().atStartOfDay();
        }));

        for (Object entry : combined) {
            if (entry instanceof Invoice i) {
                runningBalance = runningBalance.add(i.getTotalAmount());
                transactions.add(CustomerStatementResponse.Transaction.builder()
                        .date(i.getInvoiceDate())
                        .type("INVOICE")
                        .referenceNumber(i.getInvoiceNumber())
                        .description("Sales Order " + i.getSalesOrder().getSoNumber())
                        .debit(i.getTotalAmount())
                        .credit(BigDecimal.ZERO)
                        .balance(runningBalance)
                        .build());
            } else if (entry instanceof Payment p
                    && p.getPaymentStatus() == PaymentStatus.COMPLETED) {
                runningBalance = runningBalance.subtract(p.getAmount());
                transactions.add(CustomerStatementResponse.Transaction.builder()
                        .date(p.getPaymentDate())
                        .type("PAYMENT")
                        .referenceNumber(p.getPaymentNumber())
                        .description("Payment via " + p.getPaymentMethod().name())
                        .debit(BigDecimal.ZERO)
                        .credit(p.getAmount())
                        .balance(runningBalance)
                        .build());
            }
        }

        BigDecimal totalInvoiced = invoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = payments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal creditLimit = customer.getCreditLimit() != null ? customer.getCreditLimit() : BigDecimal.ZERO;
        BigDecimal availableCredit = creditLimit.subtract(runningBalance).max(BigDecimal.ZERO);

        return CustomerStatementResponse.builder()
                .customerId(customer.getId())
                .customerName(customer.getContactName())
                .statementPeriod(CustomerStatementResponse.Period.builder()
                        .startDate(resolvedStart)
                        .endDate(resolvedEnd)
                        .build())
                .openingBalance(BigDecimal.ZERO) // would be calculated from pre-period invoices in production
                .transactions(transactions)
                .closingBalance(runningBalance)
                .totalInvoiced(totalInvoiced)
                .totalPaid(totalPaid)
                .creditLimit(creditLimit)
                .availableCredit(availableCredit)
                .build();
    }

    // -------------------------------------------------------------------------
    // Segmented listing helpers
    // -------------------------------------------------------------------------

    public Page<CustomerResponse> getRetailCustomers(Pageable pageable) {
        log.debug("Fetching retail customers");
        return customerRepository.findRetailCustomers(pageable).map(customerMapper::toResponse);
    }

    public Page<CustomerResponse> getWholesaleCustomers(Pageable pageable) {
        log.debug("Fetching wholesale customers");
        return customerRepository.findWholesaleCustomers(pageable).map(customerMapper::toResponse);
    }

    public Page<CustomerResponse> getCorporateCustomers(Pageable pageable) {
        log.debug("Fetching corporate customers");
        return customerRepository.findCorporateCustomers(pageable).map(customerMapper::toResponse);
    }

    public Page<CustomerResponse> searchCustomers(String searchTerm, Pageable pageable) {
        log.debug("Searching customers with term: {}", searchTerm);
        return customerRepository.searchCustomers(searchTerm, pageable).map(customerMapper::toResponse);
    }

    // -------------------------------------------------------------------------
    // Count helpers
    // -------------------------------------------------------------------------

    public Long getActiveCustomerCount() {
        return customerRepository.countActiveCustomers();
    }

    public Long getCustomerCountByType(String customerType) {
        validateCustomerType(customerType);
        return customerRepository.countByCustomerType(customerType);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Returns true if the order is in a non-final state (i.e. still active).
     */
    private boolean isActiveOrder(SalesOrder order) {
        SalesOrderStatus status = order.getStatus();
        return status != SalesOrderStatus.CONFIRMED
                && status != SalesOrderStatus.CANCELLED
                && status != SalesOrderStatus.DELIVERED;
    }

    /**
     * Validate that customerType is one of the accepted values.
     *
     * @param customerType Customer type string
     * @throws InvalidCustomerTypeException if value is unrecognised
     */
    private void validateCustomerType(String customerType) {
        if (customerType == null) {
            return;
        }
        String type = customerType.toUpperCase();
        if (!type.equals("RETAIL") && !type.equals("WHOLESALE") && !type.equals("CORPORATE")) {
            throw new InvalidCustomerTypeException(customerType);
        }
    }

}