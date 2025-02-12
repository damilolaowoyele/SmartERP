package org.smarterp.inventory.service;


import org.smarterp.inventory.dto.category.CategoryCreateRequest;
import org.smarterp.inventory.dto.category.CategoryDTO;
import org.smarterp.inventory.dto.category.CategoryUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryDTO createCategory(CategoryCreateRequest request);
    CategoryDTO updateCategory(UUID categoryId, CategoryUpdateRequest request);
    CategoryDTO getCategoryById(UUID categoryId);
    List<CategoryDTO> getAllCategories();
    List<CategoryDTO> getRootCategories();
    List<CategoryDTO> getSubcategories(UUID parentId);
    void deleteCategory(UUID categoryId);
    CategoryDTO addItemToCategory(UUID categoryId, UUID itemId);
    CategoryDTO removeItemFromCategory(UUID categoryId, UUID itemId);
}
