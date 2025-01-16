package org.smarterp.inventory.Repository;

import org.smarterp.inventory.entity.StockCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockCountRepository extends JpaRepository<StockCount, UUID> {
    List<StockCount> findByItemId(UUID itemId);
    List<StockCount> findByWarehouseId(UUID warehouseId);
    List<StockCount> findByWarehouseIdAndSectionId(UUID warehouseId, UUID sectionId);
    List<StockCount> findAllByItemId(UUID itemId);
    Optional<StockCount> findByItemIdAndWarehouseIdAndSectionIdAndItemBatchId(
            UUID itemId, UUID warehouseId, UUID sectionId, UUID itemBatchId
    );
}

