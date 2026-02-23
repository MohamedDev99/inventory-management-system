package com.moeware.ims.exception.inventory.product;

/**
 * Exception thrown when a requested product is not found
 * More specific than generic ResourceNotFoundException for product operations
 */
public class ProductNotFoundException extends RuntimeException {

    private final Long productId;
    private final String sku;
    private final String barcode;

    public ProductNotFoundException(Long productId) {
        super("Product not found with id: " + productId);
        this.productId = productId;
        this.sku = null;
        this.barcode = null;
    }

    public ProductNotFoundException(String fieldName, String fieldValue) {
        super(String.format("Product not found with %s: '%s'", fieldName, fieldValue));
        if ("sku".equalsIgnoreCase(fieldName)) {
            this.sku = fieldValue;
            this.barcode = null;

        } else if ("barcode".equalsIgnoreCase(fieldName)) {
            this.barcode = fieldValue;
            this.sku = null;
        } else {
            this.sku = null;
            this.barcode = null;
        }
        this.productId = null;
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.productId = null;
        this.sku = null;
        this.barcode = null;
    }

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