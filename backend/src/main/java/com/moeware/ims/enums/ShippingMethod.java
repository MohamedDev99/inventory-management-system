package com.moeware.ims.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Shipping Method enumeration
 * Defines available shipping service levels
 */
@Schema(description = "Available shipping methods/service levels")
public enum ShippingMethod {
    @Schema(description = "Standard shipping (5-7 business days)")
    STANDARD,

    @Schema(description = "Express shipping (2-3 business days)")
    EXPRESS,

    @Schema(description = "Overnight shipping (next business day)")
    OVERNIGHT,

    @Schema(description = "Ground shipping (economy service)")
    GROUND
}