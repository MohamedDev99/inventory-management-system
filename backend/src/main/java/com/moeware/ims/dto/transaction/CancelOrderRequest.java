package com.moeware.ims.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for cancelling a purchase or sales order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for cancelling an order")
public class CancelOrderRequest {

    @NotBlank(message = "Cancellation reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    @Schema(description = "Reason for cancelling the order", example = "Supplier unable to fulfill order", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;
}