package com.moeware.ims.dto.inventory.category;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for category response data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO containing category information")
public class CategoryResponse {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Electronics")
    private String name;

    @Schema(description = "Category code", example = "ELEC")
    private String code;

    @Schema(description = "Category description")
    private String description;

    @Schema(description = "Parent category information")
    private CategorySummary parentCategory;

    @Schema(description = "Hierarchy level (0 for root)", example = "0")
    private Integer level;

    @Schema(description = "Full category path", example = "Electronics > Computers > Laptops")
    private String fullPath;

    @Schema(description = "Number of direct products in this category", example = "25")
    private Long productCount;

    @Schema(description = "Number of child categories", example = "5")
    private Long childCategoryCount;

    @Schema(description = "Whether this is a root category", example = "true")
    private Boolean isRootCategory;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    /**
     * Nested DTO for category summary (used for parent reference)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Summary of category information")
    public static class CategorySummary {
        @Schema(description = "Category ID", example = "1")
        private Long id;

        @Schema(description = "Category name", example = "Electronics")
        private String name;

        @Schema(description = "Category code", example = "ELEC")
        private String code;

        @Schema(description = "Hierarchy level", example = "0")
        private Integer level;
    }
}
