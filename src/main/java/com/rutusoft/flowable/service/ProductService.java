package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.ProductRequestDto;
import com.rutusoft.flowable.dto.ProductResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;


public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto requestDto);

    List<ProductResponseDto> getAllProducts();

    ProductResponseDto getProductById(Long id);

    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);

    void deleteProduct(Long id);

    Map<String, List<ProductResponseDto>> getProductsGroupedByType();

    Page<ProductResponseDto> getProductsPaginated(int page, int size, String sortBy, String sortDir);

    List<String> getProductNamesByProductType(String productType);
}
