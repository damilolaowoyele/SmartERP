package org.smarterp.inventory.mapper;


import org.smarterp.inventory.dto.batch.ItemBatchCreateRequest;
import org.smarterp.inventory.dto.batch.ItemBatchDTO;
import org.smarterp.inventory.entity.ItemBatch;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ItemBatchMapper {

    public ItemBatchDTO toDTO(ItemBatch batch) {
        if (batch == null) return null;

        ItemBatchDTO dto = new ItemBatchDTO();
        dto.setBatchId(batch.getBatchId());
        dto.setItemId(batch.getItem().getItemId());
        dto.setBatchNumber(batch.getBatchNumber());
        dto.setManufactureDate(batch.getManufactureDate());
        dto.setExpiryDate(batch.getExpiryDate());
        dto.setQuantity(batch.getQuantity());
        dto.setWarehouseId(batch.getWarehouse() != null ? batch.getWarehouse().getWarehouseId() : null);
        dto.setSectionId(batch.getSection() != null ? batch.getSection().getSectionId() : null);

        dto.setMovementIds(batch.getMovements().stream()
                .map(movement -> movement.getMovementId())
                .collect(Collectors.toSet()));

        dto.setCreatedAt(batch.getCreatedAt());
        dto.setUpdatedAt(batch.getUpdatedAt());

        return dto;
    }

    public ItemBatch toEntity(ItemBatchCreateRequest request) {
        if (request == null) return null;

        ItemBatch batch = new ItemBatch();
        batch.setBatchNumber(request.getBatchNumber());
        batch.setManufactureDate(request.getManufactureDate());
        batch.setExpiryDate(request.getExpiryDate());
        batch.setQuantity(request.getQuantity());

        return batch;
    }
}
