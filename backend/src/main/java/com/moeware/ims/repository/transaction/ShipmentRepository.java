package com.moeware.ims.repository.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.entity.transaction.SalesOrder;
import com.moeware.ims.entity.transaction.Shipment;
import com.moeware.ims.enums.transaction.ShipmentStatus;

/**
 * Repository interface for Shipment entity
 * Manages shipment tracking and delivery status for sales orders
 */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    /**
     * Find shipment by unique shipment number
     */
    Optional<Shipment> findByShipmentNumber(String shipmentNumber);

    /**
     * Find shipment by tracking number
     */
    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    /**
     * Find all shipments for a specific sales order
     */
    Page<Shipment> findBySalesOrder(SalesOrder salesOrder, Pageable pageable);

    /**
     * Find shipments by status
     */
    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);

    /**
     * Find all shipments from a specific warehouse
     */
    Page<Shipment> findByShippedFromWarehouse(Warehouse warehouse, Pageable pageable);

    /**
     * Find shipments by carrier
     */
    Page<Shipment> findByCarrier(String carrier, Pageable pageable);

    /**
     * Find shipments with estimated delivery date in range
     */
    @Query("SELECT s FROM Shipment s WHERE s.estimatedDeliveryDate BETWEEN :startDate AND :endDate")
    Page<Shipment> findByEstimatedDeliveryDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    /**
     * Find shipments with actual delivery date in range
     */
    @Query("SELECT s FROM Shipment s WHERE s.actualDeliveryDate BETWEEN :startDate AND :endDate")
    Page<Shipment> findByActualDeliveryDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    /**
     * Find all pending shipments (not yet delivered)
     */
    @Query("SELECT s FROM Shipment s WHERE s.status IN ('PENDING', 'IN_TRANSIT')")
    List<Shipment> findPendingShipments();

    /**
     * Find overdue shipments (past estimated delivery date and not delivered)
     */
    @Query("SELECT s FROM Shipment s WHERE s.status NOT IN ('DELIVERED', 'RETURNED', 'FAILED') " +
            "AND s.estimatedDeliveryDate < :currentDate")
    List<Shipment> findOverdueShipments(@Param("currentDate") LocalDate currentDate);

    /**
     * Find shipments with filters
     */
    @Query("SELECT s FROM Shipment s WHERE " +
            "(:warehouseId IS NULL OR s.shippedFromWarehouse.id = :warehouseId) AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:carrier IS NULL OR LOWER(s.carrier) LIKE LOWER(CONCAT('%', :carrier, '%')))")
    Page<Shipment> findAllWithFilters(
            @Param("warehouseId") Long warehouseId,
            @Param("status") ShipmentStatus status,
            @Param("carrier") String carrier,
            Pageable pageable);

    /**
     * Count shipments by status
     */
    @Query("SELECT s.status, COUNT(s) FROM Shipment s GROUP BY s.status")
    List<Object[]> countByStatus();

    /**
     * Get shipment statistics for a warehouse
     */
    @Query("SELECT COUNT(s), AVG(s.shippingCost), SUM(s.weight) " +
            "FROM Shipment s WHERE s.shippedFromWarehouse.id = :warehouseId")
    List<Object[]> getWarehouseShipmentStats(@Param("warehouseId") Long warehouseId);

    /**
     * Check if shipment number already exists
     */
    boolean existsByShipmentNumber(String shipmentNumber);

    /**
     * Check if tracking number already exists
     */
    boolean existsByTrackingNumber(String trackingNumber);

    /**
     * Find shipments by sales order ID
     */
    @Query("SELECT s FROM Shipment s WHERE s.salesOrder.id = :salesOrderId")
    List<Shipment> findBySalesOrderId(@Param("salesOrderId") Long salesOrderId);
}
