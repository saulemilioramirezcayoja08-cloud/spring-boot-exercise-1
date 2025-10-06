package com.exercise_1.modules.order.repository;

import com.exercise_1.modules.order.entity.OrderAdvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface OrderAdvanceRepository extends JpaRepository<OrderAdvance, Long> {

    @Query("SELECT COALESCE(SUM(oa.amount), 0) FROM OrderAdvance oa WHERE oa.order.id = :orderId")
    BigDecimal sumAmountByOrderId(@Param("orderId") Long orderId);
}