package org.smarterp.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.Repository.*;
import org.smarterp.inventory.dto.stock.StockCountCreateRequest;
import org.smarterp.inventory.dto.stock.StockCountDTO;
import org.smarterp.inventory.entity.*;
import org.smarterp.inventory.exception.InvalidOperationException;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.StockCountMapper;
import org.smarterp.inventory.service.StockCountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StockCountServiceImpl implements StockCountService {
    private final StockCountRepository stockCountRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseSectionRepository sectionRepository;
    private final ItemBatchRepository itemBatchRepository;
    private final StockCountMapper stockCountMapper;

    @Override
    public StockCountDTO createStockCount(StockCountCreateRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> ResourceNotFoundException.forId("Item", request.getItemId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> ResourceNotFoundException.forId("Warehouse", request.getWarehouseId()));

        StockCount stockCount = stockCountMapper.toEntity(request);
        stockCount.setItem(item);
        stockCount.setWarehouse(warehouse);

        if (request.getSectionId() != null) {
            WarehouseSection section = sectionRepository.findById(request.getSectionId())
                    .orElseThrow(() -> ResourceNotFoundException.forId("WarehouseSection", request.getSectionId()));
            if (!section.getWarehouse().getWarehouseId().equals(request.getWarehouseId())) {
                throw new InvalidOperationException("Section does not belong to the specified warehouse");
            }
            stockCount.setSection(section);
        }

        if (request.getItemBatchId() != null) {
            ItemBatch itemBatch = itemBatchRepository.findById(request.getItemBatchId())
                    .orElseThrow(() -> ResourceNotFoundException.forId("ItemBatch", request.getItemBatchId()));
            if (!itemBatch.getItem().getItemId().equals(request.getItemId())) {
                throw new InvalidOperationException("ItemBatch does not belong to the specified item");
            }
            stockCount.setItemBatch(itemBatch);
        }

        return stockCountMapper.toDTO(stockCountRepository.save(stockCount));
    }

    @Override
    @Transactional(readOnly = true)
    public StockCountDTO getStockCountById(UUID recordId) {
        return stockCountMapper.toDTO(stockCountRepository.findById(recordId)
                .orElseThrow(() -> ResourceNotFoundException.forId("StockCount", recordId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountDTO> getAllStockCounts() {
        return stockCountRepository.findAll().stream()
                .map(stockCountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountDTO> getStockCountsByItem(UUID itemId) {
        return stockCountRepository.findByItemId(itemId).stream()
                .map(stockCountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountDTO> getStockCountsByWarehouse(UUID warehouseId) {
        return stockCountRepository.findByWarehouseId(warehouseId).stream()
                .map(stockCountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountDTO> getStockCountsByWarehouseAndSection(UUID warehouseId, UUID sectionId) {
        return stockCountRepository.findByWarehouseIdAndSectionId(warehouseId, sectionId).stream()
                .map(stockCountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockCountDTO> getAvailableStock() {
        return stockCountRepository.findAll().stream()
                .filter(stockCount -> stockCount.getQuantity() > 0)
                .map(stockCountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteStockCount(UUID recordId) {
        StockCount stockCount = stockCountRepository.findById(recordId)
                .orElseThrow(() -> ResourceNotFoundException.forId("StockCount", recordId));

        stockCountRepository.delete(stockCount);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalBatchQuantity(UUID itemId) {
        return stockCountRepository.findAllByItemId(itemId).stream()
                .filter(stockCount -> stockCount.getCountType() == StockCount.CountType.BATCH)
                .mapToInt(StockCount::getQuantity)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalAggregatedQuantity(UUID itemId) {
        return stockCountRepository.findAllByItemId(itemId).stream()
                .filter(stockCount -> stockCount.getCountType() == StockCount.CountType.AGGREGATED)
                .mapToInt(StockCount::getQuantity)
                .sum();
    }
}



