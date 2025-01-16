package org.smarterp.inventory.Repository;

import org.smarterp.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByProductNameContainingIgnoreCase(String name);
    List<Product> findByPriceLessThanEqual(BigDecimal price);
    boolean existsByProductNameIgnoreCase(String productName);
}
