package org.smarterp.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smarterp.inventory.dto.batch.ItemBatchCreateRequest;
import org.smarterp.inventory.dto.batch.ItemBatchDTO;
import org.smarterp.inventory.dto.batch.ItemBatchUpdateRequest;
import org.smarterp.inventory.entity.ItemBatch;
import org.smarterp.inventory.entity.Item;
import org.smarterp.inventory.entity.Warehouse;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.ItemBatchMapper;
import org.smarterp.inventory.Repository.ItemBatchRepository;
import org.smarterp.inventory.Repository.ItemRepository;
import org.smarterp.inventory.Repository.WarehouseRepository;
import org.smarterp.inventory.service.ItemBatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemBatchServiceImpl implements ItemBatchService {

    private final ItemBatchRepository itemBatchRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final ItemBatchMapper itemBatchMapper;

    @Override
    @Transactional
    public ItemBatchDTO createBatch(ItemBatchCreateRequest request) {
        log.info("Creating new item batch for item ID: {}", request.getItemId());

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> ResourceNotFoundException.forField("Item", "itemId", request.getItemId()));

        Warehouse warehouse = null;
        if (request.getWarehouseId() != null) {
            warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> ResourceNotFoundException.forField("Warehouse", "warehouseId", request.getWarehouseId()));
        }

        ItemBatch itemBatch = itemBatchMapper.toEntity(request);
        itemBatch.setItem(item);
        itemBatch.setWarehouse(warehouse);

        ItemBatch savedBatch = itemBatchRepository.save(itemBatch);
        log.info("Created item batch with ID: {}", savedBatch.getBatchId());

        return itemBatchMapper.toDTO(savedBatch);
    }

    @Override
    @Transactional
    public ItemBatchDTO updateBatch(UUID batchId, ItemBatchUpdateRequest request) {
        log.info("Updating item batch with ID: {}", batchId);

        ItemBatch itemBatch = itemBatchRepository.findById(batchId)
                .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", batchId));

        if (request.getBatchNumber() != null) {
            itemBatch.setBatchNumber(request.getBatchNumber());
        }
        if (request.getManufactureDate() != null) {
            itemBatch.setManufactureDate(request.getManufactureDate());
        }
        if (request.getExpiryDate() != null) {
            itemBatch.setExpiryDate(request.getExpiryDate());
        }
        if (request.getQuantity() != null) {
            itemBatch.setQuantity(request.getQuantity());
        }

        ItemBatch updatedBatch = itemBatchRepository.save(itemBatch);
        log.info("Updated item batch with ID: {}", updatedBatch.getBatchId());

        return itemBatchMapper.toDTO(updatedBatch);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemBatchDTO getBatchById(UUID batchId) {
        log.debug("Fetching item batch with ID: {}", batchId);

        ItemBatch itemBatch = itemBatchRepository.findById(batchId)
                .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", batchId));

        return itemBatchMapper.toDTO(itemBatch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBatchDTO> getAllBatches() {
        log.debug("Fetching all item batches");

        return itemBatchRepository.findAll().stream()
                .map(itemBatchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBatchDTO> getBatchesByItem(UUID itemId) {
        log.debug("Fetching item batches for item ID: {}", itemId);

        return itemBatchRepository.findByItemId(itemId).stream()
                .map(itemBatchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBatchDTO> getBatchesByWarehouse(UUID warehouseId) {
        log.debug("Fetching item batches for warehouse ID: {}", warehouseId);

        return itemBatchRepository.findByWarehouseId(warehouseId).stream()
                .map(itemBatchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBatchDTO> getExpiringBatches(LocalDate beforeDate) {
        log.debug("Fetching expiring item batches before date: {}", beforeDate);

        return itemBatchRepository.findByExpiryDateBefore(beforeDate).stream()
                .map(itemBatchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBatchDTO> getAvailableBatches() {
        log.debug("Fetching available item batches");

        return itemBatchRepository.findAll().stream()
                .filter(batch -> batch.getQuantity() > 0)
                .map(itemBatchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBatch(UUID batchId) {
        log.info("Deleting item batch with ID: {}", batchId);

        if (!itemBatchRepository.existsById(batchId)) {
            throw ResourceNotFoundException.forField("ItemBatch", "batchId", batchId);
        }

        itemBatchRepository.deleteById(batchId);
        log.info("Deleted item batch with ID: {}", batchId);
    }
}
