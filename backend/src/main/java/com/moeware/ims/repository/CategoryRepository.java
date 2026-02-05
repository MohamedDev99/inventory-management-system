package com.moeware.ims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.product.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCode(String code);

    Optional<Category> findByName(String name);

    List<Category> findByParentCategoryIsNull(); // Root categories

    List<Category> findByParentCategory(Category parentCategory);

    List<Category> findByLevel(Integer level);

    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId")
    List<Category> findByParentCategoryId(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.childCategories WHERE c.id = :id")
    Optional<Category> findByIdWithChildren(@Param("id") Long id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(@Param("id") Long id);

    boolean existsByCode(String code);

    boolean existsByName(String name);
}