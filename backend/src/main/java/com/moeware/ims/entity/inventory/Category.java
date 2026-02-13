package com.moeware.ims.entity.inventory;

import java.util.HashSet;
import java.util.Set;

import com.moeware.ims.entity.AuditableEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_categories_code", columnList = "code"),
        @Index(name = "idx_categories_parent", columnList = "parent_category_id"),
        @Index(name = "idx_categories_level_parent", columnList = "level, parent_category_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product category with hierarchical structure supporting parent-child relationships")
public class Category extends AuditableEntity {

    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Category name", example = "Electronics", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Schema(description = "Unique category code", example = "ELEC", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    @NotBlank(message = "Category code is required")
    @Size(max = 50, message = "Category code must not exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Schema(description = "Detailed description of the category", example = "All electronic products including computers and mobile devices")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Schema(description = "Parent category for hierarchical structure", implementation = Category.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @Schema(description = "Child categories under this category", accessMode = Schema.AccessMode.READ_ONLY)
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Category> childCategories = new HashSet<>();

    @Schema(description = "Tree depth level (0 for root categories)", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(nullable = false)
    @Builder.Default
    private Integer level = 0;

    @Schema(description = "Products belonging to this category", accessMode = Schema.AccessMode.READ_ONLY)
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Product> products = new HashSet<>();

    // Helper methods
    public void addChildCategory(Category child) {
        childCategories.add(child);
        child.setParentCategory(this);
        child.setLevel(this.level + 1);
    }

    public void removeChildCategory(Category child) {
        childCategories.remove(child);
        child.setParentCategory(null);
    }

    public boolean isRootCategory() {
        return parentCategory == null;
    }

    public String getFullPath() {
        if (isRootCategory()) {
            return name;
        }
        return parentCategory.getFullPath() + " > " + name;
    }
}