package com.marcosperboni.productservice.service;

import com.marcosperboni.productservice.dto.ProductDto;
import com.marcosperboni.productservice.dto.ProductRequest;
import com.marcosperboni.productservice.entity.Product;
import com.marcosperboni.productservice.exception.ResourceNotFoundException;
import com.marcosperboni.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService service() {
        return new ProductService(productRepository);
    }

    @Test
    void findAllFiltersByCategoryWhenProvided() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Widget");
        product.setPrice(BigDecimal.TEN);
        product.setCategory("tools");
        product.setCreatedAt(Instant.now());
        when(productRepository.findByCategory("tools")).thenReturn(List.of(product));

        List<ProductDto> result = service().findAll("tools");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).category()).isEqualTo("tools");
    }

    @Test
    void findByIdThrowsWhenProductMissing() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createSavesAndReturnsDto() {
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(UUID.randomUUID());
            p.setCreatedAt(Instant.now());
            return p;
        });

        ProductDto dto = service().create(new ProductRequest("Gadget", "desc", BigDecimal.valueOf(19.99), 5, "tools"));

        assertThat(dto.id()).isNotNull();
        assertThat(dto.name()).isEqualTo("Gadget");
        assertThat(dto.price()).isEqualByComparingTo("19.99");
    }
}
