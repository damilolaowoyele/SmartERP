package org.smarterp.inventory.Repository;

import org.smarterp.inventory.entity.ItemBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemBatchRepository extends JpaRepository<ItemBatch, UUID> {
    List<ItemBatch> findByItemId(UUID itemId);
    List<ItemBatch> findByWarehouseId(UUID warehouseId);
    List<ItemBatch> findByExpiryDateBefore(LocalDate date);
    List<ItemBatch> findAllByItemId(UUID itemId);
    Optional<ItemBatch> findByItemIdAndBatchNumber(UUID itemId, String batchNumber);
    boolean existsByItemIdAndBatchNumber(UUID itemId, String batchNumber);
}




