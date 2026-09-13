package com.marcosperboni.productservice.service;

import com.marcosperboni.productservice.dto.ProductDto;
import com.marcosperboni.productservice.dto.ProductRequest;
import com.marcosperboni.productservice.entity.Product;
import com.marcosperboni.productservice.exception.ResourceNotFoundException;
import com.marcosperboni.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> findAll(String category) {
        List<Product> products = StringUtils.hasText(category)
                ? productRepository.findByCategory(category)
                : productRepository.findAll();
        return products.stream().map(ProductService::toDto).toList();
    }

    public ProductDto findById(UUID id) {
        return toDto(getOrThrow(id));
    }

    public ProductDto create(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        return toDto(productRepository.save(product));
    }

    public ProductDto update(UUID id, ProductRequest request) {
        Product product = getOrThrow(id);
        applyRequest(product, request);
        return toDto(productRepository.save(product));
    }

    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    private Product getOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private static void applyRequest(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(request.category());
    }

    private static ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory(),
                product.getCreatedAt()
        );
    }
}
