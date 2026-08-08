package org.smarterp.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smarterp.inventory.dto.section.WarehouseSectionCreateRequest;
import org.smarterp.inventory.dto.section.WarehouseSectionDTO;
import org.smarterp.inventory.dto.section.WarehouseSectionUpdateRequest;
import org.smarterp.inventory.entity.Warehouse;
import org.smarterp.inventory.entity.WarehouseSection;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.WarehouseSectionMapper;
import org.smarterp.inventory.Repository.WarehouseRepository;
import org.smarterp.inventory.Repository.WarehouseSectionRepository;
import org.smarterp.inventory.service.WarehouseSectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseSectionServiceImpl implements WarehouseSectionService {

    private final WarehouseSectionRepository warehouseSectionRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseSectionMapper warehouseSectionMapper;

    @Override
    @Transactional
    public WarehouseSectionDTO createWarehouseSection(WarehouseSectionCreateRequest request) {
        log.info("Creating warehouse section with name: {}", request.getName());

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> ResourceNotFoundException.forField("Warehouse", "warehouseId", request.getWarehouseId()));

        WarehouseSection warehouseSection = warehouseSectionMapper.toEntity(request);
        warehouseSection.setWarehouse(warehouse);

        WarehouseSection savedSection = warehouseSectionRepository.save(warehouseSection);
        log.info("Created warehouse section with ID: {}", savedSection.getSectionId());

        return warehouseSectionMapper.toDTO(savedSection);
    }

    @Override
    @Transactional
    public WarehouseSectionDTO updateWarehouseSection(UUID sectionId, WarehouseSectionUpdateRequest request) {
        log.info("Updating warehouse section with ID: {}", sectionId);

        WarehouseSection warehouseSection = warehouseSectionRepository.findById(sectionId)
                .orElseThrow(() -> ResourceNotFoundException.forField("WarehouseSection", "sectionId", sectionId));

        if (request.getName() != null) {
            warehouseSection.setName(request.getName());
        }
        if (request.getCapacity() != null) {
            warehouseSection.setCapacity(request.getCapacity());
        }

        WarehouseSection updatedSection = warehouseSectionRepository.save(warehouseSection);
        log.info("Updated warehouse section with ID: {}", updatedSection.getSectionId());

        return warehouseSectionMapper.toDTO(updatedSection);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseSectionDTO getWarehouseSectionById(UUID sectionId) {
        log.debug("Fetching warehouse section with ID: {}", sectionId);

        WarehouseSection warehouseSection = warehouseSectionRepository.findById(sectionId)
                .orElseThrow(() -> ResourceNotFoundException.forField("WarehouseSection", "sectionId", sectionId));

        return warehouseSectionMapper.toDTO(warehouseSection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseSectionDTO> getAllWarehouseSections() {
        log.debug("Fetching all warehouse sections");

        return warehouseSectionRepository.findAll().stream()
                .map(warehouseSectionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseSectionDTO> getWarehouseSectionsByWarehouseId(UUID warehouseId) {
        log.debug("Fetching warehouse sections for warehouse ID: {}", warehouseId);

        return warehouseSectionRepository.findByWarehouseId(warehouseId).stream()
                .map(warehouseSectionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteWarehouseSection(UUID sectionId) {
        log.info("Deleting warehouse section with ID: {}", sectionId);

        if (!warehouseSectionRepository.existsById(sectionId)) {
            throw ResourceNotFoundException.forField("WarehouseSection", "sectionId", sectionId);
        }

        warehouseSectionRepository.deleteById(sectionId);
        log.info("Deleted warehouse section with ID: {}", sectionId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEnoughCapacity(UUID sectionId, int quantity) {
        log.debug("Checking capacity for warehouse section ID: {} with quantity: {}", sectionId, quantity);

        WarehouseSection warehouseSection = warehouseSectionRepository.findById(sectionId)
                .orElseThrow(() -> ResourceNotFoundException.forField("WarehouseSection", "sectionId", sectionId));

        BigDecimal capacity = warehouseSection.getCapacity();
        if (capacity == null) {
            return true; // No capacity limit set
        }

        int currentStock = getTotalStockCount(sectionId);
        return BigDecimal.valueOf(currentStock + quantity).compareTo(capacity) <= 0;
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalStockCount(UUID sectionId) {
        log.debug("Calculating total stock count for warehouse section ID: {}", sectionId);

        // This would typically query the stock counts or item batches in this section
        // For now, returning 0 as a placeholder - would need to integrate with StockCount or ItemBatch
        return 0;
    }

    @Override
    @Transactional
    public void addStock(UUID sectionId, int quantity) {
        log.info("Adding stock to warehouse section ID: {} with quantity: {}", sectionId, quantity);

        WarehouseSection warehouseSection = warehouseSectionRepository.findById(sectionId)
                .orElseThrow(() -> ResourceNotFoundException.forField("WarehouseSection", "sectionId", sectionId));

        if (!hasEnoughCapacity(sectionId, quantity)) {
            throw new IllegalArgumentException("Insufficient capacity in warehouse section");
        }

        // Update logic would depend on how stock is tracked (via StockCount or ItemBatch)
        log.info("Stock added successfully to warehouse section ID: {}", sectionId);
    }

    @Override
    @Transactional
    public void removeStock(UUID sectionId, int quantity) {
        log.info("Removing stock from warehouse section ID: {} with quantity: {}", sectionId, quantity);

        WarehouseSection warehouseSection = warehouseSectionRepository.findById(sectionId)
                .orElseThrow(() -> ResourceNotFoundException.forField("WarehouseSection", "sectionId", sectionId));

        int currentStock = getTotalStockCount(sectionId);
        if (currentStock < quantity) {
            throw new IllegalArgumentException("Insufficient stock in warehouse section");
        }

        // Update logic would depend on how stock is tracked (via StockCount or ItemBatch)
        log.info("Stock removed successfully from warehouse section ID: {}", sectionId);
    }
}
