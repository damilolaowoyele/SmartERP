package org.smarterp.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.Repository.CategoryRepository;
import org.smarterp.inventory.Repository.ItemRepository;
import org.smarterp.inventory.dto.category.CategoryCreateRequest;
import org.smarterp.inventory.dto.category.CategoryDTO;
import org.smarterp.inventory.dto.category.CategoryUpdateRequest;
import org.smarterp.inventory.entity.Category;
import org.smarterp.inventory.entity.Item;
import org.smarterp.inventory.exception.ResourceAlreadyExistsException;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.CategoryMapper;
import org.smarterp.inventory.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDTO createCategory(CategoryCreateRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw ResourceAlreadyExistsException.forField("Category", "name", request.getName());
        }

        Category category = categoryMapper.toEntity(request);

        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> ResourceNotFoundException.forId("Parent Category", request.getParentCategoryId()));
            category.setParentCategory(parentCategory);
        }

        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO updateCategory(UUID categoryId, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Category", categoryId));

        if (request.getName() != null &&
                !request.getName().equalsIgnoreCase(category.getName()) &&
                categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw ResourceAlreadyExistsException.forField("Category", "name", request.getName());
        }

        if (request.getName() != null) category.setName(request.getName());
        if (request.getDescription() != null) category.setDescription(request.getDescription());

        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> ResourceNotFoundException.forId("Parent Category", request.getParentCategoryId()));
            category.setParentCategory(parentCategory);
        }

        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(UUID categoryId) {
        return categoryMapper.toDTO(categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Category", categoryId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getRootCategories() {
        return categoryRepository.findByParentCategoryIsNull().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getSubcategories(UUID parentId) {
        return categoryRepository.findByParentCategoryId(parentId).stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Category", categoryId));

        categoryRepository.delete(category);
    }

    @Override
    public CategoryDTO addItemToCategory(UUID categoryId, UUID itemId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Category", categoryId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Item", itemId));

        category.getItems().add(item);
        item.getCategories().add(category);

        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO removeItemFromCategory(UUID categoryId, UUID itemId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Category", categoryId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Item", itemId));

        category.getItems().remove(item);
        item.getCategories().remove(category);

        return categoryMapper.toDTO(categoryRepository.save(category));
    }
}
