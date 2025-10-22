package com.exercise_1.modules.catalog.repository;

import com.exercise_1.modules.catalog.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    boolean existsByCode(String code);
}