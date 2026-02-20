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
import com.moeware.ims.dto.transaction.salesOrder.SalesOrderItemRequest;
import com.moeware.ims.dto.transaction.salesOrder.SalesOrderItemResponse;
import com.moeware.ims.dto.transaction.salesOrder.SalesOrderRequest;
import com.moeware.ims.dto.transaction.salesOrder.SalesOrderResponse;
import com.moeware.ims.dto.transaction.salesOrder.SalesOrderSummaryResponse;
import com.moeware.ims.entity.User;
import com.moeware.ims.entity.inventory.Product;
import com.moeware.ims.entity.staff.Customer;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.entity.transaction.SalesOrder;
import com.moeware.ims.entity.transaction.SalesOrderItem;
import com.moeware.ims.enums.transaction.SalesOrderStatus;
import com.moeware.ims.exception.ResourceNotFoundException;
import com.moeware.ims.exception.transaction.InvalidOrderStatusTransitionException;
import com.moeware.ims.exception.transaction.OrderNotEditableException;
import com.moeware.ims.exception.transaction.salesOrder.SalesOrderNotFoundException;
import com.moeware.ims.repository.transaction.SalesOrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for sales order business logic and workflow management
 *
 * Workflow: PENDING -> CONFIRMED -> FULFILLED -> SHIPPED -> DELIVERED
 * Any status (before SHIPPED) -> CANCELLED
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;

    // These repositories are assumed to exist from previous sprints
    // Adjust package paths to match your project structure
    private final com.moeware.ims.repository.staff.CustomerRepository customerRepository;
    private final com.moeware.ims.repository.staff.WarehouseRepository warehouseRepository;
    private final com.moeware.ims.repository.UserRepository userRepository;
    private final com.moeware.ims.repository.inventory.ProductRepository productRepository;

    // ==================== READ OPERATIONS ====================

    /**
     * Get all sales orders with optional filters (paginated)
     */
    public Page<SalesOrderSummaryResponse> getAllSalesOrders(
            String search,
            Long customerId,
            Long warehouseId,
            SalesOrderStatus status,
            Long createdByUserId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        return salesOrderRepository.findAllWithFilters(
                search, customerId, warehouseId, status, createdByUserId, startDate, endDate, pageable)
                .map(this::toSummaryResponse);
    }

    /**
     * Get sales order by ID (full detail)
     */
    public SalesOrderResponse getSalesOrderById(Long id) {
        SalesOrder so = findSalesOrderOrThrow(id);
        return toResponse(so);
    }

    /**
     * Get sales orders by customer
     */
    public Page<SalesOrderSummaryResponse> getOrdersByCustomer(
            Long customerId,
            SalesOrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", "id", customerId);
        }
        return salesOrderRepository.findByCustomerIdWithFilters(customerId, status, startDate, endDate, pageable)
                .map(this::toSummaryResponse);
    }

    // ==================== WRITE OPERATIONS ====================

    /**
     * Create a new sales order in PENDING status
     */
    @Transactional
    public SalesOrderResponse createSalesOrder(SalesOrderRequest request, Long createdByUserId) {
        log.info("Creating sales order for customer id: {}", request.getCustomerId());

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId()));

        User createdByUser = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", createdByUserId));

        SalesOrder so = SalesOrder.builder()
                .soNumber(generateSoNumber(request.getOrderDate()))
                .customer(customer)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .shippingAddress(request.getShippingAddress())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .warehouse(warehouse)
                .createdByUser(createdByUser)
                .status(SalesOrderStatus.PENDING)
                .orderDate(request.getOrderDate())
                .taxAmount(request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO)
                .shippingCost(request.getShippingCost() != null ? request.getShippingCost() : BigDecimal.ZERO)
                .notes(request.getNotes())
                .build();

        // Add line items
        for (SalesOrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));

            SalesOrderItem item = SalesOrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .build();

            so.addItem(item);
        }

        so.calculateTotals();
        SalesOrder saved = salesOrderRepository.save(so);

        log.info("Sales order created: {}", saved.getSoNumber());
        return toResponse(saved);
    }

    /**
     * Update a sales order (PENDING only)
     */
    @Transactional
    public SalesOrderResponse updateSalesOrder(Long id, SalesOrderRequest request) {
        log.info("Updating sales order id: {}", id);

        SalesOrder so = findSalesOrderOrThrow(id);

        if (so.getStatus() != SalesOrderStatus.PENDING) {
            throw new OrderNotEditableException("SalesOrder", id, so.getStatus().name());
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId()));

        so.setCustomer(customer);
        so.setCustomerName(request.getCustomerName());
        so.setCustomerEmail(request.getCustomerEmail());
        so.setCustomerPhone(request.getCustomerPhone());
        so.setShippingAddress(request.getShippingAddress());
        so.setCity(request.getCity());
        so.setPostalCode(request.getPostalCode());
        so.setWarehouse(warehouse);
        so.setOrderDate(request.getOrderDate());
        so.setTaxAmount(request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO);
        so.setShippingCost(request.getShippingCost() != null ? request.getShippingCost() : BigDecimal.ZERO);
        so.setNotes(request.getNotes());

        // Replace all items
        so.getItems().clear();
        for (SalesOrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));

            SalesOrderItem item = SalesOrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .build();

            so.addItem(item);
        }

        so.calculateTotals();
        SalesOrder updated = salesOrderRepository.save(so);

        log.info("Sales order updated: {}", updated.getSoNumber());
        return toResponse(updated);
    }

    /**
     * Confirm a PENDING sales order (reserve inventory) -> CONFIRMED
     */
    @Transactional
    public SalesOrderResponse confirmSalesOrder(Long id) {
        log.info("Confirming sales order id: {}", id);

        SalesOrder so = findSalesOrderOrThrow(id);

        if (so.getStatus() != SalesOrderStatus.PENDING) {
            throw new InvalidOrderStatusTransitionException("SalesOrder", so.getStatus().name(), "CONFIRMED");
        }

        if (so.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot confirm a sales order with no items");
        }

        // TODO: Check inventory availability for each item in the assigned warehouse
        // This will interact with the InventoryItem repository once that sprint is
        // wired in.
        // For now the status transition is tracked here; inventory reservation
        // should be implemented as part of the inventory service integration.

        so.setStatus(SalesOrderStatus.CONFIRMED);
        SalesOrder updated = salesOrderRepository.save(so);

        log.info("Sales order confirmed: {}", updated.getSoNumber());
        return toResponse(updated);
    }

    /**
     * Fulfill a CONFIRMED sales order (pick & pack, deduct inventory) -> FULFILLED
     */
    @Transactional
    public SalesOrderResponse fulfillSalesOrder(Long id) {
        log.info("Fulfilling sales order id: {}", id);

        SalesOrder so = findSalesOrderOrThrow(id);

        if (so.getStatus() != SalesOrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusTransitionException("SalesOrder", so.getStatus().name(), "FULFILLED");
        }

        // TODO: Deduct inventory quantities and create InventoryMovement records
        // This will interact with InventoryItem and InventoryMovement repositories
        // once the inventory service integration sprint is complete.

        so.setStatus(SalesOrderStatus.FULFILLED);
        so.setFulfillmentDate(LocalDate.now());
        SalesOrder updated = salesOrderRepository.save(so);

        log.info("Sales order fulfilled: {}", updated.getSoNumber());
        return toResponse(updated);
    }

    /**
     * Mark a FULFILLED sales order as SHIPPED -> SHIPPED
     */
    @Transactional
    public SalesOrderResponse shipSalesOrder(Long id) {
        log.info("Marking sales order as shipped, id: {}", id);

        SalesOrder so = findSalesOrderOrThrow(id);

        if (so.getStatus() != SalesOrderStatus.FULFILLED) {
            throw new InvalidOrderStatusTransitionException("SalesOrder", so.getStatus().name(), "SHIPPED");
        }

        so.setStatus(SalesOrderStatus.SHIPPED);
        so.setShippingDate(LocalDate.now());
        SalesOrder updated = salesOrderRepository.save(so);

        log.info("Sales order shipped: {}", updated.getSoNumber());
        return toResponse(updated);
    }

    /**
     * Mark a SHIPPED sales order as DELIVERED -> DELIVERED
     */
    @Transactional
    public SalesOrderResponse deliverSalesOrder(Long id) {
        log.info("Marking sales order as delivered, id: {}", id);

        SalesOrder so = findSalesOrderOrThrow(id);

        if (so.getStatus() != SalesOrderStatus.SHIPPED) {
            throw new InvalidOrderStatusTransitionException("SalesOrder", so.getStatus().name(), "DELIVERED");
        }

        so.setStatus(SalesOrderStatus.DELIVERED);
        so.setDeliveryDate(LocalDate.now());
        SalesOrder updated = salesOrderRepository.save(so);

        log.info("Sales order delivered: {}", updated.getSoNumber());
        return toResponse(updated);
    }

    /**
     * Cancel a sales order (any status before SHIPPED)
     */
    @Transactional
    public SalesOrderResponse cancelSalesOrder(Long id, CancelOrderRequest request) {
        log.info("Cancelling sales order id: {}", id);

        SalesOrder so = findSalesOrderOrThrow(id);

        if (so.getStatus() == SalesOrderStatus.SHIPPED
                || so.getStatus() == SalesOrderStatus.DELIVERED
                || so.getStatus() == SalesOrderStatus.CANCELLED) {
            throw new InvalidOrderStatusTransitionException("SalesOrder", so.getStatus().name(), "CANCELLED");
        }

        // TODO: Release reserved inventory if the order was CONFIRMED or FULFILLED
        // This will interact with the inventory service once that integration is done.

        so.setStatus(SalesOrderStatus.CANCELLED);
        String cancellationNote = "[CANCELLED] " + request.getReason();
        so.setNotes(so.getNotes() != null ? so.getNotes() + "\n" + cancellationNote : cancellationNote);

        SalesOrder updated = salesOrderRepository.save(so);
        log.info("Sales order cancelled: {}", updated.getSoNumber());
        return toResponse(updated);
    }

    // ==================== PRIVATE HELPERS ====================

    private SalesOrder findSalesOrderOrThrow(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new SalesOrderNotFoundException(id));
    }

    /**
     * Generate a unique SO number in format SO-YYYYMMDD-SEQUENCE
     */
    private String generateSoNumber(LocalDate orderDate) {
        String dateStr = orderDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = salesOrderRepository.countByOrderDate(orderDate) + 1;
        return String.format("SO-%s-%04d", dateStr, count);
    }

    // ==================== MAPPERS ====================

    public SalesOrderResponse toResponse(SalesOrder so) {
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

    private SalesOrderSummaryResponse toSummaryResponse(SalesOrder so) {
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

    private SalesOrderItemResponse toItemResponse(SalesOrderItem item) {
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