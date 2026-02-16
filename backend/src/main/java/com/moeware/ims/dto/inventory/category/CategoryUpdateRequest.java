package com.moeware.ims.dto.inventory.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing category
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for updating an existing category")
public class CategoryUpdateRequest {

    @Schema(description = "Category name", example = "Electronics")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @Schema(description = "Detailed description of the category")
    private String description;

    @Schema(description = "Parent category ID (null for root category)", example = "1")
    private Long parentCategoryId;
}