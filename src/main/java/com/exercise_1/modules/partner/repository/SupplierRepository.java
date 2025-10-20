package com.exercise_1.modules.partner.repository;

import com.exercise_1.modules.partner.entity.Customer;
import com.exercise_1.modules.partner.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByEmail(String email);
}