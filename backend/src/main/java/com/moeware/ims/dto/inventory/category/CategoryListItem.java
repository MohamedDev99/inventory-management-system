package com.moeware.ims.dto.inventory.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for simple category list item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Simple category information for lists")
public class CategoryListItem {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Electronics")
    private String name;

    @Schema(description = "Category code", example = "ELEC")
    private String code;

    @Schema(description = "Full category path", example = "Electronics > Computers > Laptops")
    private String fullPath;

    @Schema(description = "Hierarchy level", example = "0")
    private Integer level;

    @Schema(description = "Number of products", example = "25")
    private Long productCount;

    @Schema(description = "Has child categories", example = "true")
    private Boolean hasChildren;
}