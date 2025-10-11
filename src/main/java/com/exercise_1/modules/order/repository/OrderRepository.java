package com.exercise_1.modules.order.repository;

import com.exercise_1.modules.order.entity.Order;
import com.exercise_1.modules.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.customer c " +
            "LEFT JOIN FETCH o.warehouse w " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.payment p " +
            "LEFT JOIN FETCH o.details od " +
            "WHERE LOWER(o.number) LIKE LOWER(CONCAT('%', :number, '%'))")
    Page<Order> findByNumberContainingIgnoreCase(@Param("number") String number, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.customer c " +
            "LEFT JOIN FETCH o.warehouse w " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.payment p " +
            "LEFT JOIN FETCH o.details od " +
            "WHERE o.status = :status")
    Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.customer c " +
            "LEFT JOIN FETCH o.warehouse w " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.payment p " +
            "LEFT JOIN FETCH o.details od " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    Page<Order> findByUserUsernameContainingIgnoreCase(@Param("username") String username, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.customer c " +
            "LEFT JOIN FETCH o.warehouse w " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.payment p " +
            "LEFT JOIN FETCH o.details od " +
            "WHERE o.createdAt BETWEEN :dateFrom AND :dateTo")
    Page<Order> findByCreatedAtBetween(@Param("dateFrom") Instant dateFrom,
                                       @Param("dateTo") Instant dateTo,
                                       Pageable pageable);
}