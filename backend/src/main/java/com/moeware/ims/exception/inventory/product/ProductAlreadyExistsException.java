package com.moeware.ims.exception.inventory.product;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to create or update a product with a SKU or barcode
 * that already belongs to another product.
 *
 * Uses a {@link ConflictField} enum instead of a raw string field name
 * to eliminate typo risk and make call sites self-documenting.
 *
 * @author MoeWare Team
 */
public class ProductAlreadyExistsException extends BaseAppException {

    public enum ConflictField {
        SKU, BARCODE
    }

    private final String sku;
    private final String barcode;

    public ProductAlreadyExistsException(ConflictField field, String value) {
        super(String.format("Product already exists with %s: '%s'", field.name().toLowerCase(), value));
        this.sku = field == ConflictField.SKU ? value : null;
        this.barcode = field == ConflictField.BARCODE ? value : null;
    }

    // ── BaseAppException contract ─────────────────────────────────────────────

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Product Already Exists";
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public String getSku() {
        return sku;
    }

    public String getBarcode() {
        return barcode;
    }
}