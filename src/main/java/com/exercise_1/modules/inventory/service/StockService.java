package com.exercise_1.modules.inventory.service;

import com.exercise_1.modules.catalog.repository.ProductRepository;
import com.exercise_1.modules.inventory.dto.StockAvailabilityResponseDto;
import com.exercise_1.modules.inventory.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public StockAvailabilityResponseDto getAvailability(Long productId) {

        if (!productRepository.existsById(productId)) {
            return StockAvailabilityResponseDto.builder()
                    .success(false)
                    .message("Product not found")
                    .data(null)
                    .build();
        }

        try {
            List<Object[]> results = stockRepository.findStockAvailabilityByProductId(productId);

            if (results.isEmpty()) {
                return StockAvailabilityResponseDto.builder()
                        .success(true)
                        .message("No active reservations found for this product")
                        .data(List.of())
                        .build();
            }

            List<StockAvailabilityResponseDto.WarehouseStock> warehouseStocks = results.stream()
                    .map(row -> StockAvailabilityResponseDto.WarehouseStock.builder()
                            .code((String) row[0])
                            .existence(((Number) row[1]).intValue())
                            .reserved(((Number) row[2]).intValue())
                            .available(((Number) row[3]).intValue())
                            .build())
                    .collect(Collectors.toList());

            return StockAvailabilityResponseDto.builder()
                    .success(true)
                    .message("Stock availability retrieved successfully")
                    .data(warehouseStocks)
                    .build();

        } catch (Exception e) {
            return StockAvailabilityResponseDto.builder()
                    .success(false)
                    .message("Error retrieving stock availability: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }
}