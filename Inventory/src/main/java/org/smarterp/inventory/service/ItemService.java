package org.smarterp.inventory.service;

import org.smarterp.inventory.dto.item.ItemUpdateRequest;
import org.smarterp.inventory.dto.item.ItemCreateRequest;
import org.smarterp.inventory.dto.item.ItemDTO;

import java.util.List;
import java.util.UUID;

public interface ItemService {
    ItemDTO createItem(ItemCreateRequest request);
    ItemDTO updateItem(UUID itemId, ItemUpdateRequest request);
    ItemDTO getItemById(UUID itemId);
    ItemDTO getItemByBarcode(String barcode);
    List<ItemDTO> getAllItems();
    List<ItemDTO> searchItemsByName(String name);
    List<ItemDTO> getItemsByCategory(UUID categoryId);
    List<ItemDTO> getTemperatureSensitiveItems();
    void deleteItem(UUID itemId);
}
