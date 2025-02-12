package org.smarterp.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.dto.batch.ItemBatchCreateRequest;
import org.smarterp.inventory.dto.batch.ItemBatchDTO;
import org.smarterp.inventory.dto.batch.ItemBatchUpdateRequest;
import org.smarterp.inventory.service.ItemBatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/item-batches")
@RequiredArgsConstructor
public class ItemBatchController {

    private final ItemBatchService itemBatchService;

    @PostMapping
    public ResponseEntity<ItemBatchDTO> createBatch(@Valid @RequestBody ItemBatchCreateRequest request) {
        ItemBatchDTO batchDTO = itemBatchService.createBatch(request);
        return new ResponseEntity<>(batchDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{batchId}")
    public ResponseEntity<ItemBatchDTO> updateBatch(
            @PathVariable UUID batchId,
            @Valid @RequestBody ItemBatchUpdateRequest request) {
        ItemBatchDTO batchDTO = itemBatchService.updateBatch(batchId, request);
        return ResponseEntity.ok(batchDTO);
    }

    @GetMapping("/{batchId}")
    public ResponseEntity<ItemBatchDTO> getBatchById(@PathVariable UUID batchId) {
        ItemBatchDTO batchDTO = itemBatchService.getBatchById(batchId);
        return ResponseEntity.ok(batchDTO);
    }

    @GetMapping
    public ResponseEntity<List<ItemBatchDTO>> getAllBatches() {
        List<ItemBatchDTO> batches = itemBatchService.getAllBatches();
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ItemBatchDTO>> getBatchesByItem(@PathVariable UUID itemId) {
        List<ItemBatchDTO> batches = itemBatchService.getBatchesByItem(itemId);
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<ItemBatchDTO>> getBatchesByWarehouse(@PathVariable UUID warehouseId) {
        List<ItemBatchDTO> batches = itemBatchService.getBatchesByWarehouse(warehouseId);
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<ItemBatchDTO>> getExpiringBatches(@RequestParam LocalDate beforeDate) {
        List<ItemBatchDTO> batches = itemBatchService.getExpiringBatches(beforeDate);
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/available")
    public ResponseEntity<List<ItemBatchDTO>> getAvailableBatches() {
        List<ItemBatchDTO> batches = itemBatchService.getAvailableBatches();
        return ResponseEntity.ok(batches);
    }

    @DeleteMapping("/{batchId}")
    public ResponseEntity<Void> deleteBatch(@PathVariable UUID batchId) {
        itemBatchService.deleteBatch(batchId);
        return ResponseEntity.noContent().build();
    }
}
