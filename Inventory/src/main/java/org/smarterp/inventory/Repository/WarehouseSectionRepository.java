package org.smarterp.inventory.Repository;

import org.smarterp.inventory.entity.WarehouseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseSectionRepository extends JpaRepository<WarehouseSection, UUID> {
    List<WarehouseSection> findByWarehouseId(UUID warehouseId);
    Optional<WarehouseSection> findByWarehouseIdAndNameIgnoreCase(UUID warehouseId, String name);
    boolean existsByWarehouseIdAndNameIgnoreCase(UUID warehouseId, String name);
}
