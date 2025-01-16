package org.smarterp.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.dto.movement.InventoryMovementCreateRequest;
import org.smarterp.inventory.dto.movement.InventoryMovementDTO;
import org.smarterp.inventory.service.InventoryMovementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory-movements")
@RequiredArgsConstructor
public class InventoryMovementController {

    private final InventoryMovementService inventoryMovementService;

    @PostMapping
    public ResponseEntity<InventoryMovementDTO> createMovement(
            @Valid @RequestBody InventoryMovementCreateRequest request,
            @RequestParam UUID userId) {
        InventoryMovementDTO movementDTO = inventoryMovementService.createMovement(request, userId);
        return new ResponseEntity<>(movementDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{movementId}")
    public ResponseEntity<InventoryMovementDTO> getMovementById(@PathVariable UUID movementId) {
        InventoryMovementDTO movementDTO = inventoryMovementService.getMovementById(movementId);
        return ResponseEntity.ok(movementDTO);
    }

    @GetMapping
    public ResponseEntity<List<InventoryMovementDTO>> getAllMovements() {
        List<InventoryMovementDTO> movements = inventoryMovementService.getAllMovements();
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/item-batch/{itemBatchId}")
    public ResponseEntity<List<InventoryMovementDTO>> getMovementsByItemBatch(@PathVariable UUID itemBatchId) {
        List<InventoryMovementDTO> movements = inventoryMovementService.getMovementsByItemBatch(itemBatchId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryMovementDTO>> getMovementsByWarehouse(
            @PathVariable UUID warehouseId,
            @RequestParam boolean isSource) {
        List<InventoryMovementDTO> movements = inventoryMovementService.getMovementsByWarehouse(warehouseId, isSource);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<InventoryMovementDTO>> getMovementsByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<InventoryMovementDTO> movements = inventoryMovementService.getMovementsByDateRange(start, end);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InventoryMovementDTO>> getMovementsByUser(@PathVariable UUID userId) {
        List<InventoryMovementDTO> movements = inventoryMovementService.getMovementsByUser(userId);
        return ResponseEntity.ok(movements);
    }
}