package com.moeware.ims.exception.inventory.product;

/**
 * Exception thrown when attempting to create a product that already exists
 */
public class ProductAlreadyExistsException extends RuntimeException {

    private final String sku;
    private final String barcode;

    public ProductAlreadyExistsException(String fieldName, String fieldValue) {
        super(String.format("Product already exists with %s: '%s'", fieldName, fieldValue));
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
    }

    public ProductAlreadyExistsException(String message) {
        super(message);
        this.sku = null;
        this.barcode = null;
    }

    public String getSku() {
        return sku;
    }

    public String getBarcode() {
        return barcode;
    }
}