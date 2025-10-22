package com.exercise_1.modules.order.service;

import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.repository.UserRepository;
import com.exercise_1.modules.order.dto.OrderAdvanceCreateDto;
import com.exercise_1.modules.order.dto.OrderAdvanceResponseDto;
import com.exercise_1.modules.order.entity.Order;
import com.exercise_1.modules.order.entity.OrderAdvance;
import com.exercise_1.modules.order.repository.OrderAdvanceRepository;
import com.exercise_1.modules.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderAdvanceService {

    private final OrderAdvanceRepository orderAdvanceRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderAdvanceResponseDto create(OrderAdvanceCreateDto dto) {

        Order order = orderRepository.findById(dto.getOrderId()).orElse(null);
        if (order == null) {
            return OrderAdvanceResponseDto.builder()
                    .success(false)
                    .message("Order not found")
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return OrderAdvanceResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        BigDecimal orderTotal = order.getDetails().stream()
                .map(detail -> detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal existingAdvances = orderAdvanceRepository.sumAmountByOrderId(dto.getOrderId());

        BigDecimal newTotal = existingAdvances.add(dto.getAmount());

        if (newTotal.compareTo(orderTotal) > 0) {
            BigDecimal available = orderTotal.subtract(existingAdvances);
            return OrderAdvanceResponseDto.builder()
                    .success(false)
                    .message("The advance amount exceeds the order total. Available: " + available)
                    .data(null)
                    .build();
        }

        try {
            OrderAdvance advance = OrderAdvance.builder()
                    .order(order)
                    .amount(dto.getAmount())
                    .user(dto.getUserId() != null ?
                            User.builder().id(dto.getUserId()).build() : null)
                    .build();

            OrderAdvance savedAdvance = orderAdvanceRepository.save(advance);

            return OrderAdvanceResponseDto.builder()
                    .success(true)
                    .message("Order advance created successfully")
                    .data(OrderAdvanceResponseDto.OrderAdvanceData.builder()
                            .id(savedAdvance.getId())
                            .orderId(order.getId())
                            .amount(savedAdvance.getAmount())
                            .build())
                    .build();

        } catch (Exception e) {
            return OrderAdvanceResponseDto.builder()
                    .success(false)
                    .message("Error creating order advance: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}