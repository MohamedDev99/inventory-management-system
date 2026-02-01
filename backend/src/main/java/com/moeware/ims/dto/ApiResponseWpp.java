package com.moeware.ims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Generic API response wrapper
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Generic API response wrapper")
public class ApiResponseWpp<T> {

    @Schema(description = "Operation success status", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "Operation successful")
    private String message;

    @Schema(description = "Response data (type varies by endpoint)")
    private T data;

    @Schema(description = "Response timestamp", example = "2026-01-28T10:30:00")
    private LocalDateTime timestamp;

    public static <T> ApiResponseWpp<T> success(T data, String message) {
        return ApiResponseWpp.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponseWpp<T> success(T data) {
        return success(data, "Operation successful");
    }

    public static <T> ApiResponseWpp<T> error(String message) {
        return ApiResponseWpp.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

}