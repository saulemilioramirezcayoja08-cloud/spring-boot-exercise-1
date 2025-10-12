package com.exercise_1.modules.sale.repository;

import com.exercise_1.modules.sale.entity.Sale;
import com.exercise_1.modules.sale.entity.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query(value = """
            SELECT 
                s.number,
                sd.quantity,
                sd.price
            FROM sale_details sd
            INNER JOIN sales s ON sd.sale_id = s.id
            WHERE sd.product_id = :productId
              AND s.status = 'CONFIRMED'
            ORDER BY s.created_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findConfirmedSalesByProductId(
            @Param("productId") Long productId,
            @Param("limit") int limit);

    @Query("SELECT DISTINCT s FROM Sale s " +
            "LEFT JOIN FETCH s.customer c " +
            "LEFT JOIN FETCH s.warehouse w " +
            "LEFT JOIN FETCH s.user u " +
            "LEFT JOIN FETCH s.payment p " +
            "LEFT JOIN FETCH s.order o " +
            "LEFT JOIN FETCH s.details sd " +
            "WHERE LOWER(s.number) LIKE LOWER(CONCAT('%', :number, '%'))")
    Page<Sale> findByNumberContainingIgnoreCase(@Param("number") String number, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Sale s " +
            "LEFT JOIN FETCH s.customer c " +
            "LEFT JOIN FETCH s.warehouse w " +
            "LEFT JOIN FETCH s.user u " +
            "LEFT JOIN FETCH s.payment p " +
            "LEFT JOIN FETCH s.order o " +
            "LEFT JOIN FETCH s.details sd " +
            "WHERE s.status = :status")
    Page<Sale> findByStatus(@Param("status") SaleStatus status, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Sale s " +
            "LEFT JOIN FETCH s.customer c " +
            "LEFT JOIN FETCH s.warehouse w " +
            "LEFT JOIN FETCH s.user u " +
            "LEFT JOIN FETCH s.payment p " +
            "LEFT JOIN FETCH s.order o " +
            "LEFT JOIN FETCH s.details sd " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    Page<Sale> findByUserUsernameContainingIgnoreCase(@Param("username") String username, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Sale s " +
            "LEFT JOIN FETCH s.customer c " +
            "LEFT JOIN FETCH s.warehouse w " +
            "LEFT JOIN FETCH s.user u " +
            "LEFT JOIN FETCH s.payment p " +
            "LEFT JOIN FETCH s.order o " +
            "LEFT JOIN FETCH s.details sd " +
            "WHERE s.createdAt BETWEEN :dateFrom AND :dateTo")
    Page<Sale> findByCreatedAtBetween(@Param("dateFrom") Instant dateFrom,
                                      @Param("dateTo") Instant dateTo,
                                      Pageable pageable);
}