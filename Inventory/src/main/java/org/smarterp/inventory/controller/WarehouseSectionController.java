package org.smarterp.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.dto.section.WarehouseSectionCreateRequest;
import org.smarterp.inventory.dto.section.WarehouseSectionDTO;
import org.smarterp.inventory.dto.section.WarehouseSectionUpdateRequest;
import org.smarterp.inventory.service.WarehouseSectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouse-sections")
@RequiredArgsConstructor
public class WarehouseSectionController {

    private final WarehouseSectionService warehouseSectionService;

    @PostMapping
    public ResponseEntity<WarehouseSectionDTO> createWarehouseSection(@Valid @RequestBody WarehouseSectionCreateRequest request) {
        WarehouseSectionDTO sectionDTO = warehouseSectionService.createWarehouseSection(request);
        return new ResponseEntity<>(sectionDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<WarehouseSectionDTO> updateWarehouseSection(
            @PathVariable UUID sectionId,
            @Valid @RequestBody WarehouseSectionUpdateRequest request) {
        WarehouseSectionDTO sectionDTO = warehouseSectionService.updateWarehouseSection(sectionId, request);
        return ResponseEntity.ok(sectionDTO);
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<WarehouseSectionDTO> getWarehouseSectionById(@PathVariable UUID sectionId) {
        WarehouseSectionDTO sectionDTO = warehouseSectionService.getWarehouseSectionById(sectionId);
        return ResponseEntity.ok(sectionDTO);
    }

    @GetMapping
    public ResponseEntity<List<WarehouseSectionDTO>> getAllWarehouseSections() {
        List<WarehouseSectionDTO> sections = warehouseSectionService.getAllWarehouseSections();
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<WarehouseSectionDTO>> getWarehouseSectionsByWarehouseId(@PathVariable UUID warehouseId) {
        List<WarehouseSectionDTO> sections = warehouseSectionService.getWarehouseSectionsByWarehouseId(warehouseId);
        return ResponseEntity.ok(sections);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteWarehouseSection(@PathVariable UUID sectionId) {
        warehouseSectionService.deleteWarehouseSection(sectionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sectionId}/has-enough-capacity")
    public ResponseEntity<Boolean> hasEnoughCapacity(@PathVariable UUID sectionId, @RequestParam int quantity) {
        boolean hasCapacity = warehouseSectionService.hasEnoughCapacity(sectionId, quantity);
        return ResponseEntity.ok(hasCapacity);
    }

    @GetMapping("/{sectionId}/total-stock-count")
    public ResponseEntity<Integer> getTotalStockCount(@PathVariable UUID sectionId) {
        int totalStockCount = warehouseSectionService.getTotalStockCount(sectionId);
        return ResponseEntity.ok(totalStockCount);
    }

    @PostMapping("/{sectionId}/add-stock")
    public ResponseEntity<Void> addStock(@PathVariable UUID sectionId, @RequestParam int quantity) {
        warehouseSectionService.addStock(sectionId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sectionId}/remove-stock")
    public ResponseEntity<Void> removeStock(@PathVariable UUID sectionId, @RequestParam int quantity) {
        warehouseSectionService.removeStock(sectionId, quantity);
        return ResponseEntity.ok().build();
    }
}
