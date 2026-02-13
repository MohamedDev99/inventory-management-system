package com.moeware.ims.dto.inventory.category;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for hierarchical category tree structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Hierarchical category tree node")
public class CategoryTreeNode {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Electronics")
    private String name;

    @Schema(description = "Category code", example = "ELEC")
    private String code;

    @Schema(description = "Category description")
    private String description;

    @Schema(description = "Hierarchy level", example = "0")
    private Integer level;

    @Schema(description = "Number of products in this category", example = "25")
    private Long productCount;

    @Schema(description = "Child categories")
    private List<CategoryTreeNode> children;
}
