package com.moeware.ims.mapper.transaction;

import org.springframework.stereotype.Component;

import com.moeware.ims.dto.transaction.invoice.InvoiceResponse;
import com.moeware.ims.entity.staff.Customer;
import com.moeware.ims.entity.transaction.Invoice;

/**
 * Mapper for converting Invoice entities to {@link InvoiceResponse} DTOs.
 *
 * <ul>
 * <li>{@link #toResponse(Invoice)} — full invoice DTO including nested
 * customer, sales order, and generated-by summaries</li>
 * </ul>
 */
@Component
public class InvoiceMapper {

    /**
     * Convert an Invoice entity to a full {@link InvoiceResponse}.
     *
     * @param i Invoice entity (must not be null)
     * @return InvoiceResponse
     */
    public InvoiceResponse toResponse(Invoice i) {
        if (i == null)
            return null;

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