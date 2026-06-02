package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.ProductRequestDto;
import com.rutusoft.flowable.dto.ProductResponseDto;
import com.rutusoft.flowable.entity.Product;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.repository.ProductRepository;
import com.rutusoft.flowable.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto productRequest) {
        log.info("Creating product : {}", productRepository);
        List<Product> products = productRepository.findByProductNameIgnoreCase(productRequest.getProductName());
        if(!products.isEmpty()) {
            log.error("Product : {} is already exists", productRequest.getProductName());
            throw new ValidationException("Product is already exists");
        }
        Product product = new Product();
        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setCategory(productRequest.getCategory());
        product.setProductType(productRequest.getProductType());
        product.setRateType(productRequest.getRateType());
        product.setProductCode(productRequest.getProductCode());

        productRepository.save(product);
        log.info("Product created successfully");
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(product);
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setProductType(dto.getProductType());
        product.setRateType(dto.getRateType());
        product.setProductCode(dto.getProductCode());

        return mapToResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductResponseDto> getProductsPaginated(int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepository.findAll(pageable);

        return productPage.map(this::mapToResponse);
    }

    // MAPPER
    private ProductResponseDto mapToResponse(Product product) {

        ProductResponseDto response = new ProductResponseDto();

        response.setId(product.getId());
        response.setProductName(product.getProductName());
        response.setDescription(product.getDescription());
        response.setCategory(product.getCategory());
        response.setProductType(product.getProductType());
        response.setRateType(product.getRateType());
        response.setProductCode(product.getProductCode());

        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }

    @Override
    public Map<String, List<ProductResponseDto>> getProductsGroupedByType() {

        List<Product> products = productRepository.findAllOrderByProductType();

        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.groupingBy(ProductResponseDto::getProductType));
    }

    @Override
    public List<String> getProductNamesByProductType(String productType) {

        List<Product> products =
                productRepository.findByProductTypeIgnoreCase(productType);

        return products.stream()
                .map(Product::getProductName)
                .toList();
    }
}
