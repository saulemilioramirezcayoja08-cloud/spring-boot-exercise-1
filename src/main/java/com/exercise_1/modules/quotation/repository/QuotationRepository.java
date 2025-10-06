package com.exercise_1.modules.quotation.repository;

import com.exercise_1.modules.quotation.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
}