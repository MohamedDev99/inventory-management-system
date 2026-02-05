package com.moeware.ims.entity.transaction;

import com.moeware.ims.entity.BaseEntity;
import com.moeware.ims.entity.User;
import com.moeware.ims.entity.Warehouse;
import com.moeware.ims.enums.ShipmentStatus;
import com.moeware.ims.enums.ShippingMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "shipments", uniqueConstraints = @UniqueConstraint(columnNames = "shipment_number"), indexes = {
        @Index(name = "idx_shipment_sales_order", columnList = "sales_order_id"),
        @Index(name = "idx_shipment_tracking", columnList = "tracking_number"),
        @Index(name = "idx_shipment_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Shipment number is required")
    @Size(max = 50)
    @Column(name = "shipment_number", nullable = false, unique = true)
    private String shipmentNumber;

    @NotNull(message = "Sales order is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    @NotBlank(message = "Carrier is required")
    @Size(max = 100)
    @Column(nullable = false)
    private String carrier;

    @Size(max = 100)
    @Column(name = "tracking_number")
    private String trackingNumber;

    @NotNull(message = "Shipping method is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_method", length = 50, nullable = false)
    private ShippingMethod shippingMethod;

    @Column(name = "estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDate actualDeliveryDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "shipping_cost", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 8, fraction = 2)
    @Column(precision = 10, scale = 2)
    private BigDecimal weight;

    @Size(max = 50)
    @Column(columnDefinition = "VARCHAR(50)")
    private String dimensions;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @NotNull(message = "Origin warehouse is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipped_from_warehouse_id", nullable = false)
    private Warehouse shippedFromWarehouse;

    @NotNull(message = "Shipper is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipped_by", nullable = false)
    private User shippedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;
}