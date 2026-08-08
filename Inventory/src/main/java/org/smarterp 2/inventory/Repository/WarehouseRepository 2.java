package org.smarterp.inventory.Repository;

import org.smarterp.inventory.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    Optional<Warehouse> findByNameIgnoreCase(String name);
    List<Warehouse> findByLocationContainingIgnoreCase(String location);
    boolean existsByNameIgnoreCase(String name);
}
