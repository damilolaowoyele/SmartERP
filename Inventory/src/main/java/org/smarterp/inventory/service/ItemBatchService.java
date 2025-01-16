package org.smarterp.inventory.service;

import org.smarterp.inventory.dto.batch.ItemBatchUpdateRequest;
import org.smarterp.inventory.dto.batch.ItemBatchCreateRequest;
import org.smarterp.inventory.dto.batch.ItemBatchDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ItemBatchService {
    ItemBatchDTO createBatch(ItemBatchCreateRequest request);
    ItemBatchDTO updateBatch(UUID batchId, ItemBatchUpdateRequest request);
    ItemBatchDTO getBatchById(UUID batchId);
    List<ItemBatchDTO> getAllBatches();
    List<ItemBatchDTO> getBatchesByItem(UUID itemId);
    List<ItemBatchDTO> getBatchesByWarehouse(UUID warehouseId);
    List<ItemBatchDTO> getExpiringBatches(LocalDate beforeDate);
    List<ItemBatchDTO> getAvailableBatches();
    void deleteBatch(UUID batchId);
//    int getTotalBatchQuantity(UUID itemId);
//    int getTotalAggregatedQuantity(UUID itemId);
}


