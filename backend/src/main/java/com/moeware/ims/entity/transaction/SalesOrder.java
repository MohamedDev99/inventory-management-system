package com.moeware.ims.entity.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.moeware.ims.entity.User;
import com.moeware.ims.entity.VersionedEntity;
import com.moeware.ims.entity.staff.Customer;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.enums.transaction.SalesOrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Sales Order entity
 * Represents an order placed by a customer
 */
@Entity
@Table(name = "sales_orders", uniqueConstraints = @UniqueConstraint(columnNames = "so_number"), indexes = {
        @Index(name = "idx_so_customer", columnList = "customer_id"),
        @Index(name = "idx_so_warehouse", columnList = "warehouse_id"),
        @Index(name = "idx_so_status", columnList = "status"),
        @Index(name = "idx_so_order_date", columnList = "order_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Sales Order representing a customer's purchase")
public class SalesOrder extends VersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the sales order", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "SO number is required")
    @Size(max = 50)
    @Pattern(regexp = "^SO-\\d{8}-\\d+$", message = "SO number must follow SO-YYYYMMDD-SEQ format")
    @Column(name = "so_number", nullable = false, unique = true)
    @Schema(description = "Unique sales order number", example = "SO-20260131-0001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String soNumber;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @Schema(description = "Customer who placed the order", requiredMode = Schema.RequiredMode.REQUIRED)
    private Customer customer;

    // Denormalized customer data for historical accuracy
    @NotBlank(message = "Customer name is required")
    @Size(max = 255)
    @Column(name = "customer_name", nullable = false)
    @Schema(description = "Customer name at time of order (denormalized for historical accuracy)", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email
    @Size(max = 255)
    @Column(name = "customer_email", nullable = false)
    @Schema(description = "Customer email address at time of order", example = "john.doe@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerEmail;

    @Size(max = 50)
    @Column(name = "customer_phone")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone must be in E.164 format")
    @Schema(description = "Customer phone number at time of order", example = "+15550200")
    private String customerPhone;

    @NotBlank(message = "Shipping address is required")
    @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Full shipping street address", example = "789 Customer St, Apt 4B", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shippingAddress;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    @Column(nullable = false)
    @Schema(description = "Shipping city", example = "New York", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20)
    @Column(name = "postal_code", nullable = false)
    @Schema(description = "Shipping postal/ZIP code", example = "10001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String postalCode;

    @NotNull(message = "Warehouse is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @Schema(description = "Warehouse responsible for fulfilling this order", requiredMode = Schema.RequiredMode.REQUIRED)
    private Warehouse warehouse;

    @NotNull(message = "Creator is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    @Schema(description = "User who created this sales order", requiredMode = Schema.RequiredMode.REQUIRED)
    private User createdByUser;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    @Schema(description = "Current status of the sales order", example = "PENDING", defaultValue = "PENDING", allowableValues = {
            "PENDING", "CONFIRMED", "FULFILLED", "SHIPPED", "DELIVERED",
            "CANCELLED" }, requiredMode = Schema.RequiredMode.REQUIRED)
    private SalesOrderStatus status = SalesOrderStatus.PENDING;

    @NotNull(message = "Order date is required")
    @Column(name = "order_date", nullable = false)
    @Schema(description = "Date when the customer placed the order", example = "2026-01-31", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate orderDate;

    @Column(name = "fulfillment_date")
    @Schema(description = "Date when the order was picked and packed", example = "2026-02-01", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate fulfillmentDate;

    @Column(name = "shipping_date")
    @Schema(description = "Date when the order was shipped", example = "2026-02-02", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate shippingDate;

    @Column(name = "delivery_date")
    @Schema(description = "Date when the order was delivered to customer", example = "2026-02-05", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate deliveryDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    @Schema(description = "Subtotal amount before tax and shipping", example = "1299.99", defaultValue = "0.00", minimum = "0")
    private BigDecimal subtotal = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    @Schema(description = "Sales tax amount calculated based on shipping location", example = "104.00", defaultValue = "0.00", minimum = "0")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "shipping_cost", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    @Schema(description = "Cost to ship the order to customer", example = "15.00", defaultValue = "0.00", minimum = "0")
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    @Schema(description = "Total order amount (subtotal + tax + shipping)", example = "1418.99", defaultValue = "0.00", minimum = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Additional order notes or special delivery instructions", example = "Please ring doorbell, leave at front door if no answer")
    private String notes;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "List of line items (products) in this sales order")
    private List<SalesOrderItem> items = new ArrayList<>();

    // Helper methods
    public void addItem(SalesOrderItem item) {
        items.add(item);
        item.setSalesOrder(this);
    }

    public void removeItem(SalesOrderItem item) {
        items.remove(item);
        item.setSalesOrder(null);
    }

    public void calculateTotals() {
        this.subtotal = items.stream()
                .map(SalesOrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalAmount = this.subtotal
                .add(this.taxAmount)
                .add(this.shippingCost);
    }
}