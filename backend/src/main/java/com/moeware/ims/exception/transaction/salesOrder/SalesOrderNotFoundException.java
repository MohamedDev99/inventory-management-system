package com.moeware.ims.exception.transaction.salesOrder;

/**
 * Exception thrown when a sales order is not found
 */
public class SalesOrderNotFoundException extends RuntimeException {

    private final Long salesOrderId;
    private final String soNumber;

    public SalesOrderNotFoundException(Long id) {
        super("Sales order not found with id: " + id);
        this.salesOrderId = id;
        this.soNumber = null;
    }

    public SalesOrderNotFoundException(String soNumber) {
        super("Sales order not found with SO number: " + soNumber);
        this.salesOrderId = null;
        this.soNumber = soNumber;
    }

    public Long getSalesOrderId() {
        return salesOrderId;
    }

    public String getSoNumber() {
        return soNumber;
    }
}