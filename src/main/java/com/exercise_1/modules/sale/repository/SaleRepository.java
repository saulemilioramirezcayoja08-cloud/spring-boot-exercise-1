package com.exercise_1.modules.sale.repository;

import com.exercise_1.modules.sale.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}