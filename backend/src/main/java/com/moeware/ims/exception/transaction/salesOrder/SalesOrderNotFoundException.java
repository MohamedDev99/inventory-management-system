package com.moeware.ims.exception.transaction.salesOrder;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a sales order cannot be found by ID or SO number.
 *
 * @author MoeWare Team
 */
public class SalesOrderNotFoundException extends BaseAppException {

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

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Sales Order Not Found";
    }

    public Long getSalesOrderId() {
        return salesOrderId;
    }

    public String getSoNumber() {
        return soNumber;
    }
}