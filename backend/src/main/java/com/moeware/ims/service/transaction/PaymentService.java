package com.moeware.ims.service.transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.transaction.payment.PaymentRequest;
import com.moeware.ims.dto.transaction.payment.PaymentResponse;
import com.moeware.ims.dto.transaction.payment.RefundPaymentRequest;
import com.moeware.ims.dto.transaction.payment.UpdatePaymentStatusRequest;
import com.moeware.ims.entity.User;
import com.moeware.ims.entity.staff.Customer;
import com.moeware.ims.entity.transaction.Payment;
import com.moeware.ims.entity.transaction.SalesOrder;
import com.moeware.ims.enums.transaction.PaymentMethod;
import com.moeware.ims.enums.transaction.PaymentStatus;
import com.moeware.ims.exception.ResourceNotFoundException;
import com.moeware.ims.exception.transaction.payment.PaymentNotFoundException;
import com.moeware.ims.repository.UserRepository;
import com.moeware.ims.repository.staff.CustomerRepository;
import com.moeware.ims.repository.transaction.PaymentRepository;
import com.moeware.ims.repository.transaction.SalesOrderRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing payment transactions.
 * Handles payment recording, status transitions, and refunds.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    // ─── READ ────────────────────────────────────────────────────────────────

    /**
     * Returns a paginated, filtered list of all payments.
     */
    public Page<PaymentResponse> getAllPayments(
            String search,
            Long customerId,
            Long salesOrderId,
            PaymentMethod paymentMethod,
            PaymentStatus paymentStatus,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        return paymentRepository.findAllWithFilters(
                search, customerId, salesOrderId, paymentMethod,
                paymentStatus, startDate, endDate, pageable)
                .map(this::toResponse);
    }

    /**
     * Returns a single payment by its internal ID.
     */
    public PaymentResponse getPaymentById(Long id) {
        return toResponse(findPaymentOrThrow(id));
    }

    /**
     * Returns all available payment methods.
     */
    public List<String> getPaymentMethods() {
        return Arrays.stream(PaymentMethod.values())
                .map(Enum::name)
                .toList();
    }

    // ─── WRITE ───────────────────────────────────────────────────────────────

    /**
     * Records a new payment transaction.
     * Links to the sales order and customer, generates a unique payment number.
     */
    @Transactional
    public PaymentResponse recordPayment(PaymentRequest request, Long processedByUserId) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer", "id", request.getCustomerId()));

        SalesOrder salesOrder = null;
        if (request.getSalesOrderId() != null) {
            salesOrder = salesOrderRepository.findById(request.getSalesOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "SalesOrder", "id", request.getSalesOrderId()));
        }

        User processedBy = userRepository.findById(processedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "id", processedByUserId));

        String paymentNumber = generatePaymentNumber(request.getPaymentDate());

        Payment payment = Payment.builder()
                .paymentNumber(paymentNumber)
                .salesOrder(salesOrder)
                .customer(customer)
                .paymentDate(request.getPaymentDate())
                .paymentMethod(request.getPaymentMethod())
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .referenceNumber(request.getReferenceNumber())
                .notes(request.getNotes())
                .paymentStatus(PaymentStatus.PENDING)
                .processedBy(processedBy)
                .build();

        return toResponse(paymentRepository.save(payment));
    }

    /**
     * Updates the status of an existing payment.
     */
    @Transactional
    public PaymentResponse updatePaymentStatus(Long id, UpdatePaymentStatusRequest request) {
        Payment payment = findPaymentOrThrow(id);

        if (payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalStateException("Cannot change status of a refunded payment.");
        }

        payment.setPaymentStatus(request.getPaymentStatus());
        if (request.getNotes() != null) {
            payment.setNotes(request.getNotes());
        }

        return toResponse(paymentRepository.save(payment));
    }

    /**
     * Processes a refund for a completed payment.
     * Validates that the refund amount does not exceed the original payment amount.
     */
    @Transactional
    public PaymentResponse processRefund(Long id, RefundPaymentRequest request, Long processedByUserId) {
        Payment payment = findPaymentOrThrow(id);

        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Can only refund COMPLETED payments. Current status: "
                            + payment.getPaymentStatus());
        }

        if (request.getRefundAmount().compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Refund amount (" + request.getRefundAmount()
                            + ") cannot exceed the original payment amount (" + payment.getAmount() + ").");
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.setNotes(
                (payment.getNotes() != null ? payment.getNotes() + " | " : "")
                        + "REFUND: " + request.getReason()
                        + (request.getNotes() != null ? " | " + request.getNotes() : ""));

        return toResponse(paymentRepository.save(payment));
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private Payment findPaymentOrThrow(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    /**
     * Generates a unique payment number in the format PAY-YYYYMMDD-XXXX.
     * The sequence resets daily.
     */
    private String generatePaymentNumber(LocalDate paymentDate) {
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

    private PaymentResponse toResponse(Payment p) {
        PaymentResponse.SalesOrderSummary soSummary = null;
        if (p.getSalesOrder() != null) {
            soSummary = PaymentResponse.SalesOrderSummary.builder()
                    .id(p.getSalesOrder().getId())
                    .soNumber(p.getSalesOrder().getSoNumber())
                    .totalAmount(p.getSalesOrder().getTotalAmount())
                    .build();
        }

        return PaymentResponse.builder()
                .id(p.getId())
                .paymentNumber(p.getPaymentNumber())
                .salesOrder(soSummary)
                .customer(PaymentResponse.CustomerSummary.builder()
                        .id(p.getCustomer().getId())
                        .customerCode(p.getCustomer().getCustomerCode())
                        .contactName(p.getCustomer().getContactName())
                        .email(p.getCustomer().getEmail())
                        .build())
                .paymentDate(p.getPaymentDate())
                .paymentMethod(p.getPaymentMethod())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .referenceNumber(p.getReferenceNumber())
                .paymentStatus(p.getPaymentStatus())
                .notes(p.getNotes())
                .processedBy(PaymentResponse.UserSummary.builder()
                        .id(p.getProcessedBy().getId())
                        .username(p.getProcessedBy().getUsername())
                        .email(p.getProcessedBy().getEmail())
                        .build())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .version(p.getVersion())
                .build();
    }
}