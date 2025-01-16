package org.smarterp.inventory.service;

import org.smarterp.inventory.dto.stock.StockCountCreateRequest;
import org.smarterp.inventory.dto.stock.StockCountDTO;

import java.util.List;
import java.util.UUID;

public interface StockCountService {
    StockCountDTO createStockCount(StockCountCreateRequest request);
    StockCountDTO getStockCountById(UUID countId);
    List<StockCountDTO> getAllStockCounts();
    List<StockCountDTO> getStockCountsByItem(UUID itemId);
    List<StockCountDTO> getStockCountsByWarehouse(UUID warehouseId);
    List<StockCountDTO> getStockCountsByWarehouseAndSection(UUID warehouseId, UUID sectionId);
    List<StockCountDTO> getAvailableStock();
    void deleteStockCount(UUID countId);
    int getTotalBatchQuantity(UUID itemId);
    int getTotalAggregatedQuantity(UUID itemId);
}
