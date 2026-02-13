package com.moeware.ims.dto.inventory.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new category
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for creating a new category")
public class CategoryCreateRequest {

    @Schema(description = "Category name", example = "Electronics", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @Schema(description = "Unique category code", example = "ELEC", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Category code is required")
    @Size(max = 50, message = "Category code must not exceed 50 characters")
    private String code;

    @Schema(description = "Detailed description of the category", example = "All electronic products")
    private String description;

    @Schema(description = "Parent category ID (null for root category)", example = "1")
    private Long parentCategoryId;
}