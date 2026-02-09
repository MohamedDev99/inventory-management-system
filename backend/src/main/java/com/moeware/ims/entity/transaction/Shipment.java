package com.moeware.ims.entity.transaction;

import com.moeware.ims.entity.VersionedEntity;
import com.moeware.ims.entity.User;
import com.moeware.ims.entity.Warehouse;
import com.moeware.ims.enums.ShipmentStatus;
import com.moeware.ims.enums.ShippingMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Shipment entity
 * Tracks shipment details and delivery status for sales orders
 */
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
@Schema(description = "Shipment tracking information for sales order delivery")
public class Shipment extends VersionedEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Schema(description = "Unique identifier for the shipment", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        private Long id;

        @NotBlank(message = "Shipment number is required")
        @Size(max = 50)
        @Column(name = "shipment_number", nullable = false, unique = true)
        @Schema(description = "Unique shipment number for tracking", example = "SHIP-20260131-0001", requiredMode = Schema.RequiredMode.REQUIRED)
        private String shipmentNumber;

        @NotNull(message = "Sales order is required")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "sales_order_id", nullable = false)
        @Schema(description = "Sales order being shipped to customer", requiredMode = Schema.RequiredMode.REQUIRED)
        private SalesOrder salesOrder;

        @NotBlank(message = "Carrier is required")
        @Size(max = 100)
        @Column(nullable = false)
        @Schema(description = "Shipping carrier company name", example = "FedEx", requiredMode = Schema.RequiredMode.REQUIRED)
        private String carrier;

        @Size(max = 100)
        @Column(name = "tracking_number")
        @Schema(description = "Carrier-provided tracking number for shipment", example = "1Z999AA10123456784")
        private String trackingNumber;

        @NotNull(message = "Shipping method is required")
        @Enumerated(EnumType.STRING)
        @Column(name = "shipping_method", length = 50, nullable = false)
        @Schema(description = "Shipping service level selected", example = "EXPRESS", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
                        "STANDARD", "EXPRESS", "OVERNIGHT", "GROUND" })
        private ShippingMethod shippingMethod;

        @Column(name = "estimated_delivery_date")
        @Schema(description = "Estimated date when shipment will be delivered", example = "2026-02-05")
        private LocalDate estimatedDeliveryDate;

        @Column(name = "actual_delivery_date")
        @Schema(description = "Actual date when shipment was delivered", example = "2026-02-04", accessMode = Schema.AccessMode.READ_ONLY)
        private LocalDate actualDeliveryDate;

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        @Digits(integer = 10, fraction = 2)
        @Column(name = "shipping_cost", nullable = false, precision = 12, scale = 2)
        @Builder.Default
        @Schema(description = "Cost charged for shipping", example = "15.99", defaultValue = "0.00", minimum = "0")
        private BigDecimal shippingCost = BigDecimal.ZERO;

        @DecimalMin(value = "0.0", inclusive = true)
        @Digits(integer = 8, fraction = 2)
        @Column(precision = 10, scale = 2)
        @Schema(description = "Total package weight in kilograms", example = "2.5", minimum = "0")
        private BigDecimal weight;

        @Size(max = 50)
        @Column(columnDefinition = "VARCHAR(50)")
        @Schema(description = "Package dimensions in format: Length x Width x Height (cm)", example = "30x20x15")
        private String dimensions;

        @NotNull(message = "Status is required")
        @Enumerated(EnumType.STRING)
        @Column(length = 20, nullable = false)
        @Builder.Default
        @Schema(description = "Current status of the shipment", example = "PENDING", defaultValue = "PENDING", allowableValues = {
                        "PENDING", "IN_TRANSIT", "DELIVERED", "RETURNED",
                        "FAILED" }, requiredMode = Schema.RequiredMode.REQUIRED)
        private ShipmentStatus status = ShipmentStatus.PENDING;

        @NotNull(message = "Origin warehouse is required")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "shipped_from_warehouse_id", nullable = false)
        @Schema(description = "Warehouse from which the shipment originated", requiredMode = Schema.RequiredMode.REQUIRED)
        private Warehouse shippedFromWarehouse;

        @NotNull(message = "Shipper is required")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "shipped_by", nullable = false)
        @Schema(description = "User who processed and shipped the order", requiredMode = Schema.RequiredMode.REQUIRED)
        private User shippedBy;

        @Column(columnDefinition = "TEXT")
        @Schema(description = "Additional shipment notes or special handling instructions", example = "Left at front door per customer instructions. Signature not required.")
        private String notes;
}