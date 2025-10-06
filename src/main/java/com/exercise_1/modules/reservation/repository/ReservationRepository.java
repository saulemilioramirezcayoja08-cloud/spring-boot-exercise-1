package com.exercise_1.modules.reservation.repository;

import com.exercise_1.modules.reservation.entity.Reservation;
import com.exercise_1.modules.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByOrderIdAndStatus(Long orderId, ReservationStatus status);
}