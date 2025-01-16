package org.smarterp.inventory.mapper;


import org.smarterp.inventory.dto.movement.InventoryMovementCreateRequest;
import org.smarterp.inventory.dto.movement.InventoryMovementDTO;
import org.smarterp.inventory.entity.InventoryMovement;
import org.springframework.stereotype.Component;

@Component
public class InventoryMovementMapper {

    public InventoryMovementDTO toDTO(InventoryMovement movement) {
        if (movement == null) return null;

        InventoryMovementDTO dto = new InventoryMovementDTO();
        dto.setMovementId(movement.getMovementId());
        dto.setItemBatchId(movement.getItemBatch().getBatchId());
        dto.setSourceWarehouseId(movement.getSourceWarehouse() != null ?
                movement.getSourceWarehouse().getWarehouseId() : null);
        dto.setDestinationWarehouseId(movement.getDestinationWarehouse() != null ?
                movement.getDestinationWarehouse().getWarehouseId() : null);
        dto.setSourceWarehouseSectionId(movement.getSourceWarehouseSection() != null ?
                movement.getSourceWarehouseSection().getSectionId() : null);
        dto.setDestinationWarehouseSectionId(movement.getDestinationWarehouseSection() != null ?
                movement.getDestinationWarehouseSection().getSectionId() : null);
        dto.setMovementType(movement.getMovementType());
        dto.setQuantity(movement.getQuantity());
        dto.setMovementDate(movement.getMovementDate());
        dto.setRemarks(movement.getRemarks());
        dto.setCreatedBy(movement.getCreatedBy() != null ? movement.getCreatedBy().getUserId() : null);

        return dto;
    }

    public InventoryMovement toEntity(InventoryMovementCreateRequest request) {
        if (request == null) return null;

        InventoryMovement movement = new InventoryMovement();
        movement.setMovementType(request.getMovementType());
        movement.setQuantity(request.getQuantity());
        movement.setRemarks(request.getRemarks());

        return movement;
    }
}
