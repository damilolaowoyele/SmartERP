package org.smarterp.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.Repository.CategoryRepository;
import org.smarterp.inventory.Repository.ItemRepository;
import org.smarterp.inventory.Repository.ProductRepository;
import org.smarterp.inventory.dto.item.ItemCreateRequest;
import org.smarterp.inventory.dto.item.ItemDTO;
import org.smarterp.inventory.dto.item.ItemUpdateRequest;
import org.smarterp.inventory.entity.Category;
import org.smarterp.inventory.entity.Item;
import org.smarterp.inventory.entity.Product;
import org.smarterp.inventory.exception.ResourceAlreadyExistsException;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.ItemMapper;
import org.smarterp.inventory.service.ItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDTO createItem(ItemCreateRequest request) {
        if (request.getBarcode() != null && itemRepository.existsByBarcodeIgnoreCase(request.getBarcode())) {
            throw ResourceAlreadyExistsException.forField("Item", "barcode", request.getBarcode());
        }

        Item item = itemMapper.toEntity(request);

        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> ResourceNotFoundException.forId("Product", request.getProductId()));
            item.setProduct(product);
        }

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            item.setCategories(new HashSet<>());
            for (UUID categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> ResourceNotFoundException.forId("Category", categoryId));
                item.getCategories().add(category);
            }
        }

        return itemMapper.toDTO(itemRepository.save(item));
    }

    @Override
    public ItemDTO updateItem(UUID itemId, ItemUpdateRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Item", itemId));

        if (request.getBarcode() != null &&
                !request.getBarcode().equalsIgnoreCase(item.getBarcode()) &&
                itemRepository.existsByBarcodeIgnoreCase(request.getBarcode())) {
            throw ResourceAlreadyExistsException.forField("Item", "barcode", request.getBarcode());
        }

        if (request.getItemName() != null) item.setItemName(request.getItemName());
        if (request.getBarcode() != null) item.setBarcode(request.getBarcode());
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getUnitOfMeasure() != null) item.setUnitOfMeasure(request.getUnitOfMeasure());
        if (request.getWeight() != null) item.setWeight(request.getWeight());
        if (request.getVolume() != null) item.setVolume(request.getVolume());
        if (request.getTemperatureSensitive() != null) item.setTemperatureSensitive(request.getTemperatureSensitive());
        if (request.getStorageConditions() != null) item.setStorageConditions(request.getStorageConditions());
        if (request.getHandlingInstructions() != null) item.setHandlingInstructions(request.getHandlingInstructions());
        if (request.getRemarks() != null) item.setRemarks(request.getRemarks());

        return itemMapper.toDTO(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDTO getItemById(UUID itemId) {
        return itemMapper.toDTO(itemRepository.findById(itemId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Item", itemId)));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDTO getItemByBarcode(String barcode) {
        return itemMapper.toDTO(itemRepository.findByBarcodeIgnoreCase(barcode)
                .orElseThrow(() -> ResourceNotFoundException.forField("Item", "barcode", barcode)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map(itemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> searchItemsByName(String name) {
        return itemRepository.findByItemNameContainingIgnoreCase(name).stream()
                .map(itemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getItemsByCategory(UUID categoryId) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getCategories().stream()
                        .anyMatch(category -> category.getCategoryId().equals(categoryId)))
                .map(itemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getTemperatureSensitiveItems() {
        return itemRepository.findByTemperatureSensitive(true).stream()
                .map(itemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(UUID itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Item", itemId));

        itemRepository.delete(item);
    }
}

