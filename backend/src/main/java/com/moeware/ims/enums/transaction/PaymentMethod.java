package com.moeware.ims.enums.transaction;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Payment Method enumeration
 * Defines supported payment methods
 */
@Schema(description = "Available payment methods")
public enum PaymentMethod {
    @Schema(description = "Cash payment")
    CASH,

    @Schema(description = "Credit card payment")
    CREDIT_CARD,

    @Schema(description = "Debit card payment")
    DEBIT_CARD,

    @Schema(description = "Bank wire transfer")
    BANK_TRANSFER,

    @Schema(description = "Check payment")
    CHECK,

    @Schema(description = "PayPal payment")
    PAYPAL
}
