package org.smarterp.inventory.service;

import org.smarterp.inventory.dto.product.ProductUpdateRequest;
import org.smarterp.inventory.dto.product.ProductCreateRequest;
import org.smarterp.inventory.dto.product.ProductDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDTO createProduct(ProductCreateRequest request);
    ProductDTO updateProduct(UUID productId, ProductUpdateRequest request);
    ProductDTO getProductById(UUID productId);
    List<ProductDTO> getAllProducts();
    List<ProductDTO> searchProductsByName(String name);
    List<ProductDTO> getProductsByPriceRange(BigDecimal maxPrice);
    void deleteProduct(UUID productId);
}
