package com.exercise_1.modules.purchase.repository;

import com.exercise_1.modules.purchase.entity.Purchase;
import com.exercise_1.modules.purchase.entity.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query(value = """
            SELECT 
                p.number,
                pd.quantity,
                pd.price
            FROM purchase_details pd
            INNER JOIN purchases p ON pd.purchase_id = p.id
            WHERE pd.product_id = :productId
              AND p.status = 'CONFIRMED'
            ORDER BY p.created_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findConfirmedPurchasesByProductId(
            @Param("productId") Long productId,
            @Param("limit") int limit);

    @Query("SELECT DISTINCT p FROM Purchase p " +
            "LEFT JOIN FETCH p.supplier s " +
            "LEFT JOIN FETCH p.warehouse w " +
            "LEFT JOIN FETCH p.user u " +
            "LEFT JOIN FETCH p.payment pm " +
            "LEFT JOIN FETCH p.details pd " +
            "WHERE LOWER(p.number) LIKE LOWER(CONCAT('%', :number, '%'))")
    Page<Purchase> findByNumberContainingIgnoreCase(@Param("number") String number, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Purchase p " +
            "LEFT JOIN FETCH p.supplier s " +
            "LEFT JOIN FETCH p.warehouse w " +
            "LEFT JOIN FETCH p.user u " +
            "LEFT JOIN FETCH p.payment pm " +
            "LEFT JOIN FETCH p.details pd " +
            "WHERE p.status = :status")
    Page<Purchase> findByStatus(@Param("status") PurchaseStatus status, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Purchase p " +
            "LEFT JOIN FETCH p.supplier s " +
            "LEFT JOIN FETCH p.warehouse w " +
            "LEFT JOIN FETCH p.user u " +
            "LEFT JOIN FETCH p.payment pm " +
            "LEFT JOIN FETCH p.details pd " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    Page<Purchase> findByUserUsernameContainingIgnoreCase(@Param("username") String username, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Purchase p " +
            "LEFT JOIN FETCH p.supplier s " +
            "LEFT JOIN FETCH p.warehouse w " +
            "LEFT JOIN FETCH p.user u " +
            "LEFT JOIN FETCH p.payment pm " +
            "LEFT JOIN FETCH p.details pd " +
            "WHERE p.createdAt BETWEEN :dateFrom AND :dateTo")
    Page<Purchase> findByCreatedAtBetween(@Param("dateFrom") Instant dateFrom,
                                          @Param("dateTo") Instant dateTo,
                                          Pageable pageable);
}