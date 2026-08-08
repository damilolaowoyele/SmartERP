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

import java.util.List;
import java.util.Set;
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
                    .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", request.getProductId()));
            item.setProduct(product);
        }

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            Set<Category> categories = request.getCategoryIds().stream()
                    .map(categoryId -> categoryRepository.findById(categoryId)
                            .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", categoryId)))
                    .collect(Collectors.toSet());
            item.setCategories(categories);
        }

        Item savedItem = itemRepository.save(item);
        return itemMapper.toDTO(savedItem);
    }

    @Override
    public ItemDTO updateItem(UUID itemId, ItemUpdateRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", itemId));

        if (request.getBarcode() != null && !request.getBarcode().equals(item.getBarcode())) {
            if (itemRepository.existsByBarcodeIgnoreCase(request.getBarcode())) {
                throw ResourceAlreadyExistsException.forField("Item", "barcode", request.getBarcode());
            }
            item.setBarcode(request.getBarcode());
        }

        if (request.getItemName() != null) {
            item.setItemName(request.getItemName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getUnitOfMeasure() != null) {
            item.setUnitOfMeasure(request.getUnitOfMeasure());
        }
        if (request.getWeight() != null) {
            item.setWeight(request.getWeight());
        }
        if (request.getVolume() != null) {
            item.setVolume(request.getVolume());
        }
        if (request.getTemperatureSensitive() != null) {
            item.setTemperatureSensitive(request.getTemperatureSensitive());
        }
        if (request.getStorageConditions() != null) {
            item.setStorageConditions(request.getStorageConditions());
        }
        if (request.getHandlingInstructions() != null) {
            item.setHandlingInstructions(request.getHandlingInstructions());
        }
        if (request.getRemarks() != null) {
            item.setRemarks(request.getRemarks());
        }

        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", request.getProductId()));
            item.setProduct(product);
        }

        if (request.getCategoryIds() != null) {
            Set<Category> categories = request.getCategoryIds().stream()
                    .map(categoryId -> categoryRepository.findById(categoryId)
                            .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", categoryId)))
                    .collect(Collectors.toSet());
            item.setCategories(categories);
        }

        Item updatedItem = itemRepository.save(item);
        return itemMapper.toDTO(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDTO getItemById(UUID itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", itemId));
        return itemMapper.toDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDTO getItemByBarcode(String barcode) {
        Item item = itemRepository.findByBarcodeIgnoreCase(barcode)
                .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", barcode));
        return itemMapper.toDTO(item);
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
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", categoryId));
        return category.getItems().stream()
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
        if (!itemRepository.existsById(itemId)) {
            throw ResourceNotFoundException.forField("ItemBatch", "batchId", itemId);
        }
        itemRepository.deleteById(itemId);
    }
}
