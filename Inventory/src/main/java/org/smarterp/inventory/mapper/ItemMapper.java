package org.smarterp.inventory.mapper;

import org.smarterp.inventory.entity.Item;
import org.smarterp.inventory.dto.item.ItemCreateRequest;
import org.smarterp.inventory.dto.item.ItemDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public ItemDTO toDTO(Item item) {
        if (item == null) return null;

        ItemDTO dto = new ItemDTO();
        dto.setItemId(item.getItemId());
        dto.setProductId(item.getProduct() != null ? item.getProduct().getProductId() : null);
        dto.setItemName(item.getItemName());
        dto.setBarcode(item.getBarcode());
        dto.setDescription(item.getDescription());

        dto.setCategoryIds(item.getCategories().stream()
                .map(category -> category.getCategoryId())
                .collect(Collectors.toSet()));

        dto.setUnitOfMeasure(item.getUnitOfMeasure());
        dto.setWeight(item.getWeight());
        dto.setVolume(item.getVolume());
        dto.setTemperatureSensitive(item.getTemperatureSensitive());
        dto.setStorageConditions(item.getStorageConditions());
        dto.setHandlingInstructions(item.getHandlingInstructions());
        dto.setRemarks(item.getRemarks());

        dto.setItemBatchIds(item.getItemBatches().stream()
                .map(batch -> batch.getBatchId())
                .collect(Collectors.toSet()));

        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());

        return dto;
    }

    public Item toEntity(ItemCreateRequest request) {
        if (request == null) return null;

        Item item = new Item();
        item.setItemName(request.getItemName());
        item.setBarcode(request.getBarcode());
        item.setDescription(request.getDescription());
        item.setUnitOfMeasure(request.getUnitOfMeasure());
        item.setWeight(request.getWeight());
        item.setVolume(request.getVolume());
        item.setTemperatureSensitive(request.getTemperatureSensitive());
        item.setStorageConditions(request.getStorageConditions());
        item.setHandlingInstructions(request.getHandlingInstructions());
        item.setRemarks(request.getRemarks());

        return item;
    }
}
