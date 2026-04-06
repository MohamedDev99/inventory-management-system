package com.moeware.ims.mapper.transaction;

import org.springframework.stereotype.Component;

import com.moeware.ims.dto.transaction.payment.PaymentResponse;
import com.moeware.ims.entity.transaction.Payment;

/**
 * Mapper for converting Payment entities to {@link PaymentResponse} DTOs.
 *
 * <ul>
 * <li>{@link #toResponse(Payment)} — full payment DTO including nested
 * customer, sales order, and processed-by summaries</li>
 * </ul>
 */
@Component
public class PaymentMapper {

    /**
     * Convert a Payment entity to a full {@link PaymentResponse}.
     * The salesOrder summary is omitted when the payment has no linked order
     * (e.g. advance payment or account credit).
     *
     * @param p Payment entity (must not be null)
     * @return PaymentResponse
     */
    public PaymentResponse toResponse(Payment p) {
        if (p == null)
            return null;

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