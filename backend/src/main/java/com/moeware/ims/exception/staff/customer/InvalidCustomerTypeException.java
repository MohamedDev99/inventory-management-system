package com.moeware.ims.exception.staff.customer;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an invalid customer type value is provided.
 * Valid values are: RETAIL, WHOLESALE, CORPORATE.
 *
 * @author MoeWare Team
 */
public class InvalidCustomerTypeException extends BaseAppException {

    private final String invalidType;

    public InvalidCustomerTypeException(String customerType) {
        super("Invalid customer type: '" + customerType + "'. Must be RETAIL, WHOLESALE, or CORPORATE.");
        this.invalidType = customerType;
    }

    public InvalidCustomerTypeException(String message, Throwable cause) {
        super(message, cause);
        this.invalidType = null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorTitle() {
        return "Invalid Customer Type";
    }

    public String getInvalidType() {
        return invalidType;
    }
}