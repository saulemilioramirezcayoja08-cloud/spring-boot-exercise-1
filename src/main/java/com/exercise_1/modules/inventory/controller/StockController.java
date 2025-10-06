package com.exercise_1.modules.inventory.controller;

import com.exercise_1.modules.inventory.dto.StockAvailabilityResponseDto;
import com.exercise_1.modules.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/availability/{productId}")
    public ResponseEntity<StockAvailabilityResponseDto> getAvailability(@PathVariable Long productId) {
        StockAvailabilityResponseDto response = stockService.getAvailability(productId);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        }
    }
}