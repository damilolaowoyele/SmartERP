package org.smarterp.inventory.service;

import org.smarterp.inventory.dto.section.WarehouseSectionCreateRequest;
import org.smarterp.inventory.dto.section.WarehouseSectionDTO;
import org.smarterp.inventory.dto.section.WarehouseSectionUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface WarehouseSectionService {
    WarehouseSectionDTO createWarehouseSection(WarehouseSectionCreateRequest request);
    WarehouseSectionDTO updateWarehouseSection(UUID sectionId, WarehouseSectionUpdateRequest request);
    WarehouseSectionDTO getWarehouseSectionById(UUID sectionId);
    List<WarehouseSectionDTO> getAllWarehouseSections();
    List<WarehouseSectionDTO> getWarehouseSectionsByWarehouseId(UUID warehouseId);
    void deleteWarehouseSection(UUID sectionId);
    boolean hasEnoughCapacity(UUID sectionId, int quantity);
    int getTotalStockCount(UUID sectionId);
    void addStock(UUID sectionId, int quantity);
    void removeStock(UUID sectionId, int quantity);
}
