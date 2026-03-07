package com.moeware.ims.service.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.transaction.invoice.InvoiceRequest;
import com.moeware.ims.dto.transaction.invoice.InvoiceResponse;
import com.moeware.ims.dto.transaction.invoice.RecordInvoicePaymentRequest;
import com.moeware.ims.dto.transaction.invoice.UpdateInvoiceStatusRequest;
import com.moeware.ims.entity.transaction.Invoice;
import com.moeware.ims.entity.transaction.Payment;
import com.moeware.ims.entity.transaction.SalesOrder;
import com.moeware.ims.entity.staff.Customer;
import com.moeware.ims.entity.User;
import com.moeware.ims.enums.transaction.InvoiceStatus;
import com.moeware.ims.enums.transaction.PaymentStatus;
import com.moeware.ims.exception.transaction.invoice.InvoiceNotFoundException;
import com.moeware.ims.exception.ResourceNotFoundException;
import com.moeware.ims.repository.transaction.InvoiceRepository;
import com.moeware.ims.repository.transaction.PaymentRepository;
import com.moeware.ims.repository.transaction.SalesOrderRepository;
import com.moeware.ims.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing invoices linked to sales orders.
 * Handles generation, status tracking, sending, and payment recording.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    // ─── READ ────────────────────────────────────────────────────────────────

    /**
     * Returns a paginated, filtered list of all invoices.
     */
    public Page<InvoiceResponse> getAllInvoices(
            String search,
            Long customerId,
            Long salesOrderId,
            InvoiceStatus invoiceStatus,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate dueDate,
            Boolean overdue,
            Pageable pageable) {
        return invoiceRepository.findAllWithFilters(
                search, customerId, salesOrderId, invoiceStatus,
                startDate, endDate, dueDate, overdue, pageable)
                .map(this::toResponse);
    }

    /**
     * Returns a single invoice by its internal ID.
     */
    public InvoiceResponse getInvoiceById(Long id) {
        return toResponse(findInvoiceOrThrow(id));
    }

    /**
     * Returns all invoices whose due date has passed and are not fully paid or
     * cancelled.
     */
    public List<InvoiceResponse> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(LocalDate.now())
                .stream().map(this::toResponse).toList();
    }

    // ─── WRITE ───────────────────────────────────────────────────────────────

    /**
     * Generates a new invoice from a sales order.
     * Derives subtotal, tax, and total amounts directly from the sales order.
     * Only one invoice per sales order is allowed.
     */
    @Transactional
    public InvoiceResponse generateInvoice(InvoiceRequest request, Long generatedByUserId) {
        SalesOrder salesOrder = salesOrderRepository.findById(request.getSalesOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SalesOrder", "id", request.getSalesOrderId()));

        if (invoiceRepository.existsBySalesOrderId(salesOrder.getId())) {
            throw new IllegalStateException(
                    "An invoice already exists for sales order: " + salesOrder.getSoNumber());
        }

        User generatedBy = userRepository.findById(generatedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "id", generatedByUserId));

        String invoiceNumber = generateInvoiceNumber(request.getInvoiceDate());

        // Derive financial amounts from the sales order
        BigDecimal subtotal = salesOrder.getSubtotal();
        BigDecimal taxAmount = salesOrder.getTaxAmount();
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal totalAmount = salesOrder.getTotalAmount();

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .salesOrder(salesOrder)
                .customer(salesOrder.getCustomer())
                .invoiceDate(request.getInvoiceDate())
                .dueDate(request.getDueDate())
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .paidAmount(BigDecimal.ZERO)
                .balanceDue(totalAmount)
                .invoiceStatus(InvoiceStatus.DRAFT)
                .paymentTerms(request.getPaymentTerms())
                .notes(request.getNotes())
                .generatedBy(generatedBy)
                .build();

        return toResponse(invoiceRepository.save(invoice));
    }

    /**
     * Updates the status of an invoice and optionally records a paid amount.
     * Automatically calculates remaining balance when paidAmount is supplied.
     */
    @Transactional
    public InvoiceResponse updateInvoiceStatus(Long id, UpdateInvoiceStatusRequest request) {
        Invoice invoice = findInvoiceOrThrow(id);

        if (invoice.getInvoiceStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update status of a cancelled invoice.");
        }

        invoice.setInvoiceStatus(request.getInvoiceStatus());

        if (request.getPaidAmount() != null) {
            invoice.setPaidAmount(request.getPaidAmount());
            invoice.calculateBalanceDue();

            // Auto-resolve status when fully paid
            if (invoice.getBalanceDue().compareTo(BigDecimal.ZERO) == 0) {
                invoice.setInvoiceStatus(InvoiceStatus.PAID);
            } else if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                invoice.setInvoiceStatus(InvoiceStatus.PARTIAL);
            }
        }

        if (request.getNotes() != null) {
            invoice.setNotes(request.getNotes());
        }

        return toResponse(invoiceRepository.save(invoice));
    }

    /**
     * Marks the invoice as SENT (simulates email delivery to the customer).
     * In a production system this would trigger an email service.
     */
    @Transactional
    public InvoiceResponse sendInvoice(Long id) {
        Invoice invoice = findInvoiceOrThrow(id);

        if (invoice.getInvoiceStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Cannot send a cancelled invoice.");
        }
        if (invoice.getInvoiceStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice is already paid. No need to resend.");
        }

        // TODO: Integrate with email service (e.g., SendGrid, SES) to send PDF to
        // customer
        invoice.setInvoiceStatus(InvoiceStatus.SENT);
        return toResponse(invoiceRepository.save(invoice));
    }

    /**
     * Records a payment against an invoice, updating the paid amount and balance.
     * Also creates a Payment record for full audit trail.
     */
    @Transactional
    public InvoiceResponse recordPaymentForInvoice(Long id, RecordInvoicePaymentRequest request,
            Long processedByUserId) {
        Invoice invoice = findInvoiceOrThrow(id);

        if (invoice.getInvoiceStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Cannot record payment for a cancelled invoice.");
        }
        if (invoice.getInvoiceStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice is already fully paid.");
        }

        // Validate payment amount does not exceed balance due
        if (request.getPaymentAmount().compareTo(invoice.getBalanceDue()) > 0) {
            throw new IllegalArgumentException(
                    "Payment amount (" + request.getPaymentAmount()
                            + ") exceeds balance due (" + invoice.getBalanceDue() + ").");
        }

        User processedBy = userRepository.findById(processedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "id", processedByUserId));

        // Create a Payment record linked to the sales order
        String paymentNumber = generateLinkedPaymentNumber(request.getPaymentDate());
        Payment payment = Payment.builder()
                .paymentNumber(paymentNumber)
                .salesOrder(invoice.getSalesOrder())
                .customer(invoice.getCustomer())
                .paymentDate(request.getPaymentDate())
                .paymentMethod(request.getPaymentMethod())
                .amount(request.getPaymentAmount())
                .currency("USD")
                .referenceNumber(request.getReferenceNumber())
                .paymentStatus(PaymentStatus.COMPLETED)
                .processedBy(processedBy)
                .notes("Payment recorded for invoice: " + invoice.getInvoiceNumber())
                .build();
        paymentRepository.save(payment);

        // Update invoice paid amount and balance
        BigDecimal newPaidAmount = invoice.getPaidAmount().add(request.getPaymentAmount());
        invoice.setPaidAmount(newPaidAmount);
        invoice.calculateBalanceDue();

        // Update invoice status based on remaining balance
        if (invoice.getBalanceDue().compareTo(BigDecimal.ZERO) == 0) {
            invoice.setInvoiceStatus(InvoiceStatus.PAID);
        } else {
            invoice.setInvoiceStatus(InvoiceStatus.PARTIAL);
        }

        return toResponse(invoiceRepository.save(invoice));
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private Invoice findInvoiceOrThrow(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
    }

    /**
     * Generates a unique invoice number in the format INV-YYYYMMDD-XXXX.
     */
    private String generateInvoiceNumber(LocalDate invoiceDate) {
        LocalDate date = invoiceDate != null ? invoiceDate : LocalDate.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = invoiceRepository.countByInvoiceDate(date);
        String sequence = String.format("%04d", count + 1);
        String candidate = "INV-" + dateStr + "-" + sequence;

        while (invoiceRepository.existsByInvoiceNumber(candidate)) {
            count++;
            sequence = String.format("%04d", count + 1);
            candidate = "INV-" + dateStr + "-" + sequence;
        }
        return candidate;
    }

    /**
     * Generates a PAY- number for payments created via the invoice payment
     * endpoint.
     */
    private String generateLinkedPaymentNumber(LocalDate paymentDate) {
        LocalDate date = paymentDate != null ? paymentDate : LocalDate.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = paymentRepository.countByPaymentDate(date);
        String sequence = String.format("%04d", count + 1);
        String candidate = "PAY-" + dateStr + "-" + sequence;

        while (paymentRepository.existsByPaymentNumber(candidate)) {
            count++;
            sequence = String.format("%04d", count + 1);
            candidate = "PAY-" + dateStr + "-" + sequence;
        }
        return candidate;
    }

    private InvoiceResponse toResponse(Invoice i) {
        Customer c = i.getCustomer();

        return InvoiceResponse.builder()
                .id(i.getId())
                .invoiceNumber(i.getInvoiceNumber())
                .salesOrder(InvoiceResponse.SalesOrderSummary.builder()
                        .id(i.getSalesOrder().getId())
                        .soNumber(i.getSalesOrder().getSoNumber())
                        .orderDate(i.getSalesOrder().getOrderDate())
                        .build())
                .customer(InvoiceResponse.CustomerSummary.builder()
                        .id(c.getId())
                        .customerCode(c.getCustomerCode())
                        .contactName(c.getContactName())
                        .companyName(c.getCompanyName())
                        .email(c.getEmail())
                        .billingAddress(c.getBillingAddress())
                        .billingCity(c.getBillingCity())
                        .billingState(c.getBillingState())
                        .billingPostalCode(c.getBillingPostalCode())
                        .build())
                .invoiceDate(i.getInvoiceDate())
                .dueDate(i.getDueDate())
                .subtotal(i.getSubtotal())
                .taxAmount(i.getTaxAmount())
                .discountAmount(i.getDiscountAmount())
                .totalAmount(i.getTotalAmount())
                .paidAmount(i.getPaidAmount())
                .balanceDue(i.getBalanceDue())
                .invoiceStatus(i.getInvoiceStatus())
                .paymentTerms(i.getPaymentTerms())
                .notes(i.getNotes())
                .fileUrl(i.getFileUrl())
                .generatedBy(InvoiceResponse.UserSummary.builder()
                        .id(i.getGeneratedBy().getId())
                        .username(i.getGeneratedBy().getUsername())
                        .email(i.getGeneratedBy().getEmail())
                        .build())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .version(i.getVersion())
                .build();
    }
}