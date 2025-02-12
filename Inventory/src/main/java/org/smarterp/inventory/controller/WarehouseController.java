package org.smarterp.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.dto.warehouse.WarehouseCreateRequest;
import org.smarterp.inventory.dto.warehouse.WarehouseDTO;
import org.smarterp.inventory.dto.warehouse.WarehouseUpdateRequest;
import org.smarterp.inventory.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseDTO> createWarehouse(@Valid @RequestBody WarehouseCreateRequest request) {
        WarehouseDTO warehouseDTO = warehouseService.createWarehouse(request);
        return new ResponseEntity<>(warehouseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{warehouseId}")
    public ResponseEntity<WarehouseDTO> updateWarehouse(
            @PathVariable UUID warehouseId,
            @Valid @RequestBody WarehouseUpdateRequest request) {
        WarehouseDTO warehouseDTO = warehouseService.updateWarehouse(warehouseId, request);
        return ResponseEntity.ok(warehouseDTO);
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<WarehouseDTO> getWarehouseById(@PathVariable UUID warehouseId) {
        WarehouseDTO warehouseDTO = warehouseService.getWarehouseById(warehouseId);
        return ResponseEntity.ok(warehouseDTO);
    }

    @GetMapping
    public ResponseEntity<List<WarehouseDTO>> getAllWarehouses() {
        List<WarehouseDTO> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(warehouses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<WarehouseDTO>> searchWarehousesByLocation(@RequestParam String location) {
        List<WarehouseDTO> warehouses = warehouseService.searchWarehousesByLocation(location);
        return ResponseEntity.ok(warehouses);
    }

    @DeleteMapping("/{warehouseId}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable UUID warehouseId) {
        warehouseService.deleteWarehouse(warehouseId);
        return ResponseEntity.noContent().build();
    }
}
