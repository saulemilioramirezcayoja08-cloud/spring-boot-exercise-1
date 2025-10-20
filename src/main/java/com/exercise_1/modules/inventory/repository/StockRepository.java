package com.exercise_1.modules.inventory.repository;

import com.exercise_1.modules.inventory.entity.Stock;
import com.exercise_1.modules.inventory.entity.StockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, StockId> {

    @Query("SELECT s FROM Stock s WHERE s.product.id = :productId AND s.warehouse.id = :warehouseId")
    Optional<Stock> findByProductIdAndWarehouseId(@Param("productId") Long productId,
                                                  @Param("warehouseId") Long warehouseId);

    @Query(value = """
            SELECT 
                w.code AS code,
                COALESCE(s.quantity, 0) AS existence,
                COALESCE(SUM(r.quantity), 0) AS reserved,
                COALESCE(s.quantity, 0) - COALESCE(SUM(r.quantity), 0) AS available
            FROM 
                products p
            CROSS JOIN 
                warehouses w
            LEFT JOIN 
                stocks s 
                    ON s.product_id = p.id 
                   AND s.warehouse_id = w.id
            LEFT JOIN 
                reservations r 
                    ON r.product_id = p.id 
                   AND r.warehouse_id = w.id 
                   AND r.status = 'ACTIVE'
            WHERE 
                p.id = :productId
                AND w.is_active = 1
            GROUP BY 
                p.id, w.id, w.code, s.quantity
            HAVING 
                COALESCE(s.quantity, 0) > 0
            ORDER BY 
                w.code
            """, nativeQuery = true)
    List<Object[]> findStockAvailabilityByProductId(@Param("productId") Long productId);
}