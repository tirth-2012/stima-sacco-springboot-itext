package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p ORDER BY p.productType")
    List<Product> findAllOrderByProductType();

    List<Product> findByProductNameIgnoreCase(String productName);

    List<Product> findByProductTypeIgnoreCase(String productType);
}
