package org.smarterp.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.Repository.*;
import org.smarterp.inventory.dto.movement.InventoryMovementCreateRequest;
import org.smarterp.inventory.dto.movement.InventoryMovementDTO;
import org.smarterp.inventory.entity.InventoryMovement;
import org.smarterp.inventory.entity.ItemBatch;
import org.smarterp.inventory.entity.StockCount;
import org.smarterp.inventory.exception.InvalidOperationException;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.InventoryMovementMapper;
import org.smarterp.inventory.service.InventoryMovementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryMovementServiceImpl implements InventoryMovementService {
    private final InventoryMovementRepository movementRepository;
    private final ItemRepository itemRepository;
    private final ItemBatchRepository itemBatchRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseSectionRepository sectionRepository;
    private final StockCountRepository stockCountRepository;
    private final InventoryMovementMapper movementMapper;

    @Override
    public InventoryMovementDTO createMovement(InventoryMovementCreateRequest request, UUID userId) {
        ItemBatch itemBatch = itemBatchRepository.findById(request.getItemBatchId())
                .orElseThrow(() -> ResourceNotFoundException.forId("ItemBatch", request.getItemBatchId()));

        validateMovementRequest(request, itemBatch);

        InventoryMovement movement = movementMapper.toEntity(request);
        movement.setItemBatch(itemBatch);

        if (request.getSourceWarehouseId() != null) {
            movement.setSourceWarehouse(warehouseRepository.findById(request.getSourceWarehouseId())
                    .orElseThrow(() -> ResourceNotFoundException.forId("Source Warehouse", request.getSourceWarehouseId())));
        }

        if (request.getDestinationWarehouseId() != null) {
            movement.setDestinationWarehouse(warehouseRepository.findById(request.getDestinationWarehouseId())
                    .orElseThrow(() -> ResourceNotFoundException.forId("Destination Warehouse", request.getDestinationWarehouseId())));
        }

        updateStockCounts(movement);

        return movementMapper.toDTO(movementRepository.save(movement));
    }

    private void validateMovementRequest(InventoryMovementCreateRequest request, ItemBatch itemBatch) {
        switch (request.getMovementType()) {
            case INBOUND:
                if (request.getDestinationWarehouseId() == null) {
                    throw InvalidOperationException.invalidMovement("Destination warehouse is required for inbound movement");
                }
                break;
            case OUTBOUND:
                if (request.getSourceWarehouseId() == null) {
                    throw InvalidOperationException.invalidMovement("Source warehouse is required for outbound movement");
                }
                validateSufficientStock(itemBatch, request.getSourceWarehouseId(), request.getQuantity());
                break;
            case TRANSFER:
                if (request.getSourceWarehouseId() == null || request.getDestinationWarehouseId() == null) {
                    throw InvalidOperationException.invalidMovement("Both source and destination warehouses are required for transfer");
                }
                validateSufficientStock(itemBatch, request.getSourceWarehouseId(), request.getQuantity());
                break;
            case ADJUSTMENT:
                if (request.getSourceWarehouseId() == null) {
                    throw InvalidOperationException.invalidMovement("Source warehouse is required for adjustment");
                }
                break;
        }
    }

    private void validateSufficientStock(ItemBatch itemBatch, UUID warehouseId, int quantity) {
        StockCount stockCount = stockCountRepository.findByItemIdAndWarehouseIdAndSectionIdAndItemBatchId(
                itemBatch.getItem().getItemId(), warehouseId, null, itemBatch.getBatchId()
        ).orElseThrow(() -> InvalidOperationException.insufficientStock(
                itemBatch.getItem().getItemName(),
                "Warehouse " + warehouseId
        ));

        if (stockCount.getQuantity() < quantity) {
            throw InvalidOperationException.insufficientStock(
                    itemBatch.getItem().getItemName(),
                    "Warehouse " + warehouseId
            );
        }
    }

//    private void updateStockCounts(InventoryMovement movement) {
//        // Implementation of stock record updates based on movement type
//        // This would include creating or updating StockCount entities
//        // and managing the inventory levels in the affected warehouses
//    }

    private void updateStockCounts(InventoryMovement movement) {
        UUID itemId = movement.getItemBatch().getItem().getItemId();
        UUID itemBatchId = movement.getItemBatch().getBatchId();
        UUID sourceWarehouseId = movement.getSourceWarehouse() != null ? movement.getSourceWarehouse().getWarehouseId() : null;
        UUID destinationWarehouseId = movement.getDestinationWarehouse() != null ? movement.getDestinationWarehouse().getWarehouseId() : null;
        UUID sourceSectionId = movement.getSourceWarehouseSection() != null ? movement.getSourceWarehouseSection().getSectionId() : null;
        UUID destinationSectionId = movement.getDestinationWarehouseSection() != null ? movement.getDestinationWarehouseSection().getSectionId() : null;
        int quantity = movement.getQuantity();

        switch (movement.getMovementType()) {
            case INBOUND:
                updateStockCount(itemId, itemBatchId, destinationWarehouseId, destinationSectionId, quantity);
                break;
            case OUTBOUND:
                updateStockCount(itemId, itemBatchId, sourceWarehouseId, sourceSectionId, -quantity);
                break;
            case TRANSFER:
                updateStockCount(itemId, itemBatchId, sourceWarehouseId, sourceSectionId, -quantity);
                updateStockCount(itemId, itemBatchId, destinationWarehouseId, destinationSectionId, quantity);
                break;
            case ADJUSTMENT:
                updateStockCount(itemId, itemBatchId, sourceWarehouseId, sourceSectionId, quantity);
                break;
        }
    }

    private void updateStockCount(UUID itemId, UUID itemBatchId, UUID warehouseId, UUID sectionId, int quantityChange) {
        StockCount stockCount = stockCountRepository.findByItemIdAndWarehouseIdAndSectionIdAndItemBatchId(
                itemId, warehouseId, sectionId, itemBatchId
        ).orElseGet(() -> createNewStockCount(itemId, itemBatchId, warehouseId, sectionId));

        stockCount.setQuantity(stockCount.getQuantity() + quantityChange);
        stockCountRepository.save(stockCount);
    }

    private StockCount createNewStockCount(UUID itemId, UUID itemBatchId, UUID warehouseId, UUID sectionId) {
        StockCount stockCount = new StockCount();
        stockCount.setItem(itemRepository.findById(itemId).orElseThrow(() -> ResourceNotFoundException.forId("Item", itemId)));
        stockCount.setItemBatch(itemBatchRepository.findById(itemBatchId).orElseThrow(() -> ResourceNotFoundException.forId("ItemBatch", itemBatchId)));
        stockCount.setWarehouse(warehouseRepository.findById(warehouseId).orElseThrow(() -> ResourceNotFoundException.forId("Warehouse", warehouseId)));
        if (sectionId != null) {
            stockCount.setSection(sectionRepository.findById(sectionId).orElseThrow(() -> ResourceNotFoundException.forId("WarehouseSection", sectionId)));
        }
        stockCount.setCountType(StockCount.CountType.BATCH);
        stockCount.setQuantity(0);
        return stockCount;
    }


    @Override
    @Transactional(readOnly = true)
    public InventoryMovementDTO getMovementById(UUID movementId) {
        return movementMapper.toDTO(movementRepository.findById(movementId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Movement", movementId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementDTO> getAllMovements() {
        return movementRepository.findAll().stream()
                .map(movementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementDTO> getMovementsByItemBatch(UUID itemBatchId) {
        return movementRepository.findByItemBatchId(itemBatchId).stream()
                .map(movementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementDTO> getMovementsByWarehouse(UUID warehouseId, boolean isSource) {
        List<InventoryMovement> movements = isSource ?
                movementRepository.findBySourceWarehouseId(warehouseId) :
                movementRepository.findByDestinationWarehouseId(warehouseId);

        return movements.stream()
                .map(movementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementDTO> getMovementsByDateRange(LocalDateTime start, LocalDateTime end) {
        return movementRepository.findByMovementDateBetween(start, end).stream()
                .map(movementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementDTO> getMovementsByUser(UUID userId) {
        return movementRepository.findByCreatedBy(userId).stream()
                .map(movementMapper::toDTO)
                .collect(Collectors.toList());
    }
}
