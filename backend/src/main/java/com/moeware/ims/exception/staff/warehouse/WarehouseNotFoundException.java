package com.moeware.ims.exception.staff.warehouse;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a warehouse cannot be found by ID or code.
 *
 * @author MoeWare Team
 */
public class WarehouseNotFoundException extends BaseAppException {

    private final Long warehouseId;
    private final String code;

    public WarehouseNotFoundException(Long warehouseId) {
        super("Warehouse not found with id: " + warehouseId);
        this.warehouseId = warehouseId;
        this.code = null;
    }

    public WarehouseNotFoundException(String code) {
        super("Warehouse not found with code: " + code);
        this.warehouseId = null;
        this.code = code;
    }

    public WarehouseNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.warehouseId = null;
        this.code = null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Warehouse Not Found";
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public String getCode() {
        return code;
    }
}