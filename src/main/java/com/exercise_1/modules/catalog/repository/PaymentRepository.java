package com.exercise_1.modules.catalog.repository;

import com.exercise_1.modules.catalog.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}