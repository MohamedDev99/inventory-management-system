package com.moeware.ims.service.transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.transaction.inventoryMovement.InventoryMovementDTO;
import com.moeware.ims.dto.transaction.inventoryMovement.InventoryMovementSummaryDTO;
import com.moeware.ims.entity.inventory.Product;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.entity.transaction.InventoryMovement;
import com.moeware.ims.enums.transaction.MovementType;
import com.moeware.ims.exception.inventory.product.ProductNotFoundException;
import com.moeware.ims.exception.staff.warehouse.WarehouseNotFoundException;
import com.moeware.ims.exception.transaction.inventoryMovement.InventoryMovementNotFoundException;
import com.moeware.ims.repository.inventory.ProductRepository;
import com.moeware.ims.repository.staff.WarehouseRepository;
import com.moeware.ims.repository.transaction.InventoryMovementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for Inventory Movement read operations.
 *
 * <p>
 * Write operations (TRANSFER, RECEIPT, SHIPMENT, ADJUSTMENT) are performed
 * internally by {@link com.moeware.ims.service.inventory.InventoryService} and
 * {@link com.moeware.ims.service.transaction.OrderInventoryService} as part of
 * their respective transactional workflows. This service exposes only the
 * query-side API consumed by {@code InventoryMovementController}.
 * </p>
 *
 * @author MoeWare Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InventoryMovementService {

        private final InventoryMovementRepository inventoryMovementRepository;
        private final ProductRepository productRepository;
        private final WarehouseRepository warehouseRepository;

        // ================================================================
        // Query Methods
        // ================================================================

        /**
         * Get all inventory movements with optional filters and pagination.
         *
         * @param productId    optional product filter
         * @param warehouseId  optional warehouse filter (matches from OR to)
         * @param movementType optional movement type filter
         * @param startDate    optional start of date range (inclusive)
         * @param endDate      optional end of date range (inclusive)
         * @param pageable     pagination and sort
         * @return paginated list of movement DTOs
         */
        public Page<InventoryMovementDTO> getAllMovements(
                        Long productId,
                        Long warehouseId,
                        MovementType movementType,
                        LocalDateTime startDate,
                        LocalDateTime endDate,
                        Pageable pageable) {

                log.debug("Fetching inventory movements - productId={}, warehouseId={}, type={}, start={}, end={}",
                                productId, warehouseId, movementType, startDate, endDate);

                return inventoryMovementRepository
                                .findAllWithFilters(productId, warehouseId, movementType, startDate, endDate, pageable)
                                .map(this::toDTO);
        }

        /**
         * Get a single inventory movement by ID.
         *
         * @param id movement ID
         * @return movement DTO
         * @throws InventoryMovementNotFoundException if not found
         */
        public InventoryMovementDTO getMovementById(Long id) {
                log.debug("Fetching inventory movement id={}", id);
                InventoryMovement movement = inventoryMovementRepository.findById(id)
                                .orElseThrow(() -> new InventoryMovementNotFoundException(id));
                return toDTO(movement);
        }

        /**
         * Get paginated movement history for a specific product.
         *
         * @param productId product ID
         * @param pageable  pagination and sort
         * @return paginated movement DTOs
         * @throws ProductNotFoundException if product does not exist
         */
        public Page<InventoryMovementDTO> getMovementsByProduct(Long productId, Pageable pageable) {
                log.debug("Fetching inventory movements for product id={}", productId);
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new ProductNotFoundException(productId));
                return inventoryMovementRepository.findByProduct(product, pageable).map(this::toDTO);
        }

        /**
         * Get paginated movements for a specific warehouse (either from or to).
         *
         * @param warehouseId warehouse ID
         * @param pageable    pagination and sort
         * @return paginated movement DTOs
         * @throws WarehouseNotFoundException if warehouse does not exist
         */
        public Page<InventoryMovementDTO> getMovementsByWarehouse(Long warehouseId, Pageable pageable) {
                log.debug("Fetching inventory movements for warehouse id={}", warehouseId);
                Warehouse warehouse = warehouseRepository.findById(warehouseId)
                                .orElseThrow(() -> new WarehouseNotFoundException(warehouseId));
                return inventoryMovementRepository.findByWarehouse(warehouse, pageable).map(this::toDTO);
        }

        /**
         * Get a summarized view of inventory movements over a date range, optionally
         * filtered by warehouse.
         *
         * <p>
         * Returns total counts grouped by {@link MovementType} and per-warehouse
         * breakdown for receipts, shipments, and the net quantity change.
         * </p>
         *
         * @param startDate   start of the reporting period (inclusive)
         * @param endDate     end of the reporting period (inclusive)
         * @param warehouseId optional warehouse filter
         * @return movement summary DTO
         */
        public InventoryMovementSummaryDTO getMovementSummary(
                        LocalDateTime startDate,
                        LocalDateTime endDate,
                        Long warehouseId) {

                log.debug("Building movement summary - start={}, end={}, warehouseId={}", startDate, endDate,
                                warehouseId);

                // Fetch movements in date range (use large page to aggregate — suitable for
                // summary endpoints; swap for aggregation queries if dataset grows large)
                List<InventoryMovement> movements = inventoryMovementRepository
                                .findByMovementDateBetween(startDate, endDate, Pageable.unpaged())
                                .getContent();

                // Apply optional warehouse filter
                if (warehouseId != null) {
                        movements = movements.stream()
                                        .filter(m -> (m.getFromWarehouse() != null
                                                        && warehouseId.equals(m.getFromWarehouse().getId()))
                                                        || (m.getToWarehouse() != null && warehouseId
                                                                        .equals(m.getToWarehouse().getId())))
                                        .collect(Collectors.toList());
                }

                // Totals by type
                Map<MovementType, Long> countByType = movements.stream()
                                .collect(Collectors.groupingBy(InventoryMovement::getMovementType,
                                                Collectors.counting()));

                Map<MovementType, Long> quantityByType = movements.stream()
                                .collect(Collectors.groupingBy(InventoryMovement::getMovementType,
                                                Collectors.summingLong(InventoryMovement::getQuantity)));

                InventoryMovementSummaryDTO.TotalsDTO totals = InventoryMovementSummaryDTO.TotalsDTO.builder()
                                .receipts(quantityByType.getOrDefault(MovementType.RECEIPT, 0L))
                                .shipments(quantityByType.getOrDefault(MovementType.SHIPMENT, 0L))
                                .transfers(quantityByType.getOrDefault(MovementType.TRANSFER, 0L))
                                .adjustments(quantityByType.getOrDefault(MovementType.ADJUSTMENT, 0L))
                                .build();

                // By movement type breakdown
                List<InventoryMovementSummaryDTO.ByTypeDTO> byType = countByType.entrySet().stream()
                                .map(e -> InventoryMovementSummaryDTO.ByTypeDTO.builder()
                                                .movementType(e.getKey())
                                                .count(e.getValue())
                                                .totalQuantity(quantityByType.getOrDefault(e.getKey(), 0L))
                                                .build())
                                .collect(Collectors.toList());

                // Per-warehouse breakdown
                List<InventoryMovementSummaryDTO.ByWarehouseDTO> byWarehouse = buildWarehouseBreakdown(movements);

                return InventoryMovementSummaryDTO.builder()
                                .period(InventoryMovementSummaryDTO.PeriodDTO.builder()
                                                .startDate(startDate)
                                                .endDate(endDate)
                                                .build())
                                .totals(totals)
                                .byMovementType(byType)
                                .byWarehouse(byWarehouse)
                                .build();
        }

        // ================================================================
        // Private Helpers
        // ================================================================

        /**
         * Build per-warehouse receipt / shipment / net-change breakdown from a list of
         * already-fetched movements.
         */
        private List<InventoryMovementSummaryDTO.ByWarehouseDTO> buildWarehouseBreakdown(
                        List<InventoryMovement> movements) {

                // Collect warehouse IDs mentioned in any movement
                Map<Long, String> warehouseNames = movements.stream()
                                .flatMap(m -> {
                                        java.util.stream.Stream.Builder<Map.Entry<Long, String>> builder = java.util.stream.Stream
                                                        .builder();
                                        if (m.getFromWarehouse() != null) {
                                                builder.accept(Map.entry(
                                                                m.getFromWarehouse().getId(),
                                                                m.getFromWarehouse().getName()));
                                        }
                                        if (m.getToWarehouse() != null) {
                                                builder.accept(Map.entry(
                                                                m.getToWarehouse().getId(),
                                                                m.getToWarehouse().getName()));
                                        }
                                        return builder.build();
                                })
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

                return warehouseNames.entrySet().stream()
                                .map(entry -> {
                                        Long wId = entry.getKey();
                                        String wName = entry.getValue();

                                        long receipts = movements.stream()
                                                        .filter(m -> m.getMovementType() == MovementType.RECEIPT
                                                                        && m.getToWarehouse() != null
                                                                        && wId.equals(m.getToWarehouse().getId()))
                                                        .mapToLong(InventoryMovement::getQuantity)
                                                        .sum();

                                        long shipments = movements.stream()
                                                        .filter(m -> m.getMovementType() == MovementType.SHIPMENT
                                                                        && m.getFromWarehouse() != null
                                                                        && wId.equals(m.getFromWarehouse().getId()))
                                                        .mapToLong(InventoryMovement::getQuantity)
                                                        .sum();

                                        long netChange = receipts - shipments;

                                        return InventoryMovementSummaryDTO.ByWarehouseDTO.builder()
                                                        .warehouseId(wId)
                                                        .warehouseName(wName)
                                                        .receipts(receipts)
                                                        .shipments(shipments)
                                                        .netChange(netChange)
                                                        .build();
                                })
                                .collect(Collectors.toList());
        }

        /**
         * Map {@link InventoryMovement} entity to {@link InventoryMovementDTO}.
         */
        private InventoryMovementDTO toDTO(InventoryMovement m) {
                return InventoryMovementDTO.builder()
                                .id(m.getId())
                                .product(InventoryMovementDTO.ProductSummaryDTO.builder()
                                                .id(m.getProduct().getId())
                                                .sku(m.getProduct().getSku())
                                                .name(m.getProduct().getName())
                                                .build())
                                .fromWarehouse(m.getFromWarehouse() != null
                                                ? InventoryMovementDTO.WarehouseSummaryDTO.builder()
                                                                .id(m.getFromWarehouse().getId())
                                                                .name(m.getFromWarehouse().getName())
                                                                .code(m.getFromWarehouse().getCode())
                                                                .build()
                                                : null)
                                .toWarehouse(m.getToWarehouse() != null
                                                ? InventoryMovementDTO.WarehouseSummaryDTO.builder()
                                                                .id(m.getToWarehouse().getId())
                                                                .name(m.getToWarehouse().getName())
                                                                .code(m.getToWarehouse().getCode())
                                                                .build()
                                                : null)
                                .quantity(m.getQuantity())
                                .movementType(m.getMovementType())
                                .reason(m.getReason())
                                .referenceNumber(m.getReferenceNumber())
                                .performedBy(InventoryMovementDTO.UserSummaryDTO.builder()
                                                .id(m.getPerformedBy().getId())
                                                .username(m.getPerformedBy().getUsername())
                                                .build())
                                .movementDate(m.getMovementDate())
                                .createdAt(m.getCreatedAt())
                                .build();
        }
}