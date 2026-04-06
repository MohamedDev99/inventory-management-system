package com.moeware.ims.mapper.transaction;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.moeware.ims.dto.transaction.salesOrder.SalesOrderItemResponse;
import com.moeware.ims.dto.transaction.salesOrder.SalesOrderResponse;
import com.moeware.ims.dto.transaction.salesOrder.SalesOrderSummaryResponse;
import com.moeware.ims.entity.transaction.SalesOrder;
import com.moeware.ims.entity.transaction.SalesOrderItem;

/**
 * Mapper for converting SalesOrder and SalesOrderItem entities to their
 * response DTOs.
 *
 * <ul>
 * <li>{@link #toResponse(SalesOrder)} — full detail DTO</li>
 * <li>{@link #toSummaryResponse(SalesOrder)} — lightweight list DTO</li>
 * <li>{@link #toItemResponse(SalesOrderItem)} — single line-item DTO</li>
 * </ul>
 */
@Component
public class SalesOrderMapper {

    /**
     * Convert a SalesOrder entity to a full {@link SalesOrderResponse}.
     * Includes all line items and nested summaries.
     *
     * @param so SalesOrder entity (must not be null)
     * @return SalesOrderResponse
     */
    public SalesOrderResponse toResponse(SalesOrder so) {
        if (so == null)
            return null;

        List<SalesOrderItemResponse> itemResponses = so.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return SalesOrderResponse.builder()
                .id(so.getId())
                .soNumber(so.getSoNumber())
                .customer(SalesOrderResponse.CustomerSummary.builder()
                        .id(so.getCustomer().getId())
                        .customerCode(so.getCustomer().getCustomerCode())
                        .contactName(so.getCustomer().getContactName())
                        .email(so.getCustomer().getEmail())
                        .build())
                .customerName(so.getCustomerName())
                .customerEmail(so.getCustomerEmail())
                .customerPhone(so.getCustomerPhone())
                .shippingAddress(so.getShippingAddress())
                .city(so.getCity())
                .postalCode(so.getPostalCode())
                .warehouse(SalesOrderResponse.WarehouseSummary.builder()
                        .id(so.getWarehouse().getId())
                        .name(so.getWarehouse().getName())
                        .code(so.getWarehouse().getCode())
                        .build())
                .createdByUser(SalesOrderResponse.UserSummary.builder()
                        .id(so.getCreatedByUser().getId())
                        .username(so.getCreatedByUser().getUsername())
                        .email(so.getCreatedByUser().getEmail())
                        .build())
                .status(so.getStatus())
                .orderDate(so.getOrderDate())
                .fulfillmentDate(so.getFulfillmentDate())
                .shippingDate(so.getShippingDate())
                .deliveryDate(so.getDeliveryDate())
                .subtotal(so.getSubtotal())
                .taxAmount(so.getTaxAmount())
                .shippingCost(so.getShippingCost())
                .totalAmount(so.getTotalAmount())
                .notes(so.getNotes())
                .itemCount(so.getItems().size())
                .items(itemResponses)
                .version(so.getVersion())
                .createdAt(so.getCreatedAt())
                .updatedAt(so.getUpdatedAt())
                .createdBy(so.getCreatedBy())
                .updatedBy(so.getUpdatedBy())
                .build();
    }

    /**
     * Convert a SalesOrder entity to a lightweight
     * {@link SalesOrderSummaryResponse}.
     * Used for paginated list endpoints where full item detail is not needed.
     *
     * @param so SalesOrder entity (must not be null)
     * @return SalesOrderSummaryResponse
     */
    public SalesOrderSummaryResponse toSummaryResponse(SalesOrder so) {
        if (so == null)
            return null;

        return SalesOrderSummaryResponse.builder()
                .id(so.getId())
                .soNumber(so.getSoNumber())
                .customerId(so.getCustomer().getId())
                .customerCode(so.getCustomer().getCustomerCode())
                .customerName(so.getCustomerName())
                .customerEmail(so.getCustomerEmail())
                .warehouseId(so.getWarehouse().getId())
                .warehouseName(so.getWarehouse().getName())
                .status(so.getStatus())
                .orderDate(so.getOrderDate())
                .fulfillmentDate(so.getFulfillmentDate())
                .shippingDate(so.getShippingDate())
                .deliveryDate(so.getDeliveryDate())
                .itemCount(so.getItems().size())
                .subtotal(so.getSubtotal())
                .taxAmount(so.getTaxAmount())
                .shippingCost(so.getShippingCost())
                .totalAmount(so.getTotalAmount())
                .createdAt(so.getCreatedAt())
                .updatedAt(so.getUpdatedAt())
                .build();
    }

    /**
     * Convert a SalesOrderItem entity to a {@link SalesOrderItemResponse}.
     *
     * @param item SalesOrderItem entity (must not be null)
     * @return SalesOrderItemResponse
     */
    public SalesOrderItemResponse toItemResponse(SalesOrderItem item) {
        if (item == null)
            return null;

        return SalesOrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productSku(item.getProduct().getSku())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}