package com.exercise_1.modules.order.controller;

import com.exercise_1.modules.order.dto.OrderAdvanceCreateDto;
import com.exercise_1.modules.order.dto.OrderAdvanceResponseDto;
import com.exercise_1.modules.order.service.OrderAdvanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order-advances")
@RequiredArgsConstructor
public class OrderAdvanceController {

    private final OrderAdvanceService orderAdvanceService;

    @PostMapping
    public ResponseEntity<OrderAdvanceResponseDto> createOrderAdvance(@Valid @RequestBody OrderAdvanceCreateDto dto) {
        OrderAdvanceResponseDto response = orderAdvanceService.create(dto);

        if (response.getSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }
    }
}
