package com.exercise_1.modules.sale.service;

import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.repository.UserRepository;
import com.exercise_1.modules.catalog.repository.ProductRepository;
import com.exercise_1.modules.inventory.entity.Stock;
import com.exercise_1.modules.inventory.repository.StockRepository;

import com.exercise_1.modules.order.entity.OrderStatus;
import com.exercise_1.modules.order.repository.OrderAdvanceRepository;
import com.exercise_1.modules.reservation.entity.Reservation;
import com.exercise_1.modules.reservation.entity.ReservationStatus;
import com.exercise_1.modules.reservation.repository.ReservationRepository;
import com.exercise_1.modules.sale.dto.*;
import com.exercise_1.modules.sale.entity.Sale;
import com.exercise_1.modules.sale.entity.SaleDetail;
import com.exercise_1.modules.sale.entity.SaleStatus;
import com.exercise_1.modules.sale.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
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
    private final OrderAdvanceRepository orderAdvanceRepository;

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

    @Transactional(readOnly = true)
    public SaleSearchResponseDto search(String number, String status, String username,
                                        String dateFrom, String dateTo,
                                        Integer page, Integer size) {

        int paramCount = 0;
        if (number != null) paramCount++;
        if (status != null) paramCount++;
        if (username != null) paramCount++;
        if (dateFrom != null || dateTo != null) paramCount++;

        if (paramCount == 0) {
            return SaleSearchResponseDto.builder()
                    .success(false)
                    .message("At least one search parameter is required")
                    .data(null)
                    .pagination(null)
                    .build();
        }

        if (paramCount > 1) {
            return SaleSearchResponseDto.builder()
                    .success(false)
                    .message("Only one search parameter is allowed at a time")
                    .data(null)
                    .pagination(null)
                    .build();
        }

        if (page != null && page < 0) {
            return SaleSearchResponseDto.builder()
                    .success(false)
                    .message("Page number must be greater than or equal to 0")
                    .data(null)
                    .pagination(null)
                    .build();
        }

        if (size != null && (size < 1 || size > 100)) {
            return SaleSearchResponseDto.builder()
                    .success(false)
                    .message("Page size must be between 1 and 100")
                    .data(null)
                    .pagination(null)
                    .build();
        }

        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        try {
            Page<Sale> salePage;

            if (number != null) {
                salePage = saleRepository.findByNumberContainingIgnoreCase(number, pageable);
            } else if (status != null) {
                SaleStatus saleStatus;
                try {
                    saleStatus = SaleStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return SaleSearchResponseDto.builder()
                            .success(false)
                            .message("Invalid status value.")
                            .data(null)
                            .pagination(null)
                            .build();
                }
                salePage = saleRepository.findByStatus(saleStatus, pageable);
            } else if (username != null) {
                salePage = saleRepository.findByUserUsernameContainingIgnoreCase(username, pageable);
            } else {
                if (dateFrom == null || dateTo == null) {
                    return SaleSearchResponseDto.builder()
                            .success(false)
                            .message("Both dateFrom and dateTo are required for date range search")
                            .data(null)
                            .pagination(null)
                            .build();
                }

                Instant instantFrom;
                Instant instantTo;
                try {
                    instantFrom = Instant.parse(dateFrom);
                    instantTo = Instant.parse(dateTo);
                } catch (Exception e) {
                    return SaleSearchResponseDto.builder()
                            .success(false)
                            .message("Invalid date format. Use ISO-8601 format (e.g., 2025-10-01T00:00:00Z)")
                            .data(null)
                            .pagination(null)
                            .build();
                }

                if (instantFrom.isAfter(instantTo)) {
                    return SaleSearchResponseDto.builder()
                            .success(false)
                            .message("dateFrom must be before dateTo")
                            .data(null)
                            .pagination(null)
                            .build();
                }

                salePage = saleRepository.findByCreatedAtBetween(instantFrom, instantTo, pageable);
            }

            if (salePage.isEmpty()) {
                return SaleSearchResponseDto.builder()
                        .success(true)
                        .message("No sales found")
                        .data(List.of())
                        .pagination(SaleSearchResponseDto.PaginationMetadata.builder()
                                .currentPage(pageNumber)
                                .totalPages(0)
                                .totalElements(0L)
                                .pageSize(pageSize)
                                .hasNext(false)
                                .hasPrevious(false)
                                .build())
                        .build();
            }

            List<SaleSearchResponseDto.SaleData> saleDataList = salePage.getContent().stream()
                    .map(sale -> {
                        BigDecimal saleTotalAmount = sale.getDetails().stream()
                                .map(detail -> detail.getPrice().multiply(new BigDecimal(detail.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal orderTotalAdvances = orderAdvanceRepository.sumAmountByOrderId(sale.getOrder().getId());

                        return SaleSearchResponseDto.SaleData.builder()
                                .id(sale.getId())
                                .number(sale.getNumber())
                                .status(sale.getStatus())
                                .customerName(sale.getCustomer().getName())
                                .warehouseName(sale.getWarehouse().getName())
                                .username(sale.getUser() != null ? sale.getUser().getUsername() : null)
                                .currency(sale.getCurrency())
                                .saleTotalAmount(saleTotalAmount)
                                .itemCount((long) sale.getDetails().size())
                                .paymentName(sale.getPayment() != null ? sale.getPayment().getName() : null)
                                .orderId(sale.getOrder().getId())
                                .orderTotalAdvances(orderTotalAdvances)
                                .createdAt(sale.getCreatedAt())
                                .updatedAt(sale.getUpdatedAt())
                                .build();
                    })
                    .collect(Collectors.toList());

            return SaleSearchResponseDto.builder()
                    .success(true)
                    .message("Sales found successfully")
                    .data(saleDataList)
                    .pagination(SaleSearchResponseDto.PaginationMetadata.builder()
                            .currentPage(salePage.getNumber())
                            .totalPages(salePage.getTotalPages())
                            .totalElements(salePage.getTotalElements())
                            .pageSize(salePage.getSize())
                            .hasNext(salePage.hasNext())
                            .hasPrevious(salePage.hasPrevious())
                            .build())
                    .build();

        } catch (Exception e) {
            return SaleSearchResponseDto.builder()
                    .success(false)
                    .message("Error searching sales: " + e.getMessage())
                    .data(null)
                    .pagination(null)
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
