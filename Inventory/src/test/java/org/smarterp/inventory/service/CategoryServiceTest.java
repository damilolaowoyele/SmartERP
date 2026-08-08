package org.smarterp.inventory.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.smarterp.inventory.Repository.CategoryRepository;
import org.smarterp.inventory.dto.category.CategoryCreateRequest;
import org.smarterp.inventory.dto.category.CategoryDTO;
import org.smarterp.inventory.dto.category.CategoryUpdateRequest;
import org.smarterp.inventory.entity.Category;
import org.smarterp.inventory.exception.ResourceAlreadyExistsException;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.CategoryMapper;
import org.smarterp.inventory.service.impl.CategoryServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDTO categoryDTO;
    private CategoryCreateRequest createRequest;
    private CategoryUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        
        category = new Category();
        category.setCategoryId(id);
        category.setName("Test Category");
        category.setDescription("Test Description");

        categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(id);
        categoryDTO.setName("Test Category");
        categoryDTO.setDescription("Test Description");

        createRequest = new CategoryCreateRequest();
        createRequest.setName("Test Category");
        createRequest.setDescription("Test Description");

        updateRequest = new CategoryUpdateRequest();
        updateRequest.setName("Updated Category");
    }

    @Test
    void testCreateCategory_Success() {
        when(categoryRepository.existsByNameIgnoreCase(createRequest.getName())).thenReturn(false);
        when(categoryMapper.toEntity(createRequest)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.createCategory(createRequest);

        assertNotNull(result);
        assertEquals("Test Category", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testCreateCategory_AlreadyExists() {
        when(categoryRepository.existsByNameIgnoreCase(createRequest.getName())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> 
            categoryService.createCategory(createRequest)
        );
    }

    @Test
    void testGetCategoryById_Success() {
        UUID id = category.getCategoryId();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.getCategoryById(id);

        assertNotNull(result);
        assertEquals(id, result.getCategoryId());
    }

    @Test
    void testGetCategoryById_NotFound() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            categoryService.getCategoryById(id)
        );
    }

    @Test
    void testUpdateCategory_Success() {
        UUID id = category.getCategoryId();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDTO result = categoryService.updateCategory(id, updateRequest);

        assertNotNull(result);
        verify(categoryRepository).save(category);
    }

    @Test
    void testDeleteCategory_Success() {
        UUID id = category.getCategoryId();
        when(categoryRepository.existsById(id)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(id);

        categoryService.deleteCategory(id);

        verify(categoryRepository).deleteById(id);
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = Arrays.asList(category);
        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
