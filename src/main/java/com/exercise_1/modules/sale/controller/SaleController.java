package com.exercise_1.modules.sale.controller;

import com.exercise_1.modules.sale.dto.*;
import com.exercise_1.modules.sale.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @GetMapping("/history/{productId}")
    public ResponseEntity<SaleHistoryResponseDto> getSalesHistory(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "5") Integer limit) {

        SaleHistoryResponseDto response = saleService.getSalesHistoryByProduct(productId, limit);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            if (response.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<SaleResponseDto> cancelSale(
            @PathVariable Long id,
            @Valid @RequestBody SaleCancelDto dto) {
        SaleResponseDto response = saleService.cancel(id, dto);

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
    public ResponseEntity<SaleResponseDto> confirmSale(
            @PathVariable Long id,
            @Valid @RequestBody SaleConfirmDto dto) {
        SaleResponseDto response = saleService.confirm(id, dto);

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

    @GetMapping("/search")
    public ResponseEntity<SaleSearchResponseDto> search(
            @RequestParam(required = false) String number,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        SaleSearchResponseDto response = saleService.search(number, status, username, dateFrom, dateTo, page, size);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}