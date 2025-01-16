package org.smarterp.inventory.mapper;

import org.smarterp.inventory.dto.category.CategoryCreateRequest;
import org.smarterp.inventory.dto.category.CategoryDTO;
import org.smarterp.inventory.dto.category.CategoryUpdateRequest;
import org.smarterp.inventory.entity.Category;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        if (category == null) return null;

        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setParentCategoryId(category.getParentCategory() != null ?
                category.getParentCategory().getCategoryId() : null);

        dto.setChildCategoryIds(category.getChildCategories().stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toSet()));

        dto.setItemIds(category.getItems().stream()
                .map(item -> item.getItemId())
                .collect(Collectors.toSet()));

        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());

        return dto;
    }

    public Category toEntity(CategoryCreateRequest request) {
        if (request == null) return null;

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return category;
    }

    public void updateEntity(Category category, CategoryUpdateRequest request) {
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
    }
}
