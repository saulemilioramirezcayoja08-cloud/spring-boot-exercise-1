package com.exercise_1.modules.partner.repository;

import com.exercise_1.modules.partner.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}