package org.smarterp.inventory.Repository;

import org.smarterp.inventory.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByBarcodeIgnoreCase(String barcode);
    List<Item> findByItemNameContainingIgnoreCase(String name);
    List<Item> findByTemperatureSensitive(Boolean temperatureSensitive);
    List<Item> findAll();
    boolean existsByBarcodeIgnoreCase(String barcode);
}



