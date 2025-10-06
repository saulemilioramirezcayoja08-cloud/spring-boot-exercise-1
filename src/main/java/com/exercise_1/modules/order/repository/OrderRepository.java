package com.exercise_1.modules.order.repository;

import com.exercise_1.modules.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}