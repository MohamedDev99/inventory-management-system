package com.moeware.ims.service.transaction;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentApproveRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentRejectRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentRequest;
import com.moeware.ims.dto.transaction.stockAdjustment.StockAdjustmentResponse;
import com.moeware.ims.entity.User;
import com.moeware.ims.entity.inventory.InventoryItem;
import com.moeware.ims.entity.inventory.Product;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.entity.transaction.InventoryMovement;
import com.moeware.ims.entity.transaction.StockAdjustment;
import com.moeware.ims.enums.transaction.AdjustmentReason;
import com.moeware.ims.enums.transaction.MovementType;
import com.moeware.ims.enums.transaction.StockAdjustmentStatus;
import com.moeware.ims.exception.inventory.inventoryItem.InventoryItemNotFoundException;
import com.moeware.ims.exception.inventory.product.ProductNotFoundException;
import com.moeware.ims.exception.staff.warehouse.WarehouseNotFoundException;
import com.moeware.ims.exception.transaction.stockAdjustment.InsufficientStockException;
import com.moeware.ims.exception.transaction.stockAdjustment.StockAdjustmentAlreadyProcessedException;
import com.moeware.ims.exception.transaction.stockAdjustment.StockAdjustmentNotFoundException;
import com.moeware.ims.exception.user.UserNotFoundException;
import com.moeware.ims.repository.UserRepository;
import com.moeware.ims.repository.inventory.InventoryItemRepository;
import com.moeware.ims.repository.inventory.ProductRepository;
import com.moeware.ims.repository.staff.WarehouseRepository;
import com.moeware.ims.repository.transaction.InventoryMovementRepository;
import com.moeware.ims.repository.transaction.StockAdjustmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for stock adjustment lifecycle management.
 *
 * <p>
 * Handles the full approval workflow:
 *
 * <pre>
 *   PENDING  →  APPROVED  (inventory quantity updated + movement record created)
 *            →  REJECTED  (no inventory change)
 * </pre>
 * </p>
 *
 * @author MoeWare Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockAdjustmentService {

    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    // ================================================================
    // Create
    // ================================================================

    /**
     * Create a new stock adjustment request.
     *
     * <p>
     * The adjustment is saved with {@link StockAdjustmentStatus#PENDING} and
     * does NOT modify inventory yet — that happens only on approval.
     * </p>
     *
     * @param request adjustment request
     * @return created adjustment response
     * @throws ProductNotFoundException       if the product does not exist
     * @throws WarehouseNotFoundException     if the warehouse does not exist
     * @throws InventoryItemNotFoundException if no inventory record exists for the
     *                                        product/warehouse combination
     * @throws InsufficientStockException     if a REMOVE adjustment would make
     *                                        stock negative
     * @throws UserNotFoundException          if the performing user does not exist
     */
    public StockAdjustmentResponse createAdjustment(StockAdjustmentRequest request) {
        log.info("Creating stock adjustment - product={}, warehouse={}, change={}",
                request.getProductId(), request.getWarehouseId(), request.getQuantityChange());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new WarehouseNotFoundException(request.getWarehouseId()));

        User performedBy = userRepository.findById(request.getPerformedBy())
                .orElseThrow(() -> new UserNotFoundException(request.getPerformedBy()));

        InventoryItem inventory = inventoryItemRepository.findByProductAndWarehouse(product, warehouse)
                .orElseThrow(
                        () -> new InventoryItemNotFoundException(request.getProductId(), request.getWarehouseId()));

        int quantityBefore = inventory.getQuantity();
        int quantityAfter = quantityBefore + request.getQuantityChange();

        // Guard: result must not be negative
        if (quantityAfter < 0) {
            throw new InsufficientStockException(
                    request.getProductId(), request.getWarehouseId(),
                    quantityBefore, quantityAfter);
        }

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

        final StockAdjustment createdAdjustment = stockAdjustmentRepository.save(adjustment);

        log.info("Stock adjustment created - id={}, status=PENDING", adjustment.getId());
        return toResponse(createdAdjustment);
    }

    // ================================================================
    // Read
    // ================================================================

    /**
     * Get all stock adjustments with optional filters and pagination.
     *
     * @param productId   optional product filter
     * @param warehouseId optional warehouse filter
     * @param status      optional status filter
     * @param reason      optional reason filter
     * @param startDate   optional date range start (inclusive)
     * @param endDate     optional date range end (inclusive)
     * @param pageable    pagination and sort
     * @return paginated adjustment responses
     */
    @Transactional(readOnly = true)
    public Page<StockAdjustmentResponse> getAllAdjustments(
            Long productId,
            Long warehouseId,
            StockAdjustmentStatus status,
            AdjustmentReason reason,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        return stockAdjustmentRepository
                .findAllWithFilters(productId, warehouseId, status, reason, startDate, endDate, pageable)
                .map(this::toResponse);
    }

    /**
     * Get a single stock adjustment by ID.
     *
     * @param id adjustment ID
     * @return adjustment response
     * @throws StockAdjustmentNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public StockAdjustmentResponse getAdjustmentById(Long id) {
        StockAdjustment adjustment = stockAdjustmentRepository.findById(id)
                .orElseThrow(() -> new StockAdjustmentNotFoundException(id));
        return toResponse(adjustment);
    }

    /**
     * Get all adjustments currently in {@link StockAdjustmentStatus#PENDING}
     * status.
     *
     * @return list of pending adjustment responses
     */
    @Transactional(readOnly = true)
    public List<StockAdjustmentResponse> getPendingAdjustments() {
        return stockAdjustmentRepository.findPendingAdjustments()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all valid adjustment reasons.
     *
     * @return list of {@link AdjustmentReason} values
     */
    @Transactional(readOnly = true)
    public List<AdjustmentReason> getAdjustmentReasons() {
        return Arrays.asList(AdjustmentReason.values());
    }

    // ================================================================
    // Approve / Reject
    // ================================================================

    /**
     * Approve a pending stock adjustment.
     *
     * <p>
     * Applies the quantity change to the inventory item and records an
     * {@link InventoryMovement} of type {@link MovementType#ADJUSTMENT}.
     * </p>
     *
     * @param id      adjustment ID
     * @param request approve request carrying the approver's user ID and optional
     *                notes
     * @return updated adjustment response
     * @throws StockAdjustmentNotFoundException         if not found
     * @throws StockAdjustmentAlreadyProcessedException if already APPROVED or
     *                                                  REJECTED
     * @throws InsufficientStockException               if approval would produce
     *                                                  negative stock
     * @throws UserNotFoundException                    if approver not found
     */
    public StockAdjustmentResponse approveAdjustment(Long id, StockAdjustmentApproveRequest request) {
        log.info("Approving stock adjustment id={} by user={}", id, request.getApprovedBy());

        StockAdjustment adjustment = stockAdjustmentRepository.findById(id)
                .orElseThrow(() -> new StockAdjustmentNotFoundException(id));

        guardNotAlreadyProcessed(adjustment);

        User approver = userRepository.findById(request.getApprovedBy())
                .orElseThrow(() -> new UserNotFoundException(request.getApprovedBy()));

        // Re-validate stock at approval time — another operation may have changed it
        InventoryItem inventory = inventoryItemRepository
                .findByProductAndWarehouse(adjustment.getProduct(), adjustment.getWarehouse())
                .orElseThrow(() -> new InventoryItemNotFoundException(
                        adjustment.getProduct().getId(), adjustment.getWarehouse().getId()));

        int currentStock = inventory.getQuantity();
        int resultingStock = currentStock + adjustment.getQuantityChange();

        if (resultingStock < 0) {
            throw new InsufficientStockException(
                    adjustment.getProduct().getId(),
                    adjustment.getWarehouse().getId(),
                    currentStock,
                    resultingStock);
        }

        // Apply inventory change
        inventory.setQuantity(resultingStock);
        inventoryItemRepository.save(inventory);

        // Record movement
        InventoryMovement movement = InventoryMovement.builder()
                .product(adjustment.getProduct())
                .fromWarehouse(adjustment.getQuantityChange() < 0 ? adjustment.getWarehouse() : null)
                .toWarehouse(adjustment.getQuantityChange() > 0 ? adjustment.getWarehouse() : null)
                .quantity(Math.abs(adjustment.getQuantityChange()))
                .movementType(MovementType.ADJUSTMENT)
                .reason(adjustment.getReason().name() + (adjustment.getNotes() != null
                        ? ": " + adjustment.getNotes()
                        : ""))
                .referenceNumber("ADJ-" + adjustment.getId())
                .performedBy(approver)
                .movementDate(LocalDateTime.now())
                .build();

        inventoryMovementRepository.save(movement);

        // Update adjustment
        adjustment.setStatus(StockAdjustmentStatus.APPROVED);
        adjustment.setApprovedBy(approver);
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            adjustment.setNotes(request.getNotes());
        }

        final StockAdjustment approvedAdjustment = stockAdjustmentRepository.save(adjustment);

        log.info("Stock adjustment id={} approved. Inventory updated: {} → {}",
                id, currentStock, resultingStock);

        return toResponse(approvedAdjustment);
    }

    /**
     * Reject a pending stock adjustment.
     *
     * <p>
     * No inventory change occurs. The rejection reason is appended to the
     * adjustment notes for audit purposes.
     * </p>
     *
     * @param id      adjustment ID
     * @param request reject request carrying the rejector's user ID and reason
     * @return updated adjustment response
     * @throws StockAdjustmentNotFoundException         if not found
     * @throws StockAdjustmentAlreadyProcessedException if already APPROVED or
     *                                                  REJECTED
     * @throws UserNotFoundException                    if rejector not found
     */
    public StockAdjustmentResponse rejectAdjustment(Long id, StockAdjustmentRejectRequest request) {
        log.info("Rejecting stock adjustment id={} by user={}", id, request.getRejectedBy());

        StockAdjustment adjustment = stockAdjustmentRepository.findById(id)
                .orElseThrow(() -> new StockAdjustmentNotFoundException(id));

        guardNotAlreadyProcessed(adjustment);

        User rejector = userRepository.findById(request.getRejectedBy())
                .orElseThrow(() -> new UserNotFoundException(request.getRejectedBy()));

        adjustment.setStatus(StockAdjustmentStatus.REJECTED);
        adjustment.setApprovedBy(rejector);

        // Append rejection reason to notes so the audit trail is self-contained
        String rejectionNote = "REJECTED: " + request.getReason();
        adjustment.setNotes(adjustment.getNotes() != null
                ? adjustment.getNotes() + " | " + rejectionNote
                : rejectionNote);

        final StockAdjustment rejectedAdjustment = stockAdjustmentRepository.save(adjustment);

        log.info("Stock adjustment id={} rejected.", id);
        return toResponse(rejectedAdjustment);
    }

    // ================================================================
    // Private Helpers
    // ================================================================

    /**
     * Guard: throws if the adjustment is not in PENDING status.
     */
    private void guardNotAlreadyProcessed(StockAdjustment adjustment) {
        if (adjustment.getStatus() != StockAdjustmentStatus.PENDING) {
            throw new StockAdjustmentAlreadyProcessedException(adjustment.getId(), adjustment.getStatus());
        }
    }

    /**
     * Map {@link StockAdjustment} entity to {@link StockAdjustmentResponse}.
     */
    private StockAdjustmentResponse toResponse(StockAdjustment a) {
        return StockAdjustmentResponse.builder()
                .id(a.getId())
                .productId(a.getProduct().getId())
                .warehouseId(a.getWarehouse().getId())
                .quantityBefore(a.getQuantityBefore())
                .quantityAfter(a.getQuantityAfter())
                .quantityChange(a.getQuantityChange())
                .adjustmentType(a.getAdjustmentType())
                .reason(a.getReason())
                .status(a.getStatus())
                .performedBy(StockAdjustmentResponse.UserSummaryDTO.builder()
                        .id(a.getPerformedBy().getId())
                        .username(a.getPerformedBy().getUsername())
                        .build())
                .approvedBy(a.getApprovedBy() != null
                        ? StockAdjustmentResponse.UserSummaryDTO.builder()
                                .id(a.getApprovedBy().getId())
                                .username(a.getApprovedBy().getUsername())
                                .build()
                        : null)
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .build();
    }
}