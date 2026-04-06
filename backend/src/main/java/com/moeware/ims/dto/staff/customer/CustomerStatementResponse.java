package com.moeware.ims.dto.staff.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Customer account statement for a given date range")
public class CustomerStatementResponse {

    private Long customerId;
    private String customerName;
    private Period statementPeriod;
    private BigDecimal openingBalance;
    private List<Transaction> transactions;
    private BigDecimal closingBalance;
    private BigDecimal totalInvoiced;
    private BigDecimal totalPaid;
    private BigDecimal creditLimit;
    private BigDecimal availableCredit;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Period {
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Transaction {
        private LocalDate date;
        private String type; // "INVOICE" | "PAYMENT"
        private String referenceNumber;
        private String description;
        private BigDecimal debit;
        private BigDecimal credit;
        private BigDecimal balance;
    }
}