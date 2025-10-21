package com.exercise_1.modules.catalog.repository;

import com.exercise_1.modules.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySkuContainingIgnoreCase(String sku);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findBySkuContainingIgnoreCaseAndNameContainingIgnoreCase(String sku, String name);

    boolean existsBySku(String sku);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> findByNameContainingIgnoreCasePageable(@Param("name") String name, Pageable pageable);
}