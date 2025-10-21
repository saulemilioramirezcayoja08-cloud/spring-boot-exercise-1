package com.exercise_1.modules.catalog.service;

import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.repository.UserRepository;
import com.exercise_1.modules.catalog.dto.PaymentCreateDto;
import com.exercise_1.modules.catalog.dto.PaymentResponseDto;
import com.exercise_1.modules.catalog.entity.Payment;
import com.exercise_1.modules.catalog.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentResponseDto create(PaymentCreateDto dto) {

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return PaymentResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        if (paymentRepository.existsByCode(dto.getCode())) {
            return PaymentResponseDto.builder()
                    .success(false)
                    .message("Payment with code " + dto.getCode() + " already exists")
                    .data(null)
                    .build();
        }

        try {
            Payment payment = Payment.builder()
                    .code(dto.getCode())
                    .name(dto.getName())
                    .active(true)
                    .user(dto.getUserId() != null ?
                            User.builder().id(dto.getUserId()).build() : null)
                    .build();

            Payment savedPayment = paymentRepository.save(payment);

            return PaymentResponseDto.builder()
                    .success(true)
                    .message("Payment created successfully")
                    .data(PaymentResponseDto.PaymentData.builder()
                            .id(savedPayment.getId())
                            .code(savedPayment.getCode())
                            .name(savedPayment.getName())
                            .build())
                    .build();

        } catch (Exception e) {
            return PaymentResponseDto.builder()
                    .success(false)
                    .message("Error creating payment: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}