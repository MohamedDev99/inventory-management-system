package com.moeware.ims.exception.transaction.invoice;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when an invoice is generated for a sales order that already has one.
 * Each sales order may have at most one invoice.
 *
 * @author MoeWare Team
 */
public class InvoiceAlreadyExistsException extends BaseAppException {

        private final Long salesOrderId;
        private final String soNumber;
        private final String existingInvoiceNumber;

        public InvoiceAlreadyExistsException(Long salesOrderId, String soNumber,
                        String existingInvoiceNumber) {
                super(String.format(
                                "Sales order '%s' already has an invoice (%s). "
                                                + "Cancel the existing invoice before generating a new one.",
                                soNumber, existingInvoiceNumber));
                this.salesOrderId = salesOrderId;
                this.soNumber = soNumber;
                this.existingInvoiceNumber = existingInvoiceNumber;
        }

        @Override
        public HttpStatus getHttpStatus() {
                return HttpStatus.CONFLICT;
        }

        @Override
        public String getErrorTitle() {
                return "Invoice Already Exists";
        }

        public Long getSalesOrderId() {
                return salesOrderId;
        }

        public String getSoNumber() {
                return soNumber;
        }

        public String getExistingInvoiceNumber() {
                return existingInvoiceNumber;
        }
}