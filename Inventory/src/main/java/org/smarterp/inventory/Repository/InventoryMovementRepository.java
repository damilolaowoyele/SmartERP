package org.smarterp.inventory.Repository;

import org.smarterp.inventory.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    List<InventoryMovement> findByItemBatchId(UUID itemBatchId);
    List<InventoryMovement> findBySourceWarehouseId(UUID warehouseId);
    List<InventoryMovement> findByDestinationWarehouseId(UUID warehouseId);
    List<InventoryMovement> findByMovementDateBetween(LocalDateTime start, LocalDateTime end);
    List<InventoryMovement> findByCreatedBy(UUID userId);
}
