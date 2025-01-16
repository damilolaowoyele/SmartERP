package org.smarterp.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.Repository.WarehouseRepository;
import org.smarterp.inventory.Repository.WarehouseSectionRepository;
import org.smarterp.inventory.dto.section.WarehouseSectionCreateRequest;
import org.smarterp.inventory.dto.section.WarehouseSectionDTO;
import org.smarterp.inventory.dto.section.WarehouseSectionUpdateRequest;
import org.smarterp.inventory.entity.StockCount;
import org.smarterp.inventory.entity.Warehouse;
import org.smarterp.inventory.entity.WarehouseSection;
import org.smarterp.inventory.exception.ResourceAlreadyExistsException;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.WarehouseSectionMapper;
import org.smarterp.inventory.service.WarehouseSectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseSectionServiceImpl implements WarehouseSectionService {
    private final WarehouseSectionRepository warehouseSectionRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseSectionMapper warehouseSectionMapper;

    @Override
    public WarehouseSectionDTO createWarehouseSection(WarehouseSectionCreateRequest request) {
        if (warehouseSectionRepository.existsByWarehouseIdAndNameIgnoreCase(request.getWarehouseId(), request.getName())) {
            throw ResourceAlreadyExistsException.forField("WarehouseSection", "name", request.getName());
        }

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> ResourceNotFoundException.forId("Warehouse", request.getWarehouseId()));

        WarehouseSection section = warehouseSectionMapper.toEntity(request);
        section.setWarehouse(warehouse);

        return warehouseSectionMapper.toDTO(warehouseSectionRepository.save(section));
    }

    @Override
    public WarehouseSectionDTO updateWarehouseSection(UUID sectionId, WarehouseSectionUpdateRequest request) {
        WarehouseSection section = warehouseSectionRepository.findById(sectionId)
                .orElseThrow(() -> ResourceNotFoundException.forId("WarehouseSection", sectionId));

        if (request.getName() != null &&
                !request.getName().equalsIgnoreCase(section.getName()) &&
                warehouseSectionRepository.existsByWarehouseIdAndNameIgnoreCase(section.getWarehouse().getWarehouseId(), request.getName())) {
            throw ResourceAlreadyExistsException.forField("WarehouseSection", "name", request.getName());
        }

        if (request.getName() != null) section.setName(request.getName());
        if (request.getCapacity() != null) section.setCapacity(request.getCapacity());

        return warehouseSectionMapper.toDTO(warehouseSectionRepository.save(section));
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseSectionDTO getWarehouseSectionById(UUID sectionId) {
        return warehouseSectionMapper.toDTO(warehouseSectionRepository.findById(sectionId)
                .orElseThrow(() -> ResourceNotFoundException.forId("WarehouseSection", sectionId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseSectionDTO> getAllWarehouseSections() {
        return warehouseSectionRepository.findAll().stream()
                .map(warehouseSectionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseSectionDTO> getWarehouseSectionsByWarehouseId(UUID warehouseId) {
        return warehouseSectionRepository.findByWarehouseId(warehouseId).stream()
                .map(warehouseSectionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteWarehouseSection(UUID sectionId) {
        WarehouseSection section = warehouseSectionRepository.findById(sectionId)
                .orElseThrow(() -> ResourceNotFoundException.forId("WarehouseSection", sectionId));

        warehouseSectionRepository.delete(section);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEnoughCapacity(UUID sectionId, int quantity) {
        WarehouseSection section = warehouseSectionRepository.findById(sectionId)
                .orElseThrow(() -> ResourceNotFoundException.forId("WarehouseSection", sectionId));
        return section.getCapacity().compareTo(BigDecimal.valueOf(quantity)) >= 0;
    }

//    @Override
//    @Transactional(readOnly = true)
//    public int getTotalStockCount(UUID sectionId) {
//        WarehouseSection section = warehouseSectionRepository.findById(sectionId)
//                .orElseThrow(() -> ResourceNotFoundException.forId("WarehouseSection", sectionId));
//        return section.getStockCounts().stream().mapToInt(stockCount -> stockCount.getQuantity()).sum();
//    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalStockCount(UUID sectionId) {
        WarehouseSection section = warehouseSectionRepository.findById(sectionId)
                .orElseThrow(() -> ResourceNotFoundException.forId("WarehouseSection", sectionId));

        return section.getStockCounts().stream()
                .filter(stockCount -> stockCount.getCountType() == StockCount.CountType.AGGREGATED)
                .mapToInt(StockCount::getQuantity)
                .sum();
    }

    @Override
    public void addStock(UUID sectionId, int quantity) {
        WarehouseSection section = warehouseSectionRepository.findById(sectionId)
                .orElseThrow(() -> ResourceNotFoundException.forId("WarehouseSection", sectionId));
        if (!hasEnoughCapacity(sectionId, quantity)) {
            throw new IllegalArgumentException("Not enough capacity in the warehouse section");
        }
        section.setCapacity(section.getCapacity().subtract(BigDecimal.valueOf(quantity)));
        warehouseSectionRepository.save(section);
    }

    public void removeStock(UUID sectionId, int quantity) {
        WarehouseSection section = warehouseSectionRepository.findById(sectionId)
                .orElseThrow(() -> ResourceNotFoundException.forId("WarehouseSection", sectionId));
        section.setCapacity(section.getCapacity().add(BigDecimal.valueOf(quantity)));
        warehouseSectionRepository.save(section);
    }
}