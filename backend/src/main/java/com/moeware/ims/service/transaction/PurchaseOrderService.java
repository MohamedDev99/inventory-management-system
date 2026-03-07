package com.moeware.ims.service.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.transaction.CancelOrderRequest;
import com.moeware.ims.dto.transaction.purchaseOrder.PurchaseOrderItemRequest;
import com.moeware.ims.dto.transaction.purchaseOrder.PurchaseOrderItemResponse;
import com.moeware.ims.dto.transaction.purchaseOrder.PurchaseOrderRequest;
import com.moeware.ims.dto.transaction.purchaseOrder.PurchaseOrderResponse;
import com.moeware.ims.dto.transaction.purchaseOrder.PurchaseOrderSummaryResponse;
import com.moeware.ims.dto.transaction.purchaseOrder.ReceivePurchaseOrderRequest;
import com.moeware.ims.entity.inventory.Product;
import com.moeware.ims.entity.inventory.Supplier;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.entity.transaction.PurchaseOrder;
import com.moeware.ims.entity.transaction.PurchaseOrderItem;
import com.moeware.ims.entity.User;
import com.moeware.ims.enums.transaction.PurchaseOrderStatus;
import com.moeware.ims.exception.ResourceNotFoundException;
import com.moeware.ims.exception.transaction.InvalidOrderStatusTransitionException;
import com.moeware.ims.exception.transaction.OrderNotEditableException;
import com.moeware.ims.exception.transaction.purchaseOrder.PurchaseOrderNotFoundException;
import com.moeware.ims.repository.transaction.PurchaseOrderItemRepository;
import com.moeware.ims.repository.transaction.PurchaseOrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for purchase order business logic and workflow management
 *
 * Workflow: DRAFT -> SUBMITTED -> APPROVED -> RECEIVED
 * Any status -> CANCELLED (except RECEIVED)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final OrderInventoryService orderInventoryService;

    // These repositories are assumed to exist from previous sprints
    // Adjust package paths to match your project structure
    private final com.moeware.ims.repository.inventory.SupplierRepository supplierRepository;
    private final com.moeware.ims.repository.staff.WarehouseRepository warehouseRepository;
    private final com.moeware.ims.repository.UserRepository userRepository;
    private final com.moeware.ims.repository.inventory.ProductRepository productRepository;

    // ==================== READ OPERATIONS ====================

    /**
     * Get all purchase orders with optional filters (paginated)
     */
    public Page<PurchaseOrderSummaryResponse> getAllPurchaseOrders(
            String search,
            Long supplierId,
            Long warehouseId,
            PurchaseOrderStatus status,
            Long createdByUserId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        return purchaseOrderRepository.findAllWithFilters(
                search, supplierId, warehouseId, status, createdByUserId, startDate, endDate, pageable)
                .map(this::toSummaryResponse);
    }

    /**
     * Get purchase order by ID (full detail)
     */
    public PurchaseOrderResponse getPurchaseOrderById(Long id) {
        PurchaseOrder po = findPurchaseOrderOrThrow(id);
        return toResponse(po);
    }

    /**
     * Get purchase orders pending approval
     */
    public Page<PurchaseOrderSummaryResponse> getPendingApprovalOrders(Pageable pageable) {
        return purchaseOrderRepository.findByStatus(PurchaseOrderStatus.SUBMITTED, pageable)
                .map(this::toSummaryResponse);
    }

    /**
     * Get purchase orders by supplier
     */
    public Page<PurchaseOrderSummaryResponse> getOrdersBySupplier(
            Long supplierId,
            PurchaseOrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        // Validate supplier exists
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier", "id", supplierId);
        }
        return purchaseOrderRepository.findBySupplierIdWithFilters(supplierId, status, startDate, endDate, pageable)
                .map(this::toSummaryResponse);
    }

    // ==================== WRITE OPERATIONS ====================

    /**
     * Create a new purchase order in DRAFT status
     */
    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request, Long createdByUserId) {
        log.info("Creating purchase order for supplier id: {}", request.getSupplierId());

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId()));

        User createdByUser = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", createdByUserId));

        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber(generatePoNumber(request.getOrderDate()))
                .supplier(supplier)
                .warehouse(warehouse)
                .createdByUser(createdByUser)
                .status(PurchaseOrderStatus.DRAFT)
                .orderDate(request.getOrderDate())
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .taxAmount(request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO)
                .discountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO)
                .notes(request.getNotes())
                .build();

        // Add line items
        for (PurchaseOrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .product(product)
                    .quantityOrdered(itemReq.getQuantityOrdered())
                    .quantityReceived(0)
                    .unitPrice(itemReq.getUnitPrice())
                    .build();

            po.addItem(item);
        }

        po.calculateTotals();
        PurchaseOrder saved = purchaseOrderRepository.save(po);

        log.info("Purchase order created: {}", saved.getPoNumber());
        return toResponse(saved);
    }

    /**
     * Update a purchase order (DRAFT only)
     */
    @Transactional
    public PurchaseOrderResponse updatePurchaseOrder(Long id, PurchaseOrderRequest request) {
        log.info("Updating purchase order id: {}", id);

        PurchaseOrder po = findPurchaseOrderOrThrow(id);

        if (po.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new OrderNotEditableException("PurchaseOrder", id, po.getStatus().name());
        }

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId()));

        po.setSupplier(supplier);
        po.setWarehouse(warehouse);
        po.setOrderDate(request.getOrderDate());
        po.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        po.setTaxAmount(request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO);
        po.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
        po.setNotes(request.getNotes());

        // Replace all items
        po.getItems().clear();
        for (PurchaseOrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .product(product)
                    .quantityOrdered(itemReq.getQuantityOrdered())
                    .quantityReceived(0)
                    .unitPrice(itemReq.getUnitPrice())
                    .build();

            po.addItem(item);
        }

        po.calculateTotals();
        PurchaseOrder updated = purchaseOrderRepository.save(po);

        log.info("Purchase order updated: {}", updated.getPoNumber());
        return toResponse(updated);
    }

    /**
     * Submit a DRAFT purchase order for approval -> SUBMITTED
     */
    @Transactional
    public PurchaseOrderResponse submitPurchaseOrder(Long id) {
        log.info("Submitting purchase order id: {}", id);

        PurchaseOrder po = findPurchaseOrderOrThrow(id);

        if (po.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new InvalidOrderStatusTransitionException("PurchaseOrder", po.getStatus().name(), "SUBMITTED");
        }

        if (po.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot submit a purchase order with no items");
        }

        po.setStatus(PurchaseOrderStatus.SUBMITTED);
        PurchaseOrder updated = purchaseOrderRepository.save(po);

        log.info("Purchase order submitted: {}", updated.getPoNumber());
        return toResponse(updated);
    }

    /**
     * Approve a SUBMITTED purchase order -> APPROVED (MANAGER/ADMIN only)
     */
    @Transactional
    public PurchaseOrderResponse approvePurchaseOrder(Long id) {
        log.info("Approving purchase order id: {}", id);

        PurchaseOrder po = findPurchaseOrderOrThrow(id);

        if (po.getStatus() != PurchaseOrderStatus.SUBMITTED) {
            throw new InvalidOrderStatusTransitionException("PurchaseOrder", po.getStatus().name(), "APPROVED");
        }

        po.setStatus(PurchaseOrderStatus.APPROVED);
        PurchaseOrder updated = purchaseOrderRepository.save(po);

        log.info("Purchase order approved: {}", updated.getPoNumber());
        return toResponse(updated);
    }

    /**
     * Reject a SUBMITTED purchase order back to DRAFT -> effectively cancel
     * submission
     * Returns to DRAFT so it can be edited and resubmitted (MANAGER/ADMIN only)
     */
    @Transactional
    public PurchaseOrderResponse rejectPurchaseOrder(Long id, String reason) {
        log.info("Rejecting purchase order id: {} - reason: {}", id, reason);

        PurchaseOrder po = findPurchaseOrderOrThrow(id);

        if (po.getStatus() != PurchaseOrderStatus.SUBMITTED) {
            throw new InvalidOrderStatusTransitionException("PurchaseOrder", po.getStatus().name(), "DRAFT (rejected)");
        }

        // Rejection returns the order to DRAFT with a reason appended to notes
        po.setStatus(PurchaseOrderStatus.DRAFT);
        String rejectionNote = "[REJECTED] " + reason;
        po.setNotes(po.getNotes() != null ? po.getNotes() + "\n" + rejectionNote : rejectionNote);

        PurchaseOrder updated = purchaseOrderRepository.save(po);
        log.info("Purchase order rejected and returned to DRAFT: {}", updated.getPoNumber());
        return toResponse(updated);
    }

    /**
     * Mark an APPROVED purchase order as RECEIVED, update inventory quantities
     */
    @Transactional
    public PurchaseOrderResponse receivePurchaseOrder(Long id, ReceivePurchaseOrderRequest request,
            Long performedByUserId) {
        PurchaseOrder order = findPurchaseOrderOrThrow(id);

        if (order.getStatus() != PurchaseOrderStatus.APPROVED) {
            throw new InvalidOrderStatusTransitionException(
                    "PurchaseOrder", order.getStatus().name(), PurchaseOrderStatus.RECEIVED.name());
        }

        User performedBy = userRepository.findById(performedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", performedByUserId));

        // Update quantityReceived on each line item
        for (ReceivePurchaseOrderRequest.ItemReceipt receipt : request.getItems()) {
            PurchaseOrderItem item = purchaseOrderItemRepository
                    .findByIdAndPurchaseOrderId(receipt.getItemId(), id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "PurchaseOrderItem", "id", receipt.getItemId()));

            int alreadyReceived = item.getQuantityReceived();
            int newTotal = alreadyReceived + receipt.getQuantityReceived();

            if (newTotal > item.getQuantityOrdered()) {
                throw new IllegalArgumentException(
                        String.format("Total received quantity (%d) exceeds ordered quantity (%d) for item %d.",
                                newTotal, item.getQuantityOrdered(), item.getId()));
            }

            item.setQuantityReceived(newTotal);
            purchaseOrderItemRepository.save(item);
        }

        // Update notes if provided
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            order.setNotes(
                    (order.getNotes() != null ? order.getNotes() + " | " : "")
                            + request.getNotes());
        }

        // Set actual delivery date
        if (request.getActualDeliveryDate() != null) {
            order.setActualDeliveryDate(request.getActualDeliveryDate());
        }

        // Increment stock and create RECEIPT movement records for received quantities
        // <-- ADDED
        orderInventoryService.receiveInventoryForPurchaseOrder(order, performedBy);

        order.setStatus(PurchaseOrderStatus.RECEIVED);
        return toResponse(purchaseOrderRepository.save(order));
    }

    /**
     * Cancel a purchase order (any status except RECEIVED)
     */
    @Transactional
    public PurchaseOrderResponse cancelPurchaseOrder(Long id, CancelOrderRequest request) {
        log.info("Cancelling purchase order id: {}", id);

        PurchaseOrder po = findPurchaseOrderOrThrow(id);

        if (po.getStatus() == PurchaseOrderStatus.RECEIVED) {
            throw new InvalidOrderStatusTransitionException("PurchaseOrder", po.getStatus().name(), "CANCELLED");
        }
        if (po.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new InvalidOrderStatusTransitionException("PurchaseOrder", po.getStatus().name(), "CANCELLED");
        }

        po.setStatus(PurchaseOrderStatus.CANCELLED);
        String cancellationNote = "[CANCELLED] " + request.getReason();
        po.setNotes(po.getNotes() != null ? po.getNotes() + "\n" + cancellationNote : cancellationNote);

        PurchaseOrder updated = purchaseOrderRepository.save(po);
        log.info("Purchase order cancelled: {}", updated.getPoNumber());
        return toResponse(updated);
    }

    /**
     * Delete a purchase order (DRAFT only)
     */
    @Transactional
    public void deletePurchaseOrder(Long id) {
        log.info("Deleting purchase order id: {}", id);

        PurchaseOrder po = findPurchaseOrderOrThrow(id);

        if (po.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new OrderNotEditableException("PurchaseOrder", id, po.getStatus().name());
        }

        purchaseOrderRepository.delete(po);
        log.info("Purchase order deleted: {}", po.getPoNumber());
    }

    // ==================== PRIVATE HELPERS ====================

    private PurchaseOrder findPurchaseOrderOrThrow(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderNotFoundException(id));
    }

    /**
     * Generate a unique PO number in format PO-YYYYMMDD-SEQUENCE
     */
    private String generatePoNumber(LocalDate orderDate) {
        String dateStr = orderDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = purchaseOrderRepository.countByOrderDate(orderDate) + 1;
        return String.format("PO-%s-%04d", dateStr, count);
    }

    // ==================== MAPPERS ====================

    public PurchaseOrderResponse toResponse(PurchaseOrder po) {
        List<PurchaseOrderItemResponse> itemResponses = po.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return PurchaseOrderResponse.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .supplier(PurchaseOrderResponse.SupplierSummary.builder()
                        .id(po.getSupplier().getId())
                        .name(po.getSupplier().getName())
                        .code(po.getSupplier().getCode())
                        .contactPerson(po.getSupplier().getContactPerson())
                        .email(po.getSupplier().getEmail())
                        .build())
                .warehouse(PurchaseOrderResponse.WarehouseSummary.builder()
                        .id(po.getWarehouse().getId())
                        .name(po.getWarehouse().getName())
                        .code(po.getWarehouse().getCode())
                        .build())
                .createdByUser(PurchaseOrderResponse.UserSummary.builder()
                        .id(po.getCreatedByUser().getId())
                        .username(po.getCreatedByUser().getUsername())
                        .email(po.getCreatedByUser().getEmail())
                        .build())
                .status(po.getStatus())
                .orderDate(po.getOrderDate())
                .expectedDeliveryDate(po.getExpectedDeliveryDate())
                .actualDeliveryDate(po.getActualDeliveryDate())
                .subtotal(po.getSubtotal())
                .taxAmount(po.getTaxAmount())
                .discountAmount(po.getDiscountAmount())
                .totalAmount(po.getTotalAmount())
                .notes(po.getNotes())
                .itemCount(po.getItems().size())
                .items(itemResponses)
                .version(po.getVersion())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .createdBy(po.getCreatedBy())
                .updatedBy(po.getUpdatedBy())
                .build();
    }

    private PurchaseOrderSummaryResponse toSummaryResponse(PurchaseOrder po) {
        return PurchaseOrderSummaryResponse.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .supplierId(po.getSupplier().getId())
                .supplierName(po.getSupplier().getName())
                .supplierCode(po.getSupplier().getCode())
                .warehouseId(po.getWarehouse().getId())
                .warehouseName(po.getWarehouse().getName())
                .status(po.getStatus())
                .orderDate(po.getOrderDate())
                .expectedDeliveryDate(po.getExpectedDeliveryDate())
                .actualDeliveryDate(po.getActualDeliveryDate())
                .itemCount(po.getItems().size())
                .subtotal(po.getSubtotal())
                .taxAmount(po.getTaxAmount())
                .discountAmount(po.getDiscountAmount())
                .totalAmount(po.getTotalAmount())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private PurchaseOrderItemResponse toItemResponse(PurchaseOrderItem item) {
        return PurchaseOrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productSku(item.getProduct().getSku())
                .productName(item.getProduct().getName())
                .quantityOrdered(item.getQuantityOrdered())
                .quantityReceived(item.getQuantityReceived())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}