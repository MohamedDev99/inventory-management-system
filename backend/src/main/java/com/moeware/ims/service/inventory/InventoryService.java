package com.moeware.ims.service.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.inventory.InventoryValuationResponse;
import com.moeware.ims.dto.inventory.inventoryItem.InventoryItemDTO;
import com.moeware.ims.dto.transaction.inventoryMovement.InventoryMovementDTO;
import com.moeware.ims.dto.transaction.inventoryMovement.TransferInventoryRequest;
import com.moeware.ims.dto.transaction.inventoryMovement.TransferInventoryResponse;
import com.moeware.ims.dto.transaction.shipment.ReceiveShipmentRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentResponse;
import com.moeware.ims.entity.User;
import com.moeware.ims.entity.inventory.InventoryItem;
import com.moeware.ims.entity.inventory.Product;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.entity.transaction.InventoryMovement;
import com.moeware.ims.entity.transaction.StockAdjustment;
import com.moeware.ims.enums.transaction.MovementType;
import com.moeware.ims.enums.transaction.StockAdjustmentStatus;
import com.moeware.ims.exception.ResourceNotFoundException;
import com.moeware.ims.exception.transaction.stockAdjustment.InsufficientStockException;
import com.moeware.ims.repository.UserRepository;
import com.moeware.ims.repository.inventory.InventoryItemRepository;
import com.moeware.ims.repository.inventory.ProductRepository;
import com.moeware.ims.repository.staff.WarehouseRepository;
import com.moeware.ims.repository.transaction.InventoryMovementRepository;
import com.moeware.ims.repository.transaction.StockAdjustmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for inventory operations
 * Handles stock management, transfers, adjustments, and valuation
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService {

        private final InventoryItemRepository inventoryItemRepository;
        private final InventoryMovementRepository inventoryMovementRepository;
        private final StockAdjustmentRepository stockAdjustmentRepository;
        private final ProductRepository productRepository;
        private final WarehouseRepository warehouseRepository;
        private final UserRepository userRepository;

        /**
         * Get all inventory items with pagination and filters
         */
        @Transactional(readOnly = true)
        public Page<InventoryItemDTO> getAllInventoryItems(
                        Long warehouseId,
                        Long productId,
                        Boolean lowStock,
                        String search,
                        Pageable pageable) {

                Page<InventoryItem> items;

                if (search != null && !search.isBlank()) {
                        items = inventoryItemRepository.searchInventoryItems(search, pageable);
                } else if (warehouseId != null || productId != null || (lowStock != null && lowStock)) {
                        items = inventoryItemRepository.findAllWithFilters(warehouseId, productId,
                                        lowStock != null && lowStock,
                                        pageable);
                } else {
                        items = inventoryItemRepository.findAll(pageable);
                }

                return items.map(this::mapToDTO);
        }

        /**
         * Get inventory item by ID
         */
        @Transactional(readOnly = true)
        public InventoryItemDTO getInventoryItemById(Long id) {
                InventoryItem item = inventoryItemRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Inventory item not found with id: " + id));
                return mapToDTO(item);
        }

        /**
         * Get inventory for a specific warehouse
         */
        @Transactional(readOnly = true)
        public Page<InventoryItemDTO> getWarehouseInventory(Long warehouseId, Pageable pageable) {
                Warehouse warehouse = warehouseRepository.findById(warehouseId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Warehouse not found with id: " + warehouseId));

                return inventoryItemRepository.findByWarehouse(warehouse, pageable)
                                .map(this::mapToDTO);
        }

        /**
         * Get inventory for a specific product across all warehouses
         */
        @Transactional(readOnly = true)
        public List<InventoryItemDTO> getProductInventory(Long productId) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found with id: " + productId));

                return inventoryItemRepository.findByProduct(product).stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        /**
         * Transfer stock between warehouses
         */
        public TransferInventoryResponse transferInventory(TransferInventoryRequest request) {
                log.info("Transferring {} units of product {} from warehouse {} to warehouse {}",
                                request.getQuantity(), request.getProductId(), request.getFromWarehouseId(),
                                request.getToWarehouseId());

                // Validate transfer request
                if (request.getFromWarehouseId().equals(request.getToWarehouseId())) {
                        throw new IllegalArgumentException("Cannot transfer to the same warehouse");
                }

                // Fetch entities
                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException("Product not found with id: "
                                                                + request.getProductId()));

                Warehouse fromWarehouse = warehouseRepository.findById(request.getFromWarehouseId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Source warehouse not found with id: " + request.getFromWarehouseId()));

                Warehouse toWarehouse = warehouseRepository.findById(request.getToWarehouseId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Destination warehouse not found with id: "
                                                                + request.getToWarehouseId()));

                User performedBy = userRepository.findById(request.getPerformedBy())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "User not found with id: " + request.getPerformedBy()));

                // Get or create inventory items
                InventoryItem fromInventory = inventoryItemRepository.findByProductAndWarehouse(product, fromWarehouse)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found in source warehouse"));

                // Check sufficient stock
                if (fromInventory.getQuantity() < request.getQuantity()) {
                        throw new InsufficientStockException(
                                        String.format("Insufficient stock in %s. Available: %d, Requested: %d",
                                                        fromWarehouse.getName(), fromInventory.getQuantity(),
                                                        request.getQuantity()));
                }

                // Remove stock from source
                fromInventory.removeStock(request.getQuantity());
                inventoryItemRepository.save(fromInventory);

                // Add stock to destination
                InventoryItem toInventory = inventoryItemRepository.findByProductAndWarehouse(product, toWarehouse)
                                .orElseGet(() -> InventoryItem.builder()
                                                .product(product)
                                                .warehouse(toWarehouse)
                                                .quantity(0)
                                                .build());

                toInventory.addStock(request.getQuantity());
                inventoryItemRepository.save(toInventory);

                // Create movement record
                InventoryMovement movement = InventoryMovement.builder()
                                .product(product)
                                .fromWarehouse(fromWarehouse)
                                .toWarehouse(toWarehouse)
                                .quantity(request.getQuantity())
                                .movementType(MovementType.TRANSFER)
                                .reason(request.getReason())
                                .performedBy(performedBy)
                                .movementDate(LocalDateTime.now())
                                .build();

                movement = inventoryMovementRepository.save(movement);

                log.info("Transfer completed successfully. Movement ID: {}", movement.getId());

                // Build response
                return TransferInventoryResponse.builder()
                                .movementId(movement.getId())
                                .productId(product.getId())
                                .fromWarehouse(TransferInventoryResponse.WarehouseTransferInfo.builder()
                                                .id(fromWarehouse.getId())
                                                .name(fromWarehouse.getName())
                                                .newQuantity(fromInventory.getQuantity())
                                                .build())
                                .toWarehouse(TransferInventoryResponse.WarehouseTransferInfo.builder()
                                                .id(toWarehouse.getId())
                                                .name(toWarehouse.getName())
                                                .newQuantity(toInventory.getQuantity())
                                                .build())
                                .quantityTransferred(request.getQuantity())
                                .movementDate(movement.getMovementDate())
                                .build();
        }

        /**
         * Create stock adjustment request
         */
        public StockAdjustmentResponse createStockAdjustment(StockAdjustmentRequest request) {
                log.info("Creating stock adjustment for product {} in warehouse {}",
                                request.getProductId(), request.getWarehouseId());

                // Fetch entities
                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException("Product not found with id: "
                                                                + request.getProductId()));

                Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Warehouse not found with id: " + request.getWarehouseId()));

                User performedBy = userRepository.findById(request.getPerformedBy())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "User not found with id: " + request.getPerformedBy()));

                // Get current inventory
                InventoryItem inventory = inventoryItemRepository.findByProductAndWarehouse(product, warehouse)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found in warehouse"));

                Integer quantityBefore = inventory.getQuantity();
                Integer quantityAfter = quantityBefore + request.getQuantityChange();

                // Validate adjustment
                if (quantityAfter < 0) {
                        throw new IllegalArgumentException("Adjustment would result in negative stock");
                }

                // Create adjustment record
                StockAdjustment adjustment = StockAdjustment.builder()
                                .product(product)
                                .warehouse(warehouse)
                                .quantityBefore(quantityBefore)
                                .quantityAfter(quantityAfter)
                                .quantityChange(request.getQuantityChange())
                                .adjustmentType(request.getAdjustmentType())
                                .reason(request.getReason())
                                .performedBy(performedBy)
                                .status(StockAdjustmentStatus.PENDING)
                                .notes(request.getNotes())
                                .adjustmentDate(LocalDateTime.now())
                                .build();

                adjustment = stockAdjustmentRepository.save(adjustment);

                log.info("Stock adjustment created successfully. ID: {}, Status: PENDING", adjustment.getId());

                // Build response
                return mapAdjustmentToResponse(adjustment);
        }

        /**
         * Receive shipment from purchase order
         */
        public void receiveShipment(ReceiveShipmentRequest request) {
                log.info("Receiving shipment for purchase order {} into warehouse {}",
                                request.getPurchaseOrderId(), request.getWarehouseId());

                // Fetch warehouse and user
                Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Warehouse not found with id: " + request.getWarehouseId()));

                User receivedBy = userRepository.findById(request.getReceivedBy())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with id: " + request.getReceivedBy()));

                LocalDateTime receivedDate = request.getReceivedDate() != null ? request.getReceivedDate()
                                : LocalDateTime.now();

                // Process each item
                for (ReceiveShipmentRequest.ReceiveShipmentItem item : request.getItems()) {
                        Product product = productRepository.findById(item.getProductId())
                                        .orElseThrow(
                                                        () -> new ResourceNotFoundException(
                                                                        "Product not found with id: "
                                                                                        + item.getProductId()));

                        // Update inventory
                        InventoryItem inventory = inventoryItemRepository.findByProductAndWarehouse(product, warehouse)
                                        .orElseGet(() -> InventoryItem.builder()
                                                        .product(product)
                                                        .warehouse(warehouse)
                                                        .quantity(0)
                                                        .build());

                        inventory.addStock(item.getQuantityReceived());

                        if (item.getLocationCode() != null) {
                                inventory.setLocationCode(item.getLocationCode());
                        }

                        inventoryItemRepository.save(inventory);

                        // Create movement record
                        InventoryMovement movement = InventoryMovement.builder()
                                        .product(product)
                                        .toWarehouse(warehouse)
                                        .quantity(item.getQuantityReceived())
                                        .movementType(MovementType.RECEIPT)
                                        .reason("Purchase order receipt")
                                        .referenceNumber("PO-" + request.getPurchaseOrderId())
                                        .performedBy(receivedBy)
                                        .movementDate(receivedDate)
                                        .build();

                        inventoryMovementRepository.save(movement);
                }

                log.info("Shipment received successfully. {} items processed", request.getItems().size());
        }

        /**
         * Get inventory valuation
         */
        @Transactional(readOnly = true)
        public InventoryValuationResponse getInventoryValuation(Long warehouseId, Long categoryId,
                        String valuationType) {
                log.info("Calculating inventory valuation with filters - warehouse: {}, category: {}, type: {}",
                                warehouseId, categoryId, valuationType);

                // This is a simplified implementation
                // In a real application, you would use more complex queries with JOIN
                // operations

                List<InventoryItem> items;
                if (warehouseId != null) {
                        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
                        items = inventoryItemRepository.findByWarehouse(warehouse, Pageable.unpaged()).getContent();
                } else {
                        items = inventoryItemRepository.findAll();
                }

                // Calculate totals
                int totalProducts = (int) items.stream().map(i -> i.getProduct().getId()).distinct().count();
                int totalUnits = items.stream().mapToInt(InventoryItem::getQuantity).sum();

                BigDecimal costValue = items.stream()
                                .map(i -> i.getProduct().getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // For retail, assume 50% markup (this should come from product data)
                BigDecimal retailValue = costValue.multiply(BigDecimal.valueOf(1.5));
                BigDecimal potentialProfit = retailValue.subtract(costValue);

                return InventoryValuationResponse.builder()
                                .totalProducts(totalProducts)
                                .totalUnits(totalUnits)
                                .costValue(costValue)
                                .retailValue(retailValue)
                                .potentialProfit(potentialProfit)
                                .build();
        }

        /**
         * Get inventory movements with filters
         */
        @Transactional(readOnly = true)
        public Page<InventoryMovementDTO> getInventoryMovements(
                        Long productId,
                        Long warehouseId,
                        MovementType movementType,
                        LocalDateTime startDate,
                        LocalDateTime endDate,
                        Pageable pageable) {

                Page<InventoryMovement> movements = inventoryMovementRepository.findAllWithFilters(
                                productId, warehouseId, movementType, startDate, endDate, pageable);

                return movements.map(this::mapMovementToDTO);
        }

        // ==================== Mapping Methods ====================

        private InventoryItemDTO mapToDTO(InventoryItem item) {
                return InventoryItemDTO.builder()
                                .id(item.getId())
                                .product(InventoryItemDTO.ProductSummaryDTO.builder()
                                                .id(item.getProduct().getId())
                                                .sku(item.getProduct().getSku())
                                                .name(item.getProduct().getName())
                                                .unitPrice(item.getProduct().getUnitPrice())
                                                .build())
                                .warehouse(InventoryItemDTO.WarehouseSummaryDTO.builder()
                                                .id(item.getWarehouse().getId())
                                                .name(item.getWarehouse().getName())
                                                .code(item.getWarehouse().getCode())
                                                .build())
                                .quantity(item.getQuantity())
                                .locationCode(item.getLocationCode())
                                .reorderLevel(item.getProduct().getReorderLevel())
                                .isLowStock(item.isLowStock())
                                .stockStatus(item.getStockStatus())
                                .lastStockCheck(item.getLastStockCheck())
                                .createdAt(item.getCreatedAt())
                                .updatedAt(item.getUpdatedAt())
                                .version(item.getVersion())
                                .build();
        }

        private StockAdjustmentResponse mapAdjustmentToResponse(StockAdjustment adjustment) {
                return StockAdjustmentResponse.builder()
                                .id(adjustment.getId())
                                .productId(adjustment.getProduct().getId())
                                .warehouseId(adjustment.getWarehouse().getId())
                                .quantityBefore(adjustment.getQuantityBefore())
                                .quantityAfter(adjustment.getQuantityAfter())
                                .quantityChange(adjustment.getQuantityChange())
                                .adjustmentType(adjustment.getAdjustmentType())
                                .reason(adjustment.getReason())
                                .status(adjustment.getStatus())
                                .performedBy(StockAdjustmentResponse.UserSummaryDTO.builder()
                                                .id(adjustment.getPerformedBy().getId())
                                                .username(adjustment.getPerformedBy().getUsername())
                                                .build())
                                .approvedBy(adjustment.getApprovedBy() != null
                                                ? StockAdjustmentResponse.UserSummaryDTO.builder()
                                                                .id(adjustment.getApprovedBy().getId())
                                                                .username(adjustment.getApprovedBy().getUsername())
                                                                .build()
                                                : null)
                                .notes(adjustment.getNotes())
                                .createdAt(adjustment.getCreatedAt())
                                .build();
        }

        private InventoryMovementDTO mapMovementToDTO(InventoryMovement movement) {
                return InventoryMovementDTO.builder()
                                .id(movement.getId())
                                .product(InventoryMovementDTO.ProductSummaryDTO.builder()
                                                .id(movement.getProduct().getId())
                                                .sku(movement.getProduct().getSku())
                                                .name(movement.getProduct().getName())
                                                .build())
                                .fromWarehouse(movement.getFromWarehouse() != null
                                                ? InventoryMovementDTO.WarehouseSummaryDTO.builder()
                                                                .id(movement.getFromWarehouse().getId())
                                                                .name(movement.getFromWarehouse().getName())
                                                                .code(movement.getFromWarehouse().getCode())
                                                                .build()
                                                : null)
                                .toWarehouse(movement.getToWarehouse() != null
                                                ? InventoryMovementDTO.WarehouseSummaryDTO.builder()
                                                                .id(movement.getToWarehouse().getId())
                                                                .name(movement.getToWarehouse().getName())
                                                                .code(movement.getToWarehouse().getCode())
                                                                .build()
                                                : null)
                                .quantity(movement.getQuantity())
                                .movementType(movement.getMovementType())
                                .reason(movement.getReason())
                                .referenceNumber(movement.getReferenceNumber())
                                .performedBy(InventoryMovementDTO.UserSummaryDTO.builder()
                                                .id(movement.getPerformedBy().getId())
                                                .username(movement.getPerformedBy().getUsername())
                                                .build())
                                .movementDate(movement.getMovementDate())
                                .createdAt(movement.getCreatedAt())
                                .build();
        }
}