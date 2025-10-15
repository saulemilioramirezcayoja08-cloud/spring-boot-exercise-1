package com.exercise_1.modules.catalog.repository;

import com.exercise_1.modules.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}