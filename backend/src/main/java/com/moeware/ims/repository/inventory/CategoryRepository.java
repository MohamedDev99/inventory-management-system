package com.moeware.ims.repository.inventory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.inventory.Category;

/**
 * Repository interface for Category entity operations.
 * Supports hierarchical category structure with parent-child relationships.
 * Extends JpaRepository for basic CRUD operations and JpaSpecificationExecutor
 * for dynamic queries.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

        /**
         * Find category by code
         *
         * @param code the category code
         * @return Optional containing the category if found
         */
        Optional<Category> findByCode(String code);

        /**
         * Find category by name
         *
         * @param name the category name
         * @return Optional containing the category if found
         */
        Optional<Category> findByName(String name);

        /**
         * Check if category with given code exists
         *
         * @param code the category code
         * @return true if exists, false otherwise
         */
        boolean existsByCode(String code);

        /**
         * Check if category with given name exists
         *
         * @param name the category name
         * @return true if exists, false otherwise
         */
        boolean existsByName(String name);

        /**
         * Find all root categories (categories without parent)
         *
         * @return List of root categories
         */
        @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL ORDER BY c.name")
        List<Category> findRootCategories();

        /**
         * Find all root categories with pagination
         *
         * @param pageable pagination information
         * @return Page of root categories
         */
        @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL")
        Page<Category> findRootCategories(Pageable pageable);

        /**
         * Find child categories of a parent
         *
         * @param parentId the parent category ID
         * @return List of child categories
         */
        @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId ORDER BY c.name")
        List<Category> findByParentCategoryId(@Param("parentId") Long parentId);

        /**
         * Find child categories of a parent with pagination
         *
         * @param parentId the parent category ID
         * @param pageable pagination information
         * @return Page of child categories
         */
        Page<Category> findByParentCategoryId(Long parentId, Pageable pageable);

        /**
         * Find categories by level in hierarchy
         *
         * @param level the hierarchy level (0 for root)
         * @return List of categories at specified level
         */
        List<Category> findByLevel(Integer level);

        /**
         * Find categories by level with pagination
         *
         * @param level    the hierarchy level
         * @param pageable pagination information
         * @return Page of categories at level
         */
        Page<Category> findByLevel(Integer level, Pageable pageable);

        /**
         * Search categories by name or code
         *
         * @param searchTerm the search term
         * @param pageable   pagination information
         * @return Page of matching categories
         */
        @Query("SELECT c FROM Category c WHERE " +
                        "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        Page<Category> searchCategories(@Param("searchTerm") String searchTerm, Pageable pageable);

        /**
         * Get all categories with their full hierarchy path
         * Ordered by level and name for tree structure
         *
         * @return List of all categories ordered hierarchically
         */
        @Query("SELECT c FROM Category c ORDER BY c.level, c.name")
        List<Category> findAllOrderedByHierarchy();

        /**
         * Count child categories of a parent
         *
         * @param parentId the parent category ID
         * @return count of child categories
         */
        long countByParentCategoryId(Long parentId);

        /**
         * Check if category has child categories
         *
         * @param categoryId the category ID
         * @return true if has children, false otherwise
         */
        @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
                        "FROM Category c WHERE c.parentCategory.id = :categoryId")
        boolean hasChildCategories(@Param("categoryId") Long categoryId);

        /**
         * Get all descendant categories (children, grandchildren, etc.)
         *
         * @param categoryId the parent category ID
         * @return List of all descendant categories
         */
        @Query(value = "WITH RECURSIVE category_tree AS (" +
                        "  SELECT id, name, code, parent_category_id, level " +
                        "  FROM categories WHERE id = :categoryId " +
                        "  UNION ALL " +
                        "  SELECT c.id, c.name, c.code, c.parent_category_id, c.level " +
                        "  FROM categories c " +
                        "  INNER JOIN category_tree ct ON c.parent_category_id = ct.id" +
                        ") SELECT * FROM category_tree WHERE id != :categoryId", nativeQuery = true)
        List<Category> findAllDescendants(@Param("categoryId") Long categoryId);

        /**
         * Get all ancestor categories (parent, grandparent, etc.)
         *
         * @param categoryId the child category ID
         * @return List of all ancestor categories
         */
        @Query(value = "WITH RECURSIVE category_tree AS (" +
                        "  SELECT id, name, code, parent_category_id, level " +
                        "  FROM categories WHERE id = :categoryId " +
                        "  UNION ALL " +
                        "  SELECT c.id, c.name, c.code, c.parent_category_id, c.level " +
                        "  FROM categories c " +
                        "  INNER JOIN category_tree ct ON c.id = ct.parent_category_id" +
                        ") SELECT * FROM category_tree WHERE id != :categoryId", nativeQuery = true)
        List<Category> findAllAncestors(@Param("categoryId") Long categoryId);

        /**
         * Count products in category (direct children only)
         *
         * @param categoryId the category ID
         * @return count of products
         */
        @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
        long countProductsInCategory(@Param("categoryId") Long categoryId);

        /**
         * Count products in category and all subcategories
         *
         * @param categoryId the category ID
         * @return total count of products
         */
        @Query(value = "WITH RECURSIVE category_tree AS (" +
                        "  SELECT id FROM categories WHERE id = :categoryId " +
                        "  UNION ALL " +
                        "  SELECT c.id FROM categories c " +
                        "  INNER JOIN category_tree ct ON c.parent_category_id = ct.id" +
                        ") SELECT COUNT(*) FROM products p " +
                        "WHERE p.category_id IN (SELECT id FROM category_tree)", nativeQuery = true)
        long countProductsInCategoryTree(@Param("categoryId") Long categoryId);

        /**
         * Find categories with products
         *
         * @return List of categories that have products
         */
        @Query("SELECT DISTINCT c FROM Category c JOIN c.products p")
        List<Category> findCategoriesWithProducts();

        /**
         * Find empty categories (no products)
         *
         * @return List of categories without products
         */
        @Query("SELECT c FROM Category c WHERE c.products IS EMPTY")
        List<Category> findEmptyCategories();

        /**
         * Get maximum level in category hierarchy
         *
         * @return maximum level value
         */
        @Query("SELECT COALESCE(MAX(c.level), 0) FROM Category c")
        Integer getMaxLevel();

        /**
         * Find categories by level range
         *
         * @param minLevel minimum level
         * @param maxLevel maximum level
         * @return List of categories in level range
         */
        @Query("SELECT c FROM Category c WHERE c.level BETWEEN :minLevel AND :maxLevel ORDER BY c.level, c.name")
        List<Category> findByLevelRange(@Param("minLevel") Integer minLevel, @Param("maxLevel") Integer maxLevel);
}