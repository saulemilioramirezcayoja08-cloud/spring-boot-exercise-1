package com.exercise_1.modules.quotation.repository;

import com.exercise_1.modules.quotation.entity.Quotation;
import com.exercise_1.modules.quotation.entity.QuotationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    @Query("SELECT DISTINCT q FROM Quotation q " +
            "LEFT JOIN FETCH q.customer c " +
            "LEFT JOIN FETCH q.warehouse w " +
            "LEFT JOIN FETCH q.user u " +
            "LEFT JOIN FETCH q.details qd " +
            "WHERE LOWER(q.number) LIKE LOWER(CONCAT('%', :number, '%'))")
    Page<Quotation> findByNumberContainingIgnoreCase(@Param("number") String number, Pageable pageable);

    @Query("SELECT DISTINCT q FROM Quotation q " +
            "LEFT JOIN FETCH q.customer c " +
            "LEFT JOIN FETCH q.warehouse w " +
            "LEFT JOIN FETCH q.user u " +
            "LEFT JOIN FETCH q.details qd " +
            "WHERE q.status = :status")
    Page<Quotation> findByStatus(@Param("status") QuotationStatus status, Pageable pageable);

    @Query("SELECT DISTINCT q FROM Quotation q " +
            "LEFT JOIN FETCH q.customer c " +
            "LEFT JOIN FETCH q.warehouse w " +
            "LEFT JOIN FETCH q.user u " +
            "LEFT JOIN FETCH q.details qd " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    Page<Quotation> findByUserUsernameContainingIgnoreCase(@Param("username") String username, Pageable pageable);

    @Query("SELECT DISTINCT q FROM Quotation q " +
            "LEFT JOIN FETCH q.customer c " +
            "LEFT JOIN FETCH q.warehouse w " +
            "LEFT JOIN FETCH q.user u " +
            "LEFT JOIN FETCH q.details qd " +
            "WHERE q.createdAt BETWEEN :dateFrom AND :dateTo")
    Page<Quotation> findByCreatedAtBetween(@Param("dateFrom") Instant dateFrom,
                                           @Param("dateTo") Instant dateTo,
                                           Pageable pageable);
}