package org.smarterp.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.Repository.ItemBatchRepository;
import org.smarterp.inventory.Repository.ItemRepository;
import org.smarterp.inventory.Repository.WarehouseRepository;
import org.smarterp.inventory.Repository.WarehouseSectionRepository;
import org.smarterp.inventory.dto.batch.ItemBatchCreateRequest;
import org.smarterp.inventory.dto.batch.ItemBatchDTO;
import org.smarterp.inventory.dto.batch.ItemBatchUpdateRequest;
import org.smarterp.inventory.entity.*;
import org.smarterp.inventory.exception.InvalidOperationException;
import org.smarterp.inventory.exception.ResourceAlreadyExistsException;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.ItemBatchMapper;
import org.smarterp.inventory.service.ItemBatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemBatchServiceImpl implements ItemBatchService {
    private final ItemBatchRepository itemBatchRepository;
    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseSectionRepository sectionRepository;
    private final ItemBatchMapper itemBatchMapper;

    @Override
    public ItemBatchDTO createBatch(ItemBatchCreateRequest request) {
        if (itemBatchRepository.existsByItemIdAndBatchNumber(request.getItemId(), request.getBatchNumber())) {
            throw ResourceAlreadyExistsException.forField("ItemBatch", "batchNumber", request.getBatchNumber());
        }

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> ResourceNotFoundException.forId("Item", request.getItemId()));

        ItemBatch batch = itemBatchMapper.toEntity(request);
        batch.setItem(item);

        if (request.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> ResourceNotFoundException.forId("Warehouse", request.getWarehouseId()));
            batch.setWarehouse(warehouse);

            if (request.getSectionId() != null) {
                WarehouseSection section = sectionRepository.findById(request.getSectionId())
                        .orElseThrow(() -> ResourceNotFoundException.forId("WarehouseSection", request.getSectionId()));

                if (!section.getWarehouse().getWarehouseId().equals(warehouse.getWarehouseId())) {
                    throw new InvalidOperationException("Section does not belong to the specified warehouse");
                }

                batch.setSection(section);
            }
        }

        return itemBatchMapper.toDTO(itemBatchRepository.save(batch));
    }

    @Override
    public ItemBatchDTO updateBatch(UUID batchId, ItemBatchUpdateRequest request) {
        ItemBatch batch = itemBatchRepository.findById(batchId)
                .orElseThrow(() -> ResourceNotFoundException.forId("ItemBatch", batchId));

        if (request.getBatchNumber() != null &&
                !request.getBatchNumber().equals(batch.getBatchNumber()) &&
                itemBatchRepository.existsByItemIdAndBatchNumber(batch.getItem().getItemId(), request.getBatchNumber())) {
            throw ResourceAlreadyExistsException.forField("ItemBatch", "batchNumber", request.getBatchNumber());
        }

        if (request.getBatchNumber() != null) batch.setBatchNumber(request.getBatchNumber());
        if (request.getManufactureDate() != null) batch.setManufactureDate(request.getManufactureDate());
        if (request.getExpiryDate() != null) batch.setExpiryDate(request.getExpiryDate());
        if (request.getQuantity() != null) batch.setQuantity(request.getQuantity());

        if (request.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> ResourceNotFoundException.forId("Warehouse", request.getWarehouseId()));
            batch.setWarehouse(warehouse);

            if (request.getSectionId() != null) {
                WarehouseSection section = sectionRepository.findById(request.getSectionId())
                        .orElseThrow(() -> ResourceNotFoundException.forId("WarehouseSection", request.getSectionId()));

                if (!section.getWarehouse().getWarehouseId().equals(warehouse.getWarehouseId())) {
                    throw new InvalidOperationException("Section does not belong to the specified warehouse");
                }

                batch.setSection(section);
            }
        }

        return itemBatchMapper.toDTO(itemBatchRepository.save(batch));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemBatchDTO getBatchById(UUID batchId) {
        return itemBatchMapper.toDTO(itemBatchRepository.findById(batchId)
                .orElseThrow(() -> ResourceNotFoundException.forId("ItemBatch", batchId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBatchDTO> getAllBatches() {
        return itemBatchRepository.findAll().stream()
                .map(itemBatchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBatchDTO> getBatchesByItem(UUID itemId) {
        return itemBatchRepository.findByItemId(itemId).stream()
                .map(itemBatchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBatchDTO> getBatchesByWarehouse(UUID warehouseId) {
        return itemBatchRepository.findByWarehouseId(warehouseId).stream()
                .map(itemBatchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBatchDTO> getExpiringBatches(LocalDate beforeDate) {
        return itemBatchRepository.findByExpiryDateBefore(beforeDate).stream()
                .map(itemBatchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBatchDTO> getAvailableBatches() {
        return itemBatchRepository.findAll().stream()
                .filter(batch -> batch.getQuantity() > 0)
                .map(itemBatchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBatch(UUID batchId) {
        ItemBatch batch = itemBatchRepository.findById(batchId)
                .orElseThrow(() -> ResourceNotFoundException.forId("ItemBatch", batchId));

        itemBatchRepository.delete(batch);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public int getTotalBatchQuantity(UUID itemId) {
//        return itemBatchRepository.findAllByItemId(itemId).stream()
//                .filter(batch -> batch.getCountType() == StockCount.CountType.BATCH)
//                .mapToInt(ItemBatch::getQuantity)
//                .sum();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public int getTotalAggregatedQuantity(UUID itemId) {
//        return itemBatchRepository.findAllByItemId(itemId).stream()
//                .filter(batch -> batch.getCountType() == StockCount.CountType.AGGREGATED)
//                .mapToInt(ItemBatch::getQuantity)
//                .sum();
//    }
}



