package com.exercise_1.modules.purchase.controller;

import com.exercise_1.modules.purchase.dto.*;
import com.exercise_1.modules.purchase.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<PurchaseResponseDto> createPurchase(@Valid @RequestBody PurchaseCreateDto dto) {
        PurchaseResponseDto response = purchaseService.create(dto);

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
    public ResponseEntity<PurchaseResponseDto> cancelPurchase(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseCancelDto dto) {
        PurchaseResponseDto response = purchaseService.cancel(id, dto);

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
    public ResponseEntity<PurchaseResponseDto> confirmPurchase(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseConfirmDto dto) {
        PurchaseResponseDto response = purchaseService.confirm(id, dto);

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
    public ResponseEntity<PurchaseSearchResponseDto> search(
            @RequestParam(required = false) String number,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        PurchaseSearchResponseDto response = purchaseService.search(number, status, username, dateFrom, dateTo, page, size);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/history/{productId}")
    public ResponseEntity<PurchaseHistoryResponseDto> getPurchasesHistory(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "5") Integer limit) {

        PurchaseHistoryResponseDto response = purchaseService.getPurchasesHistoryByProduct(productId, limit);

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

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDetailResponseDto> getPurchaseById(@PathVariable Long id) {
        PurchaseDetailResponseDto response = purchaseService.getPurchaseById(id);

        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}