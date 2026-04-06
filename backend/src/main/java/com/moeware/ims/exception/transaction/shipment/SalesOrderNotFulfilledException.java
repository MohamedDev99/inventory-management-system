package com.moeware.ims.exception.transaction.shipment;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;
import com.moeware.ims.enums.transaction.SalesOrderStatus;

/**
 * Thrown when a shipment creation is attempted for a sales order that has not
 * yet been fulfilled. A sales order must be in FULFILLED status before a
 * shipment can be created.
 *
 * @author MoeWare Team
 */
public class SalesOrderNotFulfilledException extends BaseAppException {

        private final Long salesOrderId;
        private final String soNumber;
        private final SalesOrderStatus currentStatus;

        public SalesOrderNotFulfilledException(Long salesOrderId, String soNumber,
                        SalesOrderStatus currentStatus) {
                super(String.format(
                                "Cannot create shipment for sales order '%s' — order must be in FULFILLED status "
                                                + "before shipping, but current status is: %s.",
                                soNumber, currentStatus));
                this.salesOrderId = salesOrderId;
                this.soNumber = soNumber;
                this.currentStatus = currentStatus;
        }

        @Override
        public HttpStatus getHttpStatus() {
                return HttpStatus.CONFLICT;
        }

        @Override
        public String getErrorTitle() {
                return "Sales Order Not Fulfilled";
        }

        public Long getSalesOrderId() {
                return salesOrderId;
        }

        public String getSoNumber() {
                return soNumber;
        }

        public SalesOrderStatus getCurrentStatus() {
                return currentStatus;
        }
}