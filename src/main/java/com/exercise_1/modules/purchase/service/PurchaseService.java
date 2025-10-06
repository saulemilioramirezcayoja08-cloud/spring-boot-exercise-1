package com.exercise_1.modules.purchase.service;

import com.exercise_1.modules.catalog.repository.ProductRepository;
import com.exercise_1.modules.purchase.dto.PurchaseHistoryResponseDto;
import com.exercise_1.modules.purchase.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public PurchaseHistoryResponseDto getPurchasesHistoryByProduct(Long productId, Integer limit) {

        if (!productRepository.existsById(productId)) {
            return PurchaseHistoryResponseDto.builder()
                    .success(false)
                    .message("Product not found")
                    .data(null)
                    .build();
        }

        if (limit == null || limit <= 0) {
            limit = 5;
        }
        if (limit > 100) {
            limit = 100;
        }

        try {
            List<Object[]> results = purchaseRepository.findConfirmedPurchasesByProductId(productId, limit);

            if (results.isEmpty()) {
                return PurchaseHistoryResponseDto.builder()
                        .success(true)
                        .message("No confirmed purchases found for this product")
                        .data(List.of())
                        .build();
            }

            List<PurchaseHistoryResponseDto.PurchaseHistoryData> history = results.stream()
                    .map(row -> PurchaseHistoryResponseDto.PurchaseHistoryData.builder()
                            .number((String) row[0])
                            .quantity((Integer) row[1])
                            .price((BigDecimal) row[2])
                            .build())
                    .collect(Collectors.toList());

            return PurchaseHistoryResponseDto.builder()
                    .success(true)
                    .message("Purchase history retrieved successfully")
                    .data(history)
                    .build();

        } catch (Exception e) {
            return PurchaseHistoryResponseDto.builder()
                    .success(false)
                    .message("Error retrieving purchase history: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}