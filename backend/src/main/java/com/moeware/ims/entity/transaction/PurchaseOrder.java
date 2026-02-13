package com.moeware.ims.entity.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.moeware.ims.entity.User;
import com.moeware.ims.entity.VersionedEntity;
import com.moeware.ims.entity.inventory.Supplier;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.enums.transaction.PurchaseOrderStatus;

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
 * PurchaseOrder entity
 * Represents a purchase order placed by a supplier
 */
@Entity
@Table(name = "purchase_orders", uniqueConstraints = @UniqueConstraint(columnNames = "po_number"), indexes = {
        @Index(name = "idx_po_supplier", columnList = "supplier_id"),
        @Index(name = "idx_po_warehouse", columnList = "warehouse_id"),
        @Index(name = "idx_po_status", columnList = "status"),
        @Index(name = "idx_po_order_date", columnList = "order_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Represents a purchase order placed by a supplier")
public class PurchaseOrder extends VersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the purchase order", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "PO number is required")
    @Size(max = 50)
    @Pattern(regexp = "^PO-\\d{8}-\\d{4,}$", message = "PO number must match PO-YYYYMMDD-SEQ")
    @Column(name = "po_number", nullable = false, unique = true)
    @Schema(description = "Unique purchase order number", example = "PO-20260131-0001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String poNumber;

    @NotNull(message = "Supplier is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @Schema(description = "Supplier from whom products are being ordered", requiredMode = Schema.RequiredMode.REQUIRED)
    private Supplier supplier;

    @NotNull(message = "Warehouse is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @Schema(description = "Destination warehouse for received products", requiredMode = Schema.RequiredMode.REQUIRED)
    private Warehouse warehouse;

    @NotNull(message = "Creator is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    @Schema(description = "User who created this purchase order", requiredMode = Schema.RequiredMode.REQUIRED)
    private User createdByUser;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    @Schema(description = "Current status of the purchase order", example = "DRAFT", defaultValue = "DRAFT", allowableValues = {
            "DRAFT", "SUBMITTED", "APPROVED", "RECEIVED", "CANCELLED" }, requiredMode = Schema.RequiredMode.REQUIRED)
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;

    @NotNull(message = "Order date is required")
    @Column(name = "order_date", nullable = false)
    @Schema(description = "Date when the order was placed", example = "2026-01-31", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate orderDate;

    @Column(name = "expected_delivery_date")
    @Schema(description = "Expected delivery date from supplier", example = "2026-02-15")
    private LocalDate expectedDeliveryDate;

    @Column(name = "actual_delivery_date")
    @Schema(description = "Actual date when order was received", example = "2026-02-14", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate actualDeliveryDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    @Schema(description = "Subtotal amount before tax and discounts", example = "1000.00", defaultValue = "0.00")
    private BigDecimal subtotal = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    @Schema(description = "Tax amount", example = "100.00", defaultValue = "0.00")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    @Schema(description = "Discount amount", example = "50.00", defaultValue = "0.00")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    @Schema(description = "Total order amount (subtotal + tax - discount)", example = "1050.00", defaultValue = "0.00")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Additional notes or instructions for the purchase order", example = "Please deliver to loading dock B")
    private String notes;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "Line items in this purchase order")
    private List<PurchaseOrderItem> items = new ArrayList<>();

    // Helper methods
    public void addItem(PurchaseOrderItem item) {
        items.add(item);
        item.setPurchaseOrder(this);
    }

    public void removeItem(PurchaseOrderItem item) {
        items.remove(item);
        item.setPurchaseOrder(null);
    }

    public void calculateTotals() {
        this.subtotal = items.stream()
                .map(PurchaseOrderItem::getLineTotal)
                .map(total -> total != null ? total : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalAmount = this.subtotal
                .add(this.taxAmount)
                .subtract(this.discountAmount);
    }
}