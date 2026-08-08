package org.smarterp.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smarterp.inventory.dto.movement.InventoryMovementCreateRequest;
import org.smarterp.inventory.dto.movement.InventoryMovementDTO;
import org.smarterp.inventory.entity.InventoryMovement;
import org.smarterp.inventory.entity.ItemBatch;
import org.smarterp.inventory.entity.UserReference;
import org.smarterp.inventory.entity.Warehouse;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.InventoryMovementMapper;
import org.smarterp.inventory.Repository.InventoryMovementRepository;
import org.smarterp.inventory.Repository.ItemBatchRepository;
import org.smarterp.inventory.Repository.WarehouseRepository;
import org.smarterp.inventory.service.InventoryMovementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryMovementServiceImpl implements InventoryMovementService {

    private final InventoryMovementRepository inventoryMovementRepository;
    private final ItemBatchRepository itemBatchRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryMovementMapper inventoryMovementMapper;

    @Override
    @Transactional
    public InventoryMovementDTO createMovement(InventoryMovementCreateRequest request, UUID userId) {
        log.info("Creating inventory movement for item batch ID: {}", request.getItemBatchId());

        ItemBatch itemBatch = itemBatchRepository.findById(request.getItemBatchId())
                .orElseThrow(() -> ResourceNotFoundException.forField("ItemBatch", "batchId", request.getItemBatchId()));

        Warehouse sourceWarehouse = null;
        if (request.getSourceWarehouseId() != null) {
            sourceWarehouse = warehouseRepository.findById(request.getSourceWarehouseId())
                    .orElseThrow(() -> ResourceNotFoundException.forField("Warehouse", "warehouseId", request.getSourceWarehouseId()));
        }

        Warehouse destinationWarehouse = null;
        if (request.getDestinationWarehouseId() != null) {
            destinationWarehouse = warehouseRepository.findById(request.getDestinationWarehouseId())
                    .orElseThrow(() -> ResourceNotFoundException.forField("Warehouse", "warehouseId", request.getDestinationWarehouseId()));
        }

        InventoryMovement movement = inventoryMovementMapper.toEntity(request);
        movement.setItemBatch(itemBatch);
        movement.setSourceWarehouse(sourceWarehouse);
        movement.setDestinationWarehouse(destinationWarehouse);
        
        UserReference userRef = new UserReference();
        userRef.setUserId(userId);
        movement.setCreatedBy(userRef);

        // Update batch quantity based on movement type
        if (movement.getMovementType() == InventoryMovement.MovementType.INBOUND) {
            itemBatch.setQuantity(itemBatch.getQuantity() + request.getQuantity());
        } else if (movement.getMovementType() == InventoryMovement.MovementType.OUTBOUND) {
            if (itemBatch.getQuantity() < request.getQuantity()) {
                throw new IllegalArgumentException("Insufficient quantity in batch");
            }
            itemBatch.setQuantity(itemBatch.getQuantity() - request.getQuantity());
        }

        itemBatchRepository.save(itemBatch);
        InventoryMovement savedMovement = inventoryMovementRepository.save(movement);
        log.info("Created inventory movement with ID: {}", savedMovement.getMovementId());

        return inventoryMovementMapper.toDTO(savedMovement);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryMovementDTO getMovementById(UUID movementId) {
        log.debug("Fetching inventory movement with ID: {}", movementId);

        InventoryMovement movement = inventoryMovementRepository.findById(movementId)
                .orElseThrow(() -> ResourceNotFoundException.forField("InventoryMovement", "movementId", movementId));

        return inventoryMovementMapper.toDTO(movement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementDTO> getAllMovements() {
        log.debug("Fetching all inventory movements");

        return inventoryMovementRepository.findAll().stream()
                .map(inventoryMovementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementDTO> getMovementsByItemBatch(UUID itemBatchId) {
        log.debug("Fetching inventory movements for item batch ID: {}", itemBatchId);

        return inventoryMovementRepository.findByItemBatchId(itemBatchId).stream()
                .map(inventoryMovementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementDTO> getMovementsByWarehouse(UUID warehouseId, boolean isSource) {
        log.debug("Fetching inventory movements for warehouse ID: {} as {}", warehouseId, isSource ? "source" : "destination");

        List<InventoryMovement> movements;
        if (isSource) {
            movements = inventoryMovementRepository.findBySourceWarehouseId(warehouseId);
        } else {
            movements = inventoryMovementRepository.findByDestinationWarehouseId(warehouseId);
        }

        return movements.stream()
                .map(inventoryMovementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementDTO> getMovementsByDateRange(LocalDateTime start, LocalDateTime end) {
        log.debug("Fetching inventory movements between {} and {}", start, end);

        return inventoryMovementRepository.findByMovementDateBetween(start, end).stream()
                .map(inventoryMovementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryMovementDTO> getMovementsByUser(UUID userId) {
        log.debug("Fetching inventory movements by user ID: {}", userId);

        return inventoryMovementRepository.findByCreatedBy(userId).stream()
                .map(inventoryMovementMapper::toDTO)
                .collect(Collectors.toList());
    }
}
