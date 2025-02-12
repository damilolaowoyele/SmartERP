package org.smarterp.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.dto.item.ItemCreateRequest;
import org.smarterp.inventory.dto.item.ItemDTO;
import org.smarterp.inventory.dto.item.ItemUpdateRequest;
import org.smarterp.inventory.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody ItemCreateRequest request) {
        ItemDTO itemDTO = itemService.createItem(request);
        return new ResponseEntity<>(itemDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemDTO> updateItem(
            @PathVariable UUID itemId,
            @Valid @RequestBody ItemUpdateRequest request) {
        ItemDTO itemDTO = itemService.updateItem(itemId, request);
        return ResponseEntity.ok(itemDTO);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable UUID itemId) {
        ItemDTO itemDTO = itemService.getItemById(itemId);
        return ResponseEntity.ok(itemDTO);
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ItemDTO> getItemByBarcode(@PathVariable String barcode) {
        ItemDTO itemDTO = itemService.getItemByBarcode(barcode);
        return ResponseEntity.ok(itemDTO);
    }

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        List<ItemDTO> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> searchItemsByName(@RequestParam String name) {
        List<ItemDTO> items = itemService.searchItemsByName(name);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ItemDTO>> getItemsByCategory(@PathVariable UUID categoryId) {
        List<ItemDTO> items = itemService.getItemsByCategory(categoryId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/temperature-sensitive")
    public ResponseEntity<List<ItemDTO>> getTemperatureSensitiveItems() {
        List<ItemDTO> items = itemService.getTemperatureSensitiveItems();
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
