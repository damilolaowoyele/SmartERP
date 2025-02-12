package org.smarterp.inventory.service;


import org.smarterp.inventory.dto.movement.InventoryMovementCreateRequest;
import org.smarterp.inventory.dto.movement.InventoryMovementDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface InventoryMovementService {
    InventoryMovementDTO createMovement(InventoryMovementCreateRequest request, UUID userId);
    InventoryMovementDTO getMovementById(UUID movementId);
    List<InventoryMovementDTO> getAllMovements();
    List<InventoryMovementDTO> getMovementsByItemBatch(UUID itemBatchId);
    List<InventoryMovementDTO> getMovementsByWarehouse(UUID warehouseId, boolean isSource);
    List<InventoryMovementDTO> getMovementsByDateRange(LocalDateTime start, LocalDateTime end);
    List<InventoryMovementDTO> getMovementsByUser(UUID userId);
}
