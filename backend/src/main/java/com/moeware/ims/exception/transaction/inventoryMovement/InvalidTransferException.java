package com.moeware.ims.exception.transaction.inventoryMovement;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an inventory transfer violates a business rule that does not have
 * a more specific typed exception.
 *
 * <p>
 * Prefer a typed exception where one exists:
 * <ul>
 * <li>{@link com.moeware.ims.exception.transaction.inventoryMovement.InvalidInventoryTransferException}
 * — same-source-and-destination transfer (carries {@code fromWarehouseId} /
 * {@code toWarehouseId} in the response)</li>
 * <li>{@link com.moeware.ims.exception.transaction.stockAdjustment.InsufficientStockException}
 * — not enough stock to fulfil the transfer quantity</li>
 * </ul>
 *
 * <p>
 * Use this class only for transfer violations that do not yet have a
 * dedicated typed exception (e.g. unauthorised transfer, invalid quantity
 * format, business-rule edge cases).
 *
 * @author MoeWare Team
 */
public class InvalidTransferException extends BaseAppException {

    public InvalidTransferException(String message) {
        super(message);
    }

    public InvalidTransferException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorTitle() {
        return "Invalid Transfer";
    }
}