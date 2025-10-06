package com.exercise_1.modules.order.controller;

import com.exercise_1.modules.order.dto.*;
import com.exercise_1.modules.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderCreateDto dto) {
        OrderResponseDto response = orderService.create(dto);

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

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long id, @Valid @RequestBody OrderCancelDto dto) {
        OrderResponseDto response = orderService.cancel(id, dto);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<OrderResponseDto> confirmOrder(@PathVariable Long id, @Valid @RequestBody OrderConfirmDto dto) {
        OrderResponseDto response = orderService.confirm(id, dto);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }
    }
}