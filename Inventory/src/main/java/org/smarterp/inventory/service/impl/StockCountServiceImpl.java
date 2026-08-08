package org.smarterp.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smarterp.inventory.dto.stock.StockCountCreateRequest;
import org.smarterp.inventory.dto.stock.StockCountDTO;
import org.smarterp.inventory.entity.Item;
import org.smarterp.inventory.entity.ItemBatch;
import org.smarterp.inventory.entity.StockCount;
import org.smarterp.inventory.entity.Warehouse;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.StockCountMapper;
import org.smarterp.inventory.Repository.ItemBatchRepository;
import org.smarterp.inventory.Repository.ItemRepository;
import org.smarterp.inventory.Repository.StockCountRepository;
import org.smarterp.inventory.Repository.WarehouseRepository;
import org.smarterp.inventory.service.StockCountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockCountServiceImpl implements StockCountService {

    private final StockCountRepository stockCountRepository;
    private final ItemBatchRepository itemBatchRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockCountMapper stockCountMapper;

    @Override
    @Transactional
    public StockCountDTO createStockCount(StockCountCreateRequest request) {
        log.info("Creating stock count for item ID: {}", request.getItemId());

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> ResourceNotFoundException.forField("Item", "itemId", request.getItemId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> ResourceNotFoundException.forField("Warehouse", "warehouseId", request.getWarehouseId()));

        StockCount stockCount = stockCountMapper.toEntity(request);
        stockCount.setItem(item);
        stockCount.setWarehouse(warehouse);
        stockCount.setQuantity(request.getQuantity());

        StockCount savedStockCount = stockCountRepository.save(stockCount);
        log.info("Created stock count with ID: {}", savedStockCount.getCountId());

        return stockCountMapper.toDTO(savedStockCount);
    }

    @Override
    @Transactional(readOnly = true)
    public StockCountDTO getStockCountById(UUID countId) {
        log.debug("Fetching stock count with ID: {}", countId);

        StockCount stockCount = stockCountRepository.findById(countId)
                .orElseThrow(() -> ResourceNotFoundException.forField("StockCount", "countId", countId));

        return stockCountMapper.toDTO(stockCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountDTO> getAllStockCounts() {
        log.debug("Fetching all stock counts");

        return stockCountRepository.findAll().stream()
                .map(stockCountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountDTO> getStockCountsByItem(UUID itemId) {
        log.debug("Fetching stock counts for item ID: {}", itemId);

        return stockCountRepository.findByItemId(itemId).stream()
                .map(stockCountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountDTO> getStockCountsByWarehouse(UUID warehouseId) {
        log.debug("Fetching stock counts for warehouse ID: {}", warehouseId);

        return stockCountRepository.findByWarehouseId(warehouseId).stream()
                .map(stockCountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountDTO> getStockCountsByWarehouseAndSection(UUID warehouseId, UUID sectionId) {
        log.debug("Fetching stock counts for warehouse ID: {} and section ID: {}", warehouseId, sectionId);

        return stockCountRepository.findByWarehouseIdAndSectionId(warehouseId, sectionId).stream()
                .map(stockCountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountDTO> getAvailableStock() {
        log.debug("Fetching available stock counts");

        return stockCountRepository.findAll().stream()
                .map(stockCountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteStockCount(UUID countId) {
        log.info("Deleting stock count with ID: {}", countId);

        if (!stockCountRepository.existsById(countId)) {
            throw ResourceNotFoundException.forField("StockCount", "countId", countId);
        }

        stockCountRepository.deleteById(countId);
        log.info("Deleted stock count with ID: {}", countId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalBatchQuantity(UUID itemId) {
        log.debug("Calculating total batch quantity for item ID: {}", itemId);

        return itemBatchRepository.findByItemId(itemId).stream()
                .mapToInt(ItemBatch::getQuantity)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalAggregatedQuantity(UUID itemId) {
        log.debug("Calculating total aggregated quantity for item ID: {}", itemId);

        // This could include additional logic for aggregating quantities across different states
        return getTotalBatchQuantity(itemId);
    }
}
