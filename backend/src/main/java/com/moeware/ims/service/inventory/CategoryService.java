package com.moeware.ims.service.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.inventory.category.CategoryCreateRequest;
import com.moeware.ims.dto.inventory.category.CategoryListItem;
import com.moeware.ims.dto.inventory.category.CategoryResponse;
import com.moeware.ims.dto.inventory.category.CategoryTreeNode;
import com.moeware.ims.dto.inventory.category.CategoryUpdateRequest;
import com.moeware.ims.entity.inventory.Category;
import com.moeware.ims.repository.inventory.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for Category business logic and operations.
 * Handles CRUD operations for hierarchical category structure with parent-child
 * relationships.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Create a new category
     *
     * @param request category creation request
     * @return created category response
     */
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        log.info("Creating new category with code: {}", request.getCode());

        // Validate unique constraints
        if (categoryRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Category with code '" + request.getCode() + "' already exists");
        }

        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }

        // Validate and set parent category if provided
        Category parentCategory = null;
        int level = 0;

        if (request.getParentCategoryId() != null) {
            parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category with ID " +
                            request.getParentCategoryId() + " not found"));
            level = parentCategory.getLevel() + 1;
        }

        // Create category entity
        Category category = Category.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .parentCategory(parentCategory)
                .level(level)
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());

        return mapToResponse(savedCategory);
    }

    /**
     * Get category by ID
     *
     * @param id category ID
     * @return category response
     */
    public CategoryResponse getCategoryById(Long id) {
        log.debug("Fetching category with ID: {}", id);
        Category category = findCategoryById(id);
        return mapToResponse(category);
    }

    /**
     * Get category by code
     *
     * @param code category code
     * @return category response
     */
    public CategoryResponse getCategoryByCode(String code) {
        log.debug("Fetching category with code: {}", code);
        Category category = categoryRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Category with code '" + code + "' not found"));
        return mapToResponse(category);
    }

    /**
     * Get all categories with pagination and filters
     *
     * @param pageable pagination information
     * @param search   search term
     * @param parentId parent category ID filter
     * @param level    hierarchy level filter
     * @return page of categories
     */
    public Page<CategoryResponse> getAllCategories(Pageable pageable, String search, Long parentId, Integer level) {
        log.debug("Fetching categories with filters - search: {}, parentId: {}, level: {}",
                search, parentId, level);

        Specification<Category> spec = (root, query, cb) -> cb.conjunction();

        // Apply search filter
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("code")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")));
        }

        // Apply parent filter
        if (parentId != null) {
            spec = spec.and((root, query, cb) -> {
                if (parentId == 0) {
                    // Root categories (no parent)
                    return cb.isNull(root.get("parentCategory"));
                } else {
                    return cb.equal(root.get("parentCategory").get("id"), parentId);
                }
            });
        }

        // Apply level filter
        if (level != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("level"), level));
        }

        Page<Category> categories = categoryRepository.findAll(spec, pageable);
        return categories.map(this::mapToResponse);
    }

    /**
     * Get all root categories (categories without parent)
     *
     * @param pageable pagination information
     * @return page of root categories
     */
    public Page<CategoryResponse> getRootCategories(Pageable pageable) {
        log.debug("Fetching root categories");
        Page<Category> categories = categoryRepository.findRootCategories(pageable);
        return categories.map(this::mapToResponse);
    }

    /**
     * Get child categories of a parent
     *
     * @param parentId parent category ID
     * @param pageable pagination information
     * @return page of child categories
     */
    public Page<CategoryResponse> getChildCategories(Long parentId, Pageable pageable) {
        log.debug("Fetching child categories for parent ID: {}", parentId);

        // Validate parent exists
        if (!categoryRepository.existsById(parentId)) {
            throw new EntityNotFoundException("Category with ID " + parentId + " not found");
        }

        Page<Category> categories = categoryRepository.findByParentCategoryId(parentId, pageable);
        return categories.map(this::mapToResponse);
    }

    /**
     * Get complete category tree
     *
     * @return list of root categories with nested children
     */
    public List<CategoryTreeNode> getCategoryTree() {
        log.debug("Building complete category tree");

        // Get all categories ordered by hierarchy
        List<Category> allCategories = categoryRepository.findAllOrderedByHierarchy();

        // Build tree structure
        return buildTree(allCategories);
    }

    /**
     * Get category tree starting from a specific category
     *
     * @param categoryId root category ID for the subtree
     * @return category tree node with children
     */
    public CategoryTreeNode getCategorySubtree(Long categoryId) {
        log.debug("Building category subtree for ID: {}", categoryId);

        Category category = findCategoryById(categoryId);
        return buildTreeNode(category);
    }

    /**
     * Get all products in a category (including subcategories)
     *
     * @param categoryId category ID
     * @return count of products
     */
    public long getProductCountInCategoryTree(Long categoryId) {
        log.debug("Counting products in category tree for ID: {}", categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category with ID " + categoryId + " not found");
        }

        return categoryRepository.countProductsInCategoryTree(categoryId);
    }

    /**
     * Update category
     *
     * @param id      category ID
     * @param request update request
     * @return updated category response
     */
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        log.info("Updating category with ID: {}", id);

        Category category = findCategoryById(id);

        // Update fields if provided
        if (request.getName() != null) {
            // Check if name is being changed and if new name already exists
            if (!request.getName().equals(category.getName()) &&
                    categoryRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
            }
            category.setName(request.getName());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        if (request.getParentCategoryId() != null) {
            // Prevent setting itself as parent
            if (request.getParentCategoryId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }

            // Get new parent category
            Category newParent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category with ID " +
                            request.getParentCategoryId() + " not found"));

            // Prevent circular references (check if new parent is a descendant)
            if (isDescendant(category, newParent)) {
                throw new IllegalArgumentException(
                        "Cannot set a descendant category as parent - would create circular reference");
            }

            category.setParentCategory(newParent);
            category.setLevel(newParent.getLevel() + 1);

            // Update levels of all descendants
            updateDescendantLevels(category);
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully with ID: {}", updatedCategory.getId());

        return mapToResponse(updatedCategory);
    }

    /**
     * Delete category
     *
     * @param id category ID
     */
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with ID: {}", id);

        // Category category = findCategoryById(id);

        // Check if category has products
        long productCount = categoryRepository.countProductsInCategory(id);
        if (productCount > 0) {
            throw new IllegalStateException("Cannot delete category with existing products. " +
                    "Please reassign or remove all products first.");
        }

        // Check if category has child categories
        if (categoryRepository.hasChildCategories(id)) {
            throw new IllegalStateException("Cannot delete category with child categories. " +
                    "Please delete or reassign child categories first.");
        }

        categoryRepository.deleteById(id);
        log.info("Category deleted successfully with ID: {}", id);
    }

    /**
     * Get categories as a flat list for selection/dropdown
     *
     * @return list of category list items
     */
    public List<CategoryListItem> getCategoryList() {
        log.debug("Fetching category list");

        List<Category> categories = categoryRepository.findAllOrderedByHierarchy();

        return categories.stream()
                .map(this::mapToListItem)
                .collect(Collectors.toList());
    }

    /**
     * Get empty categories (categories without products)
     *
     * @return list of empty categories
     */
    public List<CategoryResponse> getEmptyCategories() {
        log.debug("Fetching empty categories");

        List<Category> categories = categoryRepository.findEmptyCategories();
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper methods

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + id + " not found"));
    }

    private List<CategoryTreeNode> buildTree(List<Category> categories) {
        List<CategoryTreeNode> roots = new ArrayList<>();

        for (Category category : categories) {
            if (category.isRootCategory()) {
                roots.add(buildTreeNode(category));
            }
        }

        return roots;
    }

    private CategoryTreeNode buildTreeNode(Category category) {
        List<CategoryTreeNode> children = category.getChildCategories().stream()
                .map(this::buildTreeNode)
                .collect(Collectors.toList());

        long productCount = categoryRepository.countProductsInCategory(category.getId());

        return CategoryTreeNode.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .description(category.getDescription())
                .level(category.getLevel())
                .productCount(productCount)
                .children(children)
                .build();
    }

    private boolean isDescendant(Category category, Category potentialDescendant) {
        Category current = potentialDescendant.getParentCategory();

        while (current != null) {
            if (current.getId().equals(category.getId())) {
                return true;
            }
            current = current.getParentCategory();
        }

        return false;
    }

    private void updateDescendantLevels(Category category) {
        for (Category child : category.getChildCategories()) {
            child.setLevel(category.getLevel() + 1);
            categoryRepository.save(child);
            updateDescendantLevels(child);
        }
    }

    private CategoryResponse mapToResponse(Category category) {
        long productCount = categoryRepository.countProductsInCategory(category.getId());
        long childCount = categoryRepository.countByParentCategoryId(category.getId());

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .description(category.getDescription())
                .parentCategory(mapToCategorySummary(category.getParentCategory()))
                .level(category.getLevel())
                .fullPath(category.getFullPath())
                .productCount(productCount)
                .childCategoryCount(childCount)
                .isRootCategory(category.isRootCategory())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    private CategoryResponse.CategorySummary mapToCategorySummary(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponse.CategorySummary.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .level(category.getLevel())
                .build();
    }

    private CategoryListItem mapToListItem(Category category) {
        long productCount = categoryRepository.countProductsInCategory(category.getId());
        boolean hasChildren = categoryRepository.hasChildCategories(category.getId());

        return CategoryListItem.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .fullPath(category.getFullPath())
                .level(category.getLevel())
                .productCount(productCount)
                .hasChildren(hasChildren)
                .build();
    }
}