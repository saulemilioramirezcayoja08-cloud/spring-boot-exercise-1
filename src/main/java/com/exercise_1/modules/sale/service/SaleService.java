package com.exercise_1.modules.sale.service;

import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.repository.UserRepository;
import com.exercise_1.modules.catalog.repository.ProductRepository;
import com.exercise_1.modules.inventory.entity.Stock;
import com.exercise_1.modules.inventory.repository.StockRepository;

import com.exercise_1.modules.order.entity.OrderStatus;
import com.exercise_1.modules.reservation.entity.Reservation;
import com.exercise_1.modules.reservation.entity.ReservationStatus;
import com.exercise_1.modules.reservation.repository.ReservationRepository;
import com.exercise_1.modules.sale.dto.SaleCancelDto;
import com.exercise_1.modules.sale.dto.SaleConfirmDto;
import com.exercise_1.modules.sale.dto.SaleHistoryResponseDto;
import com.exercise_1.modules.sale.dto.SaleResponseDto;
import com.exercise_1.modules.sale.entity.Sale;
import com.exercise_1.modules.sale.entity.SaleDetail;
import com.exercise_1.modules.sale.entity.SaleStatus;
import com.exercise_1.modules.sale.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ReservationRepository reservationRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public SaleHistoryResponseDto getSalesHistoryByProduct(Long productId, Integer limit) {

        if (!productRepository.existsById(productId)) {
            return SaleHistoryResponseDto.builder()
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
            List<Object[]> results = saleRepository.findConfirmedSalesByProductId(productId, limit);

            if (results.isEmpty()) {
                return SaleHistoryResponseDto.builder()
                        .success(true)
                        .message("No confirmed sales found for this product")
                        .data(List.of())
                        .build();
            }

            List<SaleHistoryResponseDto.SaleHistoryData> history = results.stream()
                    .map(row -> SaleHistoryResponseDto.SaleHistoryData.builder()
                            .number((String) row[0])
                            .quantity((Integer) row[1])
                            .price((BigDecimal) row[2])
                            .build())
                    .collect(Collectors.toList());

            return SaleHistoryResponseDto.builder()
                    .success(true)
                    .message("Sales history retrieved successfully")
                    .data(history)
                    .build();

        } catch (Exception e) {
            return SaleHistoryResponseDto.builder()
                    .success(false)
                    .message("Error retrieving sales history: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Transactional
    public SaleResponseDto cancel(Long saleId, SaleCancelDto dto) {

        Sale sale = saleRepository.findById(saleId).orElse(null);
        if (sale == null) {
            return SaleResponseDto.builder()
                    .success(false)
                    .message("Sale not found")
                    .data(null)
                    .build();
        }

        if (sale.getStatus() != SaleStatus.DRAFT) {
            return SaleResponseDto.builder()
                    .success(false)
                    .message("Only DRAFT sales can be canceled. Current status: " + sale.getStatus())
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return SaleResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        try {
            cancelReservationsForSale(sale.getOrder().getId());

            if (dto.getUserId() != null) {
                sale.setUser(User.builder().id(dto.getUserId()).build());
            }
            sale.setStatus(SaleStatus.CANCELED);
            sale.setNotes(dto.getCancelNotes());
            Sale savedSale = saleRepository.save(sale);

            return SaleResponseDto.builder()
                    .success(true)
                    .message("Sale canceled successfully")
                    .data(SaleResponseDto.SaleData.builder()
                            .id(savedSale.getId())
                            .number(savedSale.getNumber())
                            .status(savedSale.getStatus())
                            .build())
                    .build();

        } catch (Exception e) {
            return SaleResponseDto.builder()
                    .success(false)
                    .message("Error canceling sale: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Transactional
    public SaleResponseDto confirm(Long saleId, SaleConfirmDto dto) {

        Sale sale = saleRepository.findById(saleId).orElse(null);
        if (sale == null) {
            return SaleResponseDto.builder()
                    .success(false)
                    .message("Sale not found")
                    .data(null)
                    .build();
        }

        if (sale.getStatus() != SaleStatus.DRAFT) {
            return SaleResponseDto.builder()
                    .success(false)
                    .message("Only DRAFT sales can be confirmed. Current status: " + sale.getStatus())
                    .data(null)
                    .build();
        }

        if (sale.getOrder().getStatus() != OrderStatus.CONFIRMED) {
            return SaleResponseDto.builder()
                    .success(false)
                    .message("Associated order must be CONFIRMED to confirm sale. Current order status: "
                            + sale.getOrder().getStatus())
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return SaleResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        try {
            String stockValidationError = validateAndReduceStock(sale);
            if (stockValidationError != null) {
                return SaleResponseDto.builder()
                        .success(false)
                        .message(stockValidationError)
                        .data(null)
                        .build();
            }

            consumeReservationsForSale(sale.getOrder().getId());

            if (dto.getUserId() != null) {
                sale.setUser(User.builder().id(dto.getUserId()).build());
            }
            sale.setStatus(SaleStatus.CONFIRMED);
            sale.setNotes(dto.getConfirmNotes());
            Sale savedSale = saleRepository.save(sale);

            return SaleResponseDto.builder()
                    .success(true)
                    .message("Sale confirmed successfully")
                    .data(SaleResponseDto.SaleData.builder()
                            .id(savedSale.getId())
                            .number(savedSale.getNumber())
                            .status(savedSale.getStatus())
                            .build())
                    .build();

        } catch (Exception e) {
            return SaleResponseDto.builder()
                    .success(false)
                    .message("Error confirming sale: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    private String validateAndReduceStock(Sale sale) {
        for (SaleDetail detail : sale.getDetails()) {
            Long productId = detail.getProduct().getId();
            Long warehouseId = sale.getWarehouse().getId();
            Integer quantityToReduce = detail.getQuantity();

            Optional<Stock> stockOpt = stockRepository.findByProductIdAndWarehouseId(productId, warehouseId);

            if (stockOpt.isEmpty()) {
                return "No stock found for product " + productId + " in warehouse " + warehouseId;
            }

            Stock stock = stockOpt.get();

            if (stock.getQuantity() < quantityToReduce) {
                return "Insufficient stock for product " + productId +
                        ". Available: " + stock.getQuantity() + ", Required: " + quantityToReduce;
            }

            int newStockLevel = stock.getQuantity() - quantityToReduce;
            stock.setQuantity(newStockLevel);
            stockRepository.save(stock);
        }

        return null;
    }

    private void consumeReservationsForSale(Long orderId) {
        List<Reservation> activeReservations = reservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.ACTIVE);

        for (Reservation reservation : activeReservations) {
            reservation.setStatus(ReservationStatus.CONSUMED);
        }

        if (!activeReservations.isEmpty()) {
            reservationRepository.saveAll(activeReservations);
        }
    }

    private void cancelReservationsForSale(Long orderId) {
        List<Reservation> activeReservations = reservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.ACTIVE);

        for (Reservation reservation : activeReservations) {
            reservation.setStatus(ReservationStatus.CANCELED);
        }

        if (!activeReservations.isEmpty()) {
            reservationRepository.saveAll(activeReservations);
        }
    }
}