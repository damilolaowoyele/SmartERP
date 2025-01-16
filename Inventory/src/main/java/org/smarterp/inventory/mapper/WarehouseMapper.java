package org.smarterp.inventory.mapper;


import org.smarterp.inventory.dto.warehouse.WarehouseCreateRequest;
import org.smarterp.inventory.dto.warehouse.WarehouseDTO;
import org.smarterp.inventory.entity.Warehouse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class WarehouseMapper {

    public WarehouseDTO toDTO(Warehouse warehouse) {
        if (warehouse == null) return null;

        WarehouseDTO dto = new WarehouseDTO();
        dto.setWarehouseId(warehouse.getWarehouseId());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        dto.setCapacity(warehouse.getCapacity());

        dto.setSectionIds(warehouse.getSections().stream()
                .map(section -> section.getSectionId())
                .collect(Collectors.toSet()));

        dto.setStockCountIds(warehouse.getStockCounts().stream()
                .map(count -> count.getCountId())
                .collect(Collectors.toSet()));

        dto.setCreatedAt(warehouse.getCreatedAt());
        dto.setUpdatedAt(warehouse.getUpdatedAt());

        return dto;
    }

    public Warehouse toEntity(WarehouseCreateRequest request) {
        if (request == null) return null;

        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        warehouse.setCapacity(request.getCapacity());

        return warehouse;
    }
}
