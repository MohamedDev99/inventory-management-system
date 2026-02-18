package com.moeware.ims.exception.inventory.product;

/**
 * Exception thrown when a requested product is not found
 * More specific than generic ResourceNotFoundException for product operations
 */
public class ProductNotFoundException extends RuntimeException {

    private final Long productId;
    private final String sku;

    public ProductNotFoundException(Long productId) {
        super("Product not found with id: " + productId);
        this.productId = productId;
        this.sku = null;
    }

    public ProductNotFoundException(String sku) {
        super("Product not found with SKU: " + sku);
        this.productId = null;
        this.sku = sku;
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.productId = null;
        this.sku = null;
    }

    public Long getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }
}