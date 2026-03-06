package com.moeware.ims.mapper.transaction;

import com.moeware.ims.dto.transaction.purchaseOrder.PurchaseOrderResponse;
import com.moeware.ims.dto.transaction.purchaseOrder.PurchaseOrderItemResponse;
import com.moeware.ims.entity.transaction.PurchaseOrder;
import com.moeware.ims.entity.transaction.PurchaseOrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper utility for converting between PurchaseOrder entity and DTO
 */
@Component
public class PurchaseOrderMapper {

    /**
     * Convert PurchaseOrder entity to DTO
     *
     * @param purchaseOrder PurchaseOrder entity
     * @return PurchaseOrderResponse
     */
    public PurchaseOrderResponse toDTO(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            return null;
        }

        return PurchaseOrderResponse.builder()
                .id(purchaseOrder.getId())
                .poNumber(purchaseOrder.getPoNumber())
                .supplier(mapSupplierSummary(purchaseOrder))
                .warehouse(mapWarehouseSummary(purchaseOrder))
                .createdByUser(mapUserSummary(purchaseOrder))
                .status(purchaseOrder.getStatus())
                .orderDate(purchaseOrder.getOrderDate())
                .expectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate())
                .actualDeliveryDate(purchaseOrder.getActualDeliveryDate())
                .subtotal(purchaseOrder.getSubtotal())
                .taxAmount(purchaseOrder.getTaxAmount())
                .discountAmount(purchaseOrder.getDiscountAmount())
                .totalAmount(purchaseOrder.getTotalAmount())
                .notes(purchaseOrder.getNotes())
                .itemCount(purchaseOrder.getItems() != null ? purchaseOrder.getItems().size() : 0)
                .createdAt(purchaseOrder.getCreatedAt())
                .updatedAt(purchaseOrder.getUpdatedAt())
                .version(purchaseOrder.getVersion())
                .build();
    }

    /**
     * Convert PurchaseOrder entity to DTO with items
     *
     * @param purchaseOrder PurchaseOrder entity
     * @return PurchaseOrderResponse with items
     */
    public PurchaseOrderResponse toDTOWithItems(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            return null;
        }

        PurchaseOrderResponse dto = toDTO(purchaseOrder);

        if (purchaseOrder.getItems() != null) {
            dto.setItems(purchaseOrder.getItems().stream()
                    .map(this::toItemDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * Convert PurchaseOrderItem entity to DTO
     *
     * @param item PurchaseOrderItem entity
     * @return PurchaseOrderItemResponse
     */
    public PurchaseOrderItemResponse toItemDTO(PurchaseOrderItem item) {
        if (item == null) {
            return null;
        }

        return PurchaseOrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productSku(item.getProduct().getSku())
                .productName(item.getProduct().getName())
                .quantityOrdered(item.getQuantityOrdered())
                .quantityReceived(item.getQuantityReceived())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }

    // Helper methods for mapping nested entities
    private PurchaseOrderResponse.SupplierSummary mapSupplierSummary(PurchaseOrder po) {
        if (po.getSupplier() == null) {
            return null;
        }
        return PurchaseOrderResponse.SupplierSummary.builder()
                .id(po.getSupplier().getId())
                .name(po.getSupplier().getName())
                .code(po.getSupplier().getCode())
                .build();
    }

    private PurchaseOrderResponse.WarehouseSummary mapWarehouseSummary(PurchaseOrder po) {
        if (po.getWarehouse() == null) {
            return null;
        }
        return PurchaseOrderResponse.WarehouseSummary.builder()
                .id(po.getWarehouse().getId())
                .name(po.getWarehouse().getName())
                .code(po.getWarehouse().getCode())
                .build();
    }

    private PurchaseOrderResponse.UserSummary mapUserSummary(PurchaseOrder po) {
        if (po.getCreatedByUser() == null) {
            return null;
        }
        return PurchaseOrderResponse.UserSummary.builder()
                .id(po.getCreatedByUser().getId())
                .username(po.getCreatedByUser().getUsername())
                .email(po.getCreatedByUser().getEmail())
                .build();
    }
}