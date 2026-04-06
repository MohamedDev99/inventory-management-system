package com.moeware.ims.exception.inventory.product;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a product cannot be found by ID, SKU, or barcode.
 *
 * Uses a {@link LookupField} enum instead of a raw string field name
 * to eliminate typo risk and make call sites self-documenting.
 *
 * @author MoeWare Team
 */
public class ProductNotFoundException extends BaseAppException {

    public enum LookupField {
        SKU, BARCODE
    }

    private final Long productId;
    private final String sku;
    private final String barcode;

    // ── Lookup by ID ──────────────────────────────────────────────────────────

    public ProductNotFoundException(Long productId) {
        super("Product not found with id: " + productId);
        this.productId = productId;
        this.sku = null;
        this.barcode = null;
    }

    // ── Lookup by SKU or barcode ──────────────────────────────────────────────

    public ProductNotFoundException(LookupField field, String value) {
        super(String.format("Product not found with %s: '%s'", field.name().toLowerCase(), value));
        this.productId = null;
        this.sku = field == LookupField.SKU ? value : null;
        this.barcode = field == LookupField.BARCODE ? value : null;
    }

    // ── Cause-wrapping constructor ────────────────────────────────────────────

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.productId = null;
        this.sku = null;
        this.barcode = null;
    }

    // ── BaseAppException contract ─────────────────────────────────────────────

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Product Not Found";
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public Long getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getBarcode() {
        return barcode;
    }
}