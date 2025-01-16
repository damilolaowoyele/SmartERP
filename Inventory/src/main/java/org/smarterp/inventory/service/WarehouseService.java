package org.smarterp.inventory.service;

import org.smarterp.inventory.dto.warehouse.WarehouseUpdateRequest;
import org.smarterp.inventory.dto.warehouse.WarehouseCreateRequest;
import org.smarterp.inventory.dto.warehouse.WarehouseDTO;

import java.util.List;
import java.util.UUID;

public interface WarehouseService {
    WarehouseDTO createWarehouse(WarehouseCreateRequest request);
    WarehouseDTO updateWarehouse(UUID warehouseId, WarehouseUpdateRequest request);
    WarehouseDTO getWarehouseById(UUID warehouseId);
    List<WarehouseDTO> getAllWarehouses();
    List<WarehouseDTO> searchWarehousesByLocation(String location);
    void deleteWarehouse(UUID warehouseId);
}
