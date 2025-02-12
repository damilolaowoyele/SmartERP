package org.smarterp.inventory.service.impl;

import org.smarterp.inventory.dto.warehouse.WarehouseUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.Repository.WarehouseRepository;
import org.smarterp.inventory.dto.warehouse.WarehouseCreateRequest;
import org.smarterp.inventory.dto.warehouse.WarehouseDTO;
import org.smarterp.inventory.entity.Warehouse;
import org.smarterp.inventory.exception.ResourceAlreadyExistsException;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.WarehouseMapper;
import org.smarterp.inventory.service.WarehouseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    public WarehouseDTO createWarehouse(WarehouseCreateRequest request) {
        if (warehouseRepository.existsByNameIgnoreCase(request.getName())) {
            throw ResourceAlreadyExistsException.forField("Warehouse", "name", request.getName());
        }

        Warehouse warehouse = warehouseMapper.toEntity(request);
        return warehouseMapper.toDTO(warehouseRepository.save(warehouse));
    }

    @Override
    public WarehouseDTO updateWarehouse(UUID warehouseId, WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Warehouse", warehouseId));

        if (request.getName() != null &&
                !request.getName().equalsIgnoreCase(warehouse.getName()) &&
                warehouseRepository.existsByNameIgnoreCase(request.getName())) {
            throw ResourceAlreadyExistsException.forField("Warehouse", "name", request.getName());
        }

        if (request.getName() != null) warehouse.setName(request.getName());
        if (request.getLocation() != null) warehouse.setLocation(request.getLocation());
        if (request.getCapacity() != null) warehouse.setCapacity(request.getCapacity());

        return warehouseMapper.toDTO(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseDTO getWarehouseById(UUID warehouseId) {
        return warehouseMapper.toDTO(warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Warehouse", warehouseId)));
    }
    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getAllWarehouses() {
        List<WarehouseDTO> warehouses = warehouseRepository.findAll().stream()
                .map(warehouseMapper::toDTO)
                .collect(Collectors.toList());
        if (warehouses.isEmpty()) {
            // Handle the case where no warehouses are found
            throw new ResourceNotFoundException("No warehouses found");
        }
        return warehouses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDTO> searchWarehousesByLocation(String location) {
        return warehouseRepository.findByLocationContainingIgnoreCase(location).stream()
                .map(warehouseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteWarehouse(UUID warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Warehouse", warehouseId));

        warehouseRepository.delete(warehouse);
    }
}
