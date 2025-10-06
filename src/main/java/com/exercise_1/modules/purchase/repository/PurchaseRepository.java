package com.exercise_1.modules.purchase.repository;

import com.exercise_1.modules.purchase.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}