package com.exercise_1.modules.catalog.repository;

import com.exercise_1.modules.catalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySkuContainingIgnoreCase(String sku);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findBySkuContainingIgnoreCaseAndNameContainingIgnoreCase(String sku, String name);

    boolean existsBySku(String sku);
}