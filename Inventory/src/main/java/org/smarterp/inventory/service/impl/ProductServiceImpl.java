package org.smarterp.inventory.service.impl;

import org.smarterp.inventory.dto.product.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.smarterp.inventory.Repository.ProductRepository;
import org.smarterp.inventory.dto.product.ProductCreateRequest;
import org.smarterp.inventory.dto.product.ProductDTO;
import org.smarterp.inventory.entity.Product;
import org.smarterp.inventory.exception.ResourceAlreadyExistsException;
import org.smarterp.inventory.exception.ResourceNotFoundException;
import org.smarterp.inventory.mapper.ProductMapper;
import org.smarterp.inventory.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTO createProduct(ProductCreateRequest request) {
        if (productRepository.existsByProductNameIgnoreCase(request.getProductName())) {
            throw ResourceAlreadyExistsException.forField("Product", "name", request.getProductName());
        }

        Product product = productMapper.toEntity(request);
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO updateProduct(UUID productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Product", productId));

        if (request.getProductName() != null &&
                !request.getProductName().equalsIgnoreCase(product.getProductName()) &&
                productRepository.existsByProductNameIgnoreCase(request.getProductName())) {
            throw ResourceAlreadyExistsException.forField("Product", "name", request.getProductName());
        }

        if (request.getProductName() != null) product.setProductName(request.getProductName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getDefaultCost() != null) product.setDefaultCost(request.getDefaultCost());

        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(UUID productId) {
        return productMapper.toDTO(productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Product", productId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProductsByName(String name) {
        return productRepository.findByProductNameContainingIgnoreCase(name).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByPriceRange(BigDecimal maxPrice) {
        return productRepository.findByPriceLessThanEqual(maxPrice).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ResourceNotFoundException.forId("Product", productId));

        productRepository.delete(product);
    }
}
