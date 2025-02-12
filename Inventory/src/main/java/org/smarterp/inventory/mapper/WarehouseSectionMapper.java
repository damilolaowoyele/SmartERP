package org.smarterp.inventory.mapper;


import org.smarterp.inventory.dto.section.WarehouseSectionCreateRequest;
import org.smarterp.inventory.dto.section.WarehouseSectionDTO;
import org.smarterp.inventory.entity.WarehouseSection;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class WarehouseSectionMapper {

    public WarehouseSectionDTO toDTO(WarehouseSection section) {
        if (section == null) return null;

        WarehouseSectionDTO dto = new WarehouseSectionDTO();
        dto.setSectionId(section.getSectionId());
        dto.setName(section.getName());
        dto.setWarehouseId(section.getWarehouse().getWarehouseId());
        dto.setCapacity(section.getCapacity());

        dto.setStockCountIds(section.getStockCounts().stream()
                .map(count -> count.getCountId())
                .collect(Collectors.toSet()));

        dto.setCreatedAt(section.getCreatedAt());
        dto.setUpdatedAt(section.getUpdatedAt());

        return dto;
    }

    public WarehouseSection toEntity(WarehouseSectionCreateRequest request) {
        if (request == null) return null;

        WarehouseSection section = new WarehouseSection();
        section.setName(request.getName());
        section.setCapacity(request.getCapacity());

        return section;
    }
}
