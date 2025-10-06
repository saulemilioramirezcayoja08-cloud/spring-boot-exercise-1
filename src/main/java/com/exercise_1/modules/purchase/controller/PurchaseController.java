package com.exercise_1.modules.purchase.controller;

import com.exercise_1.modules.purchase.dto.PurchaseHistoryResponseDto;
import com.exercise_1.modules.purchase.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

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
}
