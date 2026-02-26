package com.moeware.ims.service.transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.transaction.shipment.DeliverShipmentRequest;
import com.moeware.ims.dto.transaction.shipment.ShipmentRequest;
import com.moeware.ims.dto.transaction.shipment.ShipmentResponse;
import com.moeware.ims.dto.transaction.shipment.UpdateShipmentStatusRequest;
import com.moeware.ims.entity.transaction.SalesOrder;
import com.moeware.ims.entity.transaction.Shipment;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.entity.User;
import com.moeware.ims.enums.transaction.ShipmentStatus;
import com.moeware.ims.enums.transaction.SalesOrderStatus;
import com.moeware.ims.exception.transaction.shipment.ShipmentNotFoundException;
import com.moeware.ims.exception.ResourceNotFoundException;
import com.moeware.ims.repository.transaction.ShipmentRepository;
import com.moeware.ims.repository.transaction.SalesOrderRepository;
import com.moeware.ims.repository.staff.WarehouseRepository;
import com.moeware.ims.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing shipments linked to sales orders.
 * Handles creation, status transitions, and delivery confirmation.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShipmentService {

        private final ShipmentRepository shipmentRepository;
        private final SalesOrderRepository salesOrderRepository;
        private final WarehouseRepository warehouseRepository;
        private final UserRepository userRepository;

        // ─── READ ────────────────────────────────────────────────────────────────

        /**
         * Returns a paginated, filtered list of all shipments.
         */
        public Page<ShipmentResponse> getAllShipments(
                        Long warehouseId,
                        ShipmentStatus status,
                        String carrier,
                        Pageable pageable) {
                return shipmentRepository.findAllWithFilters(warehouseId, status, carrier, pageable)
                                .map(this::toResponse);
        }

        /**
         * Returns a single shipment by its internal ID.
         */
        public ShipmentResponse getShipmentById(Long id) {
                return toResponse(findShipmentOrThrow(id));
        }

        /**
         * Returns a single shipment by its tracking number.
         */
        public ShipmentResponse getShipmentByTrackingNumber(String trackingNumber) {
                Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                                .orElseThrow(() -> new ShipmentNotFoundException(trackingNumber));
                return toResponse(shipment);
        }

        /**
         * Returns all shipments belonging to a specific sales order.
         */
        public List<ShipmentResponse> getShipmentsBySalesOrder(Long salesOrderId) {
                return shipmentRepository.findBySalesOrderId(salesOrderId)
                                .stream().map(this::toResponse).toList();
        }

        /**
         * Returns all active (PENDING / IN_TRANSIT) shipments.
         */
        public List<ShipmentResponse> getPendingShipments() {
                return shipmentRepository.findPendingShipments()
                                .stream().map(this::toResponse).toList();
        }

        /**
         * Returns all shipments whose estimated delivery date has passed but are
         * not yet delivered, returned, or failed.
         */
        public List<ShipmentResponse> getOverdueShipments() {
                return shipmentRepository.findOverdueShipments(LocalDate.now())
                                .stream().map(this::toResponse).toList();
        }

        // ─── WRITE ───────────────────────────────────────────────────────────────

        /**
         * Creates a new shipment record for a fulfilled sales order.
         * The sales order must be in FULFILLED status to allow shipment creation.
         */
        @Transactional
        public ShipmentResponse createShipment(ShipmentRequest request, Long shippedByUserId) {
                SalesOrder salesOrder = salesOrderRepository.findById(request.getSalesOrderId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "SalesOrder", "id", request.getSalesOrderId()));

                if (salesOrder.getStatus() != SalesOrderStatus.FULFILLED) {
                        throw new IllegalStateException(
                                        "Cannot create shipment for sales order in status: " + salesOrder.getStatus()
                                                        + ". Order must be FULFILLED before shipping.");
                }

                Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Warehouse", "id", request.getWarehouseId()));

                User shippedBy = userRepository.findById(shippedByUserId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User", "id", shippedByUserId));

                String shipmentNumber = generateShipmentNumber();

                Shipment shipment = Shipment.builder()
                                .shipmentNumber(shipmentNumber)
                                .salesOrder(salesOrder)
                                .shippedFromWarehouse(warehouse)
                                .shippedBy(shippedBy)
                                .carrier(request.getCarrier())
                                .trackingNumber(request.getTrackingNumber())
                                .shippingMethod(request.getShippingMethod())
                                .estimatedDeliveryDate(request.getEstimatedDeliveryDate())
                                .shippingCost(request.getShippingCost())
                                .weight(request.getWeight())
                                .dimensions(request.getDimensions())
                                .notes(request.getNotes())
                                .status(ShipmentStatus.PENDING)
                                .build();

                Shipment saved = shipmentRepository.save(shipment);

                // Advance sales order to SHIPPED
                salesOrder.setStatus(SalesOrderStatus.SHIPPED);
                salesOrder.setShippingDate(LocalDate.now());
                salesOrderRepository.save(salesOrder);

                return toResponse(saved);
        }

        /**
         * Updates the status of an existing shipment (e.g., PENDING → IN_TRANSIT).
         * Use the dedicated deliverShipment endpoint for delivery confirmation.
         */
        @Transactional
        public ShipmentResponse updateShipmentStatus(Long id, UpdateShipmentStatusRequest request) {
                Shipment shipment = findShipmentOrThrow(id);

                // Guard: cannot move back to PENDING or set DELIVERED via this endpoint
                if (request.getStatus() == ShipmentStatus.DELIVERED) {
                        throw new IllegalArgumentException(
                                        "Use the /deliver endpoint to mark a shipment as delivered.");
                }
                if (shipment.getStatus() == ShipmentStatus.DELIVERED
                                || shipment.getStatus() == ShipmentStatus.RETURNED) {
                        throw new IllegalStateException(
                                        "Cannot change status of a shipment that is already "
                                                        + shipment.getStatus() + ".");
                }

                if (request.getNotes() != null) {
                        shipment.setNotes(request.getNotes());
                }
                shipment.setStatus(request.getStatus());
                return toResponse(shipmentRepository.save(shipment));
        }

        /**
         * Marks a shipment as DELIVERED and records the actual delivery date.
         * Also advances the linked sales order to DELIVERED status.
         */
        @Transactional
        public ShipmentResponse deliverShipment(Long id, DeliverShipmentRequest request) {
                Shipment shipment = findShipmentOrThrow(id);

                if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
                        throw new IllegalStateException("Shipment is already marked as delivered.");
                }
                if (shipment.getStatus() == ShipmentStatus.RETURNED
                                || shipment.getStatus() == ShipmentStatus.FAILED) {
                        throw new IllegalStateException(
                                        "Cannot deliver a shipment with status: " + shipment.getStatus());
                }

                shipment.setStatus(ShipmentStatus.DELIVERED);
                shipment.setActualDeliveryDate(request.getActualDeliveryDate());
                if (request.getNotes() != null) {
                        shipment.setNotes(request.getNotes());
                }

                Shipment saved = shipmentRepository.save(shipment);

                // Advance sales order to DELIVERED
                SalesOrder salesOrder = shipment.getSalesOrder();
                salesOrder.setStatus(SalesOrderStatus.DELIVERED);
                salesOrder.setDeliveryDate(request.getActualDeliveryDate());
                salesOrderRepository.save(salesOrder);

                return toResponse(saved);
        }

        // ─── HELPERS ─────────────────────────────────────────────────────────────

        private Shipment findShipmentOrThrow(Long id) {
                return shipmentRepository.findById(id)
                                .orElseThrow(() -> new ShipmentNotFoundException(id));
        }

        /**
         * Generates a unique shipment number in the format SHIP-YYYYMMDD-XXXX.
         * The sequence resets daily.
         */
        private String generateShipmentNumber() {
                LocalDate today = LocalDate.now();
                String dateStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                // Count how many shipments already exist for today to derive the next sequence
                long existingToday = shipmentRepository.findAllWithFilters(null, null, null,
                                Pageable.unpaged()).stream()
                                .filter(s -> s.getShipmentNumber().startsWith("SHIP-" + dateStr))
                                .count();

                String sequence = String.format("%04d", existingToday + 1);
                String candidate = "SHIP-" + dateStr + "-" + sequence;

                // Ensure uniqueness (handles edge cases)
                while (shipmentRepository.existsByShipmentNumber(candidate)) {
                        existingToday++;
                        sequence = String.format("%04d", existingToday + 1);
                        candidate = "SHIP-" + dateStr + "-" + sequence;
                }
                return candidate;
        }

        private ShipmentResponse toResponse(Shipment s) {
                return ShipmentResponse.builder()
                                .id(s.getId())
                                .shipmentNumber(s.getShipmentNumber())
                                .salesOrder(ShipmentResponse.SalesOrderSummary.builder()
                                                .id(s.getSalesOrder().getId())
                                                .soNumber(s.getSalesOrder().getSoNumber())
                                                .customerName(s.getSalesOrder().getCustomerName())
                                                .build())
                                .shippedFromWarehouse(ShipmentResponse.WarehouseSummary.builder()
                                                .id(s.getShippedFromWarehouse().getId())
                                                .name(s.getShippedFromWarehouse().getName())
                                                .code(s.getShippedFromWarehouse().getCode())
                                                .build())
                                .shippedBy(ShipmentResponse.UserSummary.builder()
                                                .id(s.getShippedBy().getId())
                                                .username(s.getShippedBy().getUsername())
                                                .email(s.getShippedBy().getEmail())
                                                .build())
                                .carrier(s.getCarrier())
                                .trackingNumber(s.getTrackingNumber())
                                .shippingMethod(s.getShippingMethod())
                                .estimatedDeliveryDate(s.getEstimatedDeliveryDate())
                                .actualDeliveryDate(s.getActualDeliveryDate())
                                .shippingCost(s.getShippingCost())
                                .weight(s.getWeight())
                                .dimensions(s.getDimensions())
                                .status(s.getStatus())
                                .notes(s.getNotes())
                                .createdAt(s.getCreatedAt())
                                .updatedAt(s.getUpdatedAt())
                                .version(s.getVersion())
                                .build();
        }
}