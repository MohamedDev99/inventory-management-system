package com.moeware.ims.service.transaction;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.entity.User;
import com.moeware.ims.entity.inventory.InventoryItem;
import com.moeware.ims.entity.inventory.Product;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.entity.transaction.InventoryMovement;
import com.moeware.ims.entity.transaction.PurchaseOrder;
import com.moeware.ims.entity.transaction.PurchaseOrderItem;
import com.moeware.ims.entity.transaction.SalesOrder;
import com.moeware.ims.entity.transaction.SalesOrderItem;
import com.moeware.ims.enums.transaction.MovementType;
import com.moeware.ims.exception.transaction.stockAdjustment.InsufficientStockException;
import com.moeware.ims.repository.inventory.InventoryItemRepository;
import com.moeware.ims.repository.transaction.InventoryMovementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Central service for all inventory operations triggered by order workflows.
 *
 * <p>
 * Handles three integration points:
 * <ol>
 * <li><b>Sales Order CONFIRM</b> — checks availability for every line item (no
 * stock deduction yet).</li>
 * <li><b>Sales Order FULFILL</b> — deducts stock and creates SHIPMENT movement
 * records.</li>
 * <li><b>Sales Order CANCEL</b> — releases any previously confirmed reservation
 * if the order was CONFIRMED.</li>
 * <li><b>Purchase Order RECEIVE</b> — increments stock and creates RECEIPT
 * movement records per item received.</li>
 * </ol>
 *
 * <p>
 * This service is intentionally scoped to <em>order-driven</em> inventory
 * changes only.
 * General transfers, adjustments, and valuations remain in
 * {@code InventoryService}.
 *
 * <p>
 * All methods are {@code @Transactional} — callers (the order services) should
 * already be
 * inside a transaction, so these participate in the same unit of work via
 * Spring's propagation default
 * (REQUIRED). If any step fails the entire order transition rolls back.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderInventoryService {

        private final InventoryItemRepository inventoryItemRepository;
        private final InventoryMovementRepository inventoryMovementRepository;

        // ─── SALES ORDER: CONFIRM ────────────────────────────────────────────────

        /**
         * Validates that sufficient stock exists in the order's warehouse for every
         * line item. Throws {@link InsufficientStockException} for the first item
         * that fails the check. No stock is modified at this stage.
         *
         * <p>
         * Called from {@code SalesOrderService.confirmSalesOrder()}.
         *
         * @param salesOrder the order being confirmed (must be in PENDING status)
         * @throws InsufficientStockException if any product has insufficient stock
         */
        @Transactional
        public void checkInventoryAvailability(SalesOrder salesOrder) {
                Warehouse warehouse = salesOrder.getWarehouse();
                log.info("Checking inventory availability for SO {} in warehouse {}",
                                salesOrder.getSoNumber(), warehouse.getName());

                for (SalesOrderItem item : salesOrder.getItems()) {
                        Product product = item.getProduct();
                        int requested = item.getQuantity();

                        InventoryItem inventory = inventoryItemRepository
                                        .findByProductAndWarehouse(product, warehouse)
                                        .orElseThrow(() -> new InsufficientStockException(
                                                        String.format("Product '%s' (SKU: %s) is not stocked in warehouse '%s'.",
                                                                        product.getName(), product.getSku(),
                                                                        warehouse.getName())));

                        if (inventory.getQuantity() < requested) {
                                throw new InsufficientStockException(
                                                String.format("Insufficient stock for '%s' (SKU: %s) in warehouse '%s'. "
                                                                +
                                                                "Available: %d, Required: %d.",
                                                                product.getName(), product.getSku(),
                                                                warehouse.getName(),
                                                                inventory.getQuantity(), requested));
                        }

                        log.debug("Stock OK — product: {}, available: {}, required: {}",
                                        product.getSku(), inventory.getQuantity(), requested);
                }

                log.info("Inventory availability check passed for SO {}", salesOrder.getSoNumber());
        }

        // ─── SALES ORDER: FULFILL ────────────────────────────────────────────────

        /**
         * Deducts stock from the order's warehouse for every line item and creates
         * an {@link InventoryMovement} record of type {@code SHIPMENT} for each.
         *
         * <p>
         * Uses {@link InventoryItem#removeStock(Integer)} which performs its own
         * non-negativity guard, providing a second safety net after the confirmation
         * check.
         *
         * <p>
         * Called from {@code SalesOrderService.fulfillSalesOrder()}.
         *
         * @param salesOrder  the order being fulfilled (must be in CONFIRMED status)
         * @param performedBy the user triggering the fulfillment
         */
        @Transactional
        public void deductInventoryForSalesOrder(SalesOrder salesOrder, User performedBy) {
                Warehouse warehouse = salesOrder.getWarehouse();
                log.info("Deducting inventory for SO {} from warehouse {}",
                                salesOrder.getSoNumber(), warehouse.getName());

                for (SalesOrderItem item : salesOrder.getItems()) {
                        Product product = item.getProduct();
                        int quantity = item.getQuantity();

                        InventoryItem inventory = inventoryItemRepository
                                        .findByProductAndWarehouse(product, warehouse)
                                        .orElseThrow(() -> new InsufficientStockException(
                                                        String.format("Product '%s' (SKU: %s) not found in warehouse '%s' during fulfillment.",
                                                                        product.getName(), product.getSku(),
                                                                        warehouse.getName())));

                        // Deduct stock (throws InsufficientStockException internally if quantity < 0)
                        inventory.removeStock(quantity);
                        inventoryItemRepository.save(inventory);

                        // Create SHIPMENT movement record
                        InventoryMovement movement = InventoryMovement.builder()
                                        .product(product)
                                        .fromWarehouse(warehouse)
                                        .toWarehouse(null) // null = outbound to customer
                                        .quantity(quantity)
                                        .movementType(MovementType.SHIPMENT)
                                        .reason("Sales order fulfillment")
                                        .referenceNumber(salesOrder.getSoNumber())
                                        .performedBy(performedBy)
                                        .movementDate(LocalDateTime.now())
                                        .build();

                        inventoryMovementRepository.save(movement);

                        log.debug("Deducted {} units of {} — remaining stock: {}",
                                        quantity, product.getSku(), inventory.getQuantity());
                }

                log.info("Inventory deduction complete for SO {}. {} line items processed.",
                                salesOrder.getSoNumber(), salesOrder.getItems().size());
        }

        // ─── SALES ORDER: CANCEL ─────────────────────────────────────────────────

        /**
         * Releases previously deducted stock back to the warehouse when a FULFILLED
         * sales order is cancelled. If the order was only CONFIRMED (stock was checked
         * but not deducted) no action is taken.
         *
         * <p>
         * Creates an {@link InventoryMovement} of type {@code ADJUSTMENT} per line item
         * to maintain a full audit trail.
         *
         * <p>
         * Called from {@code SalesOrderService.cancelSalesOrder()} when the order was
         * in
         * FULFILLED status at the time of cancellation.
         *
         * @param salesOrder  the order being cancelled
         * @param performedBy the user triggering the cancellation
         */
        @Transactional
        public void releaseInventoryForCancelledSalesOrder(SalesOrder salesOrder, User performedBy) {
                Warehouse warehouse = salesOrder.getWarehouse();
                log.info("Releasing inventory for cancelled SO {} back to warehouse {}",
                                salesOrder.getSoNumber(), warehouse.getName());

                for (SalesOrderItem item : salesOrder.getItems()) {
                        Product product = item.getProduct();
                        int quantity = item.getQuantity();

                        // Get or create the inventory record (edge case: product might have been
                        // removed)
                        InventoryItem inventory = inventoryItemRepository
                                        .findByProductAndWarehouse(product, warehouse)
                                        .orElseGet(() -> {
                                                log.warn("InventoryItem not found for product {} in warehouse {} during cancel — "
                                                                +
                                                                "creating new record.", product.getSku(),
                                                                warehouse.getName());
                                                return InventoryItem.builder()
                                                                .product(product)
                                                                .warehouse(warehouse)
                                                                .quantity(0)
                                                                .build();
                                        });

                        inventory.addStock(quantity);
                        inventoryItemRepository.save(inventory);

                        // Audit trail for the reversal
                        InventoryMovement movement = InventoryMovement.builder()
                                        .product(product)
                                        .fromWarehouse(null) // null = inbound (reversal)
                                        .toWarehouse(warehouse)
                                        .quantity(quantity)
                                        .movementType(MovementType.ADJUSTMENT)
                                        .reason("Cancellation of fulfilled sales order")
                                        .referenceNumber(salesOrder.getSoNumber())
                                        .performedBy(performedBy)
                                        .movementDate(LocalDateTime.now())
                                        .build();

                        inventoryMovementRepository.save(movement);

                        log.debug("Returned {} units of {} to warehouse — new stock: {}",
                                        quantity, product.getSku(), inventory.getQuantity());
                }

                log.info("Inventory released for cancelled SO {}.", salesOrder.getSoNumber());
        }

        // ─── PURCHASE ORDER: RECEIVE ─────────────────────────────────────────────

        /**
         * Increments stock and creates a {@link InventoryMovement} of type
         * {@code RECEIPT}
         * for each item that was actually received (i.e.,
         * {@code quantityReceived > 0}).
         *
         * <p>
         * Supports partial receipts — only items with a positive
         * {@code quantityReceived}
         * are processed. The stock is added to the PO's associated warehouse.
         *
         * <p>
         * Called from {@code PurchaseOrderService.receivePurchaseOrder()} after
         * updating the per-item {@code quantityReceived} values.
         *
         * @param purchaseOrder the PO being received (must be in APPROVED status)
         * @param performedBy   the user performing the receipt
         */
        @Transactional
        public void receiveInventoryForPurchaseOrder(PurchaseOrder purchaseOrder, User performedBy) {
                Warehouse warehouse = purchaseOrder.getWarehouse();
                log.info("Receiving inventory for PO {} into warehouse {}",
                                purchaseOrder.getPoNumber(), warehouse.getName());

                List<PurchaseOrderItem> items = purchaseOrder.getItems();
                int processedCount = 0;

                for (PurchaseOrderItem item : items) {
                        int qtyReceived = item.getQuantityReceived();
                        if (qtyReceived <= 0) {
                                log.debug("Skipping product {} — quantityReceived is 0", item.getProduct().getSku());
                                continue;
                        }

                        Product product = item.getProduct();

                        // Get existing inventory record or create one if this product is new to the
                        // warehouse
                        InventoryItem inventory = inventoryItemRepository
                                        .findByProductAndWarehouse(product, warehouse)
                                        .orElseGet(() -> {
                                                log.info("Creating new InventoryItem for product {} in warehouse {}",
                                                                product.getSku(), warehouse.getName());
                                                return InventoryItem.builder()
                                                                .product(product)
                                                                .warehouse(warehouse)
                                                                .quantity(0)
                                                                .build();
                                        });

                        inventory.addStock(qtyReceived);
                        inventoryItemRepository.save(inventory);

                        // Create RECEIPT movement record
                        InventoryMovement movement = InventoryMovement.builder()
                                        .product(product)
                                        .fromWarehouse(null) // null = inbound from supplier
                                        .toWarehouse(warehouse)
                                        .quantity(qtyReceived)
                                        .movementType(MovementType.RECEIPT)
                                        .reason("Purchase order receipt")
                                        .referenceNumber(purchaseOrder.getPoNumber())
                                        .performedBy(performedBy)
                                        .movementDate(LocalDateTime.now())
                                        .build();

                        inventoryMovementRepository.save(movement);

                        log.debug("Received {} units of {} — new stock in {}: {}",
                                        qtyReceived, product.getSku(), warehouse.getName(), inventory.getQuantity());
                        processedCount++;
                }

                log.info("PO {} receipt complete. {}/{} line items processed.",
                                purchaseOrder.getPoNumber(), processedCount, items.size());
        }
}