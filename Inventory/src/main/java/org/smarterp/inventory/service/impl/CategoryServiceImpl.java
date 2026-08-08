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
                    .orElseThrow(() -> ResourceNotFoundException.forField("Category", "categoryId", request.getParentCategoryId()));
            category.setParentCategory(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    public CategoryDTO updateCategory(UUID categoryId, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.forField("Category", "categoryId", categoryId));

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
                throw ResourceAlreadyExistsException.forField("Category", "name", request.getName());
            }
        }

        categoryMapper.updateEntity(category, request);

        if (request.getParentCategoryId() != null) {
            if (!request.getParentCategoryId().equals(
                    category.getParentCategory() != null ? category.getParentCategory().getCategoryId() : null)) {
                Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                        .orElseThrow(() -> ResourceNotFoundException.forField("Category", "categoryId", request.getParentCategoryId()));
                category.setParentCategory(parentCategory);
            }
        } else if (request.getParentCategoryId() == null && category.getParentCategory() != null) {
            category.setParentCategory(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.forField("Category", "categoryId", categoryId));
        return categoryMapper.toDTO(category);
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
        return categoryRepository.findByParentCategory_CategoryId(parentId).stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw ResourceNotFoundException.forField("Category", "categoryId", categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDTO addItemToCategory(UUID categoryId, UUID itemId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.forField("Category", "categoryId", categoryId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> ResourceNotFoundException.forField("Category", "categoryId", itemId));

        category.getItems().add(item);
        item.getCategories().add(category);

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(updatedCategory);
    }

    @Override
    public CategoryDTO removeItemFromCategory(UUID categoryId, UUID itemId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.forField("Category", "categoryId", categoryId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> ResourceNotFoundException.forField("Category", "categoryId", itemId));

        category.getItems().remove(item);
        item.getCategories().remove(category);

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(updatedCategory);
    }
}
