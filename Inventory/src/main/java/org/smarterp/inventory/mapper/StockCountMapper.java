package org.smarterp.inventory.mapper;


import org.smarterp.inventory.dto.stock.StockCountCreateRequest;
import org.smarterp.inventory.dto.stock.StockCountDTO;
import org.smarterp.inventory.entity.StockCount;
import org.springframework.stereotype.Component;

@Component
public class StockCountMapper {

    public StockCountDTO toDTO(StockCount count) {
        if (count == null) return null;

        StockCountDTO dto = new StockCountDTO();
        dto.setCountId(count.getCountId());
        dto.setItemId(count.getItem().getItemId());
        dto.setWarehouseId(count.getWarehouse().getWarehouseId());
        dto.setSectionId(count.getSection() != null ? count.getSection().getSectionId() : null);
        dto.setItemBatchId(count.getItemBatch() != null ? count.getItemBatch().getBatchId() : null);
        dto.setCountType(count.getCountType());
        dto.setQuantity(count.getQuantity());
        dto.setLastUpdated(count.getLastUpdated());

        return dto;
    }

    public StockCount toEntity(StockCountCreateRequest request) {
        if (request == null) return null;

        StockCount count = new StockCount();
        count.setCountType(request.getCountType());
        count.setQuantity(request.getQuantity());

        return count;
    }
}
