package org.smarterp.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.dto.stock.StockCountCreateRequest;
import org.smarterp.inventory.dto.stock.StockCountDTO;
import org.smarterp.inventory.service.StockCountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock-counts")
@RequiredArgsConstructor
public class StockCountController {

    private final StockCountService stockCountService;

    @PostMapping
    public ResponseEntity<StockCountDTO> createStockCount(@Valid @RequestBody StockCountCreateRequest request) {
        StockCountDTO stockCountDTO = stockCountService.createStockCount(request);
        return new ResponseEntity<>(stockCountDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{countId}")
    public ResponseEntity<StockCountDTO> getStockCountById(@PathVariable UUID countId) {
        StockCountDTO stockCountDTO = stockCountService.getStockCountById(countId);
        return ResponseEntity.ok(stockCountDTO);
    }

    @GetMapping
    public ResponseEntity<List<StockCountDTO>> getAllStockCounts() {
        List<StockCountDTO> stockCounts = stockCountService.getAllStockCounts();
        return ResponseEntity.ok(stockCounts);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<StockCountDTO>> getStockCountsByItem(@PathVariable UUID itemId) {
        List<StockCountDTO> stockCounts = stockCountService.getStockCountsByItem(itemId);
        return ResponseEntity.ok(stockCounts);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<StockCountDTO>> getStockCountsByWarehouse(@PathVariable UUID warehouseId) {
        List<StockCountDTO> stockCounts = stockCountService.getStockCountsByWarehouse(warehouseId);
        return ResponseEntity.ok(stockCounts);
    }

    @GetMapping("/warehouse/{warehouseId}/section/{sectionId}")
    public ResponseEntity<List<StockCountDTO>> getStockCountsByWarehouseAndSection(
            @PathVariable UUID warehouseId,
            @PathVariable UUID sectionId) {
        List<StockCountDTO> stockCounts = stockCountService.getStockCountsByWarehouseAndSection(warehouseId, sectionId);
        return ResponseEntity.ok(stockCounts);
    }

    @GetMapping("/available")
    public ResponseEntity<List<StockCountDTO>> getAvailableStock() {
        List<StockCountDTO> availableStock = stockCountService.getAvailableStock();
        return ResponseEntity.ok(availableStock);
    }

    @DeleteMapping("/{countId}")
    public ResponseEntity<Void> deleteStockCount(@PathVariable UUID countId) {
        stockCountService.deleteStockCount(countId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total-batch-quantity/{itemId}")
    public ResponseEntity<Integer> getTotalBatchQuantity(@PathVariable UUID itemId) {
        int totalBatchQuantity = stockCountService.getTotalBatchQuantity(itemId);
        return ResponseEntity.ok(totalBatchQuantity);
    }

    @GetMapping("/total-aggregated-quantity/{itemId}")
    public ResponseEntity<Integer> getTotalAggregatedQuantity(@PathVariable UUID itemId) {
        int totalAggregatedQuantity = stockCountService.getTotalAggregatedQuantity(itemId);
        return ResponseEntity.ok(totalAggregatedQuantity);
    }
}
