package com.exercise_1.modules.purchase.service;

import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.repository.UserRepository;
import com.exercise_1.modules.catalog.entity.Payment;
import com.exercise_1.modules.catalog.entity.Product;
import com.exercise_1.modules.catalog.entity.Warehouse;
import com.exercise_1.modules.catalog.repository.PaymentRepository;
import com.exercise_1.modules.catalog.repository.ProductRepository;
import com.exercise_1.modules.catalog.repository.WarehouseRepository;
import com.exercise_1.modules.inventory.entity.Stock;
import com.exercise_1.modules.inventory.entity.StockId;
import com.exercise_1.modules.inventory.repository.StockRepository;
import com.exercise_1.modules.partner.entity.Supplier;
import com.exercise_1.modules.partner.repository.SupplierRepository;
import com.exercise_1.modules.purchase.dto.*;
import com.exercise_1.modules.purchase.entity.Purchase;
import com.exercise_1.modules.purchase.entity.PurchaseDetail;
import com.exercise_1.modules.purchase.entity.PurchaseStatus;
import com.exercise_1.modules.purchase.repository.PurchaseRepository;
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
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    @Transactional
    public PurchaseResponseDto create(PurchaseCreateDto dto) {

        if (!supplierRepository.existsById(dto.getSupplierId())) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("Supplier not found")
                    .data(null)
                    .build();
        }

        if (!warehouseRepository.existsById(dto.getWarehouseId())) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("Warehouse not found")
                    .data(null)
                    .build();
        }

        if (dto.getPaymentId() != null && !paymentRepository.existsById(dto.getPaymentId())) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("Payment not found")
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        for (PurchaseCreateDto.PurchaseDetailDto detail : dto.getDetails()) {
            if (!productRepository.existsById(detail.getProductId())) {
                return PurchaseResponseDto.builder()
                        .success(false)
                        .message("Product not found")
                        .data(null)
                        .build();
            }
        }

        try {
            Purchase purchase = Purchase.builder()
                    .number(null)
                    .supplier(Supplier.builder().id(dto.getSupplierId()).build())
                    .warehouse(Warehouse.builder().id(dto.getWarehouseId()).build())
                    .currency(dto.getCurrency())
                    .payment(dto.getPaymentId() != null ?
                            Payment.builder().id(dto.getPaymentId()).build() : null)
                    .status(PurchaseStatus.DRAFT)
                    .notes(dto.getNotes())
                    .user(dto.getUserId() != null ?
                            User.builder().id(dto.getUserId()).build() : null)
                    .build();

            List<PurchaseDetail> details = dto.getDetails().stream()
                    .map(detailDto -> PurchaseDetail.builder()
                            .purchase(purchase)
                            .product(Product.builder().id(detailDto.getProductId()).build())
                            .quantity(detailDto.getQuantity())
                            .price(detailDto.getPrice())
                            .notes(detailDto.getNotes())
                            .user(dto.getUserId() != null ?
                                    User.builder().id(dto.getUserId()).build() : null)
                            .build())
                    .collect(Collectors.toList());

            purchase.setDetails(details);

            Purchase savedPurchase = purchaseRepository.save(purchase);

            savedPurchase.setNumber(savedPurchase.getId().toString());
            savedPurchase = purchaseRepository.save(savedPurchase);

            return PurchaseResponseDto.builder()
                    .success(true)
                    .message("Purchase created successfully")
                    .data(PurchaseResponseDto.PurchaseData.builder()
                            .id(savedPurchase.getId())
                            .number(savedPurchase.getNumber())
                            .status(savedPurchase.getStatus())
                            .build())
                    .build();

        } catch (Exception e) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("Error creating purchase: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Transactional
    public PurchaseResponseDto cancel(Long purchaseId, PurchaseCancelDto dto) {

        Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
        if (purchase == null) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("Purchase not found")
                    .data(null)
                    .build();
        }

        if (purchase.getStatus() != PurchaseStatus.DRAFT) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("Only DRAFT purchases can be canceled. Current status: " + purchase.getStatus())
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        try {
            if (dto.getUserId() != null) {
                purchase.setUser(User.builder().id(dto.getUserId()).build());
            }
            purchase.setStatus(PurchaseStatus.CANCELED);
            purchase.setNotes(dto.getCancelNotes());
            Purchase savedPurchase = purchaseRepository.save(purchase);

            return PurchaseResponseDto.builder()
                    .success(true)
                    .message("Purchase canceled successfully")
                    .data(PurchaseResponseDto.PurchaseData.builder()
                            .id(savedPurchase.getId())
                            .number(savedPurchase.getNumber())
                            .status(savedPurchase.getStatus())
                            .build())
                    .build();

        } catch (Exception e) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("Error canceling purchase: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Transactional
    public PurchaseResponseDto confirm(Long purchaseId, PurchaseConfirmDto dto) {

        Purchase purchase = purchaseRepository.findById(purchaseId).orElse(null);
        if (purchase == null) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("Purchase not found")
                    .data(null)
                    .build();
        }

        if (purchase.getStatus() != PurchaseStatus.DRAFT) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("Only DRAFT purchases can be confirmed. Current status: " + purchase.getStatus())
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        try {
            String stockValidationError = validateAndIncreaseStock(purchase);
            if (stockValidationError != null) {
                return PurchaseResponseDto.builder()
                        .success(false)
                        .message(stockValidationError)
                        .data(null)
                        .build();
            }

            if (dto.getUserId() != null) {
                purchase.setUser(User.builder().id(dto.getUserId()).build());
            }
            purchase.setStatus(PurchaseStatus.CONFIRMED);
            purchase.setNotes(dto.getConfirmNotes());
            Purchase savedPurchase = purchaseRepository.save(purchase);

            return PurchaseResponseDto.builder()
                    .success(true)
                    .message("Purchase confirmed successfully")
                    .data(PurchaseResponseDto.PurchaseData.builder()
                            .id(savedPurchase.getId())
                            .number(savedPurchase.getNumber())
                            .status(savedPurchase.getStatus())
                            .build())
                    .build();

        } catch (Exception e) {
            return PurchaseResponseDto.builder()
                    .success(false)
                    .message("Error confirming purchase: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public PurchaseSearchResponseDto search(String number, String status, String username,
                                            String dateFrom, String dateTo,
                                            Integer page, Integer size) {

        int paramCount = 0;
        if (number != null) paramCount++;
        if (status != null) paramCount++;
        if (username != null) paramCount++;
        if (dateFrom != null || dateTo != null) paramCount++;

        if (paramCount == 0) {
            return PurchaseSearchResponseDto.builder()
                    .success(false)
                    .message("At least one search parameter is required")
                    .data(null)
                    .pagination(null)
                    .build();
        }

        if (paramCount > 1) {
            return PurchaseSearchResponseDto.builder()
                    .success(false)
                    .message("Only one search parameter is allowed at a time")
                    .data(null)
                    .pagination(null)
                    .build();
        }

        if (page != null && page < 0) {
            return PurchaseSearchResponseDto.builder()
                    .success(false)
                    .message("Page number must be greater than or equal to 0")
                    .data(null)
                    .pagination(null)
                    .build();
        }

        if (size != null && (size < 1 || size > 100)) {
            return PurchaseSearchResponseDto.builder()
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
            Page<Purchase> purchasePage;

            if (number != null) {
                purchasePage = purchaseRepository.findByNumberContainingIgnoreCase(number, pageable);
            } else if (status != null) {
                PurchaseStatus purchaseStatus;
                try {
                    purchaseStatus = PurchaseStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return PurchaseSearchResponseDto.builder()
                            .success(false)
                            .message("Invalid status value.")
                            .data(null)
                            .pagination(null)
                            .build();
                }
                purchasePage = purchaseRepository.findByStatus(purchaseStatus, pageable);
            } else if (username != null) {
                purchasePage = purchaseRepository.findByUserUsernameContainingIgnoreCase(username, pageable);
            } else {
                if (dateFrom == null || dateTo == null) {
                    return PurchaseSearchResponseDto.builder()
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
                    return PurchaseSearchResponseDto.builder()
                            .success(false)
                            .message("Invalid date format. Use ISO-8601 format (e.g., 2025-10-01T00:00:00Z)")
                            .data(null)
                            .pagination(null)
                            .build();
                }

                if (instantFrom.isAfter(instantTo)) {
                    return PurchaseSearchResponseDto.builder()
                            .success(false)
                            .message("dateFrom must be before dateTo")
                            .data(null)
                            .pagination(null)
                            .build();
                }

                purchasePage = purchaseRepository.findByCreatedAtBetween(instantFrom, instantTo, pageable);
            }

            if (purchasePage.isEmpty()) {
                return PurchaseSearchResponseDto.builder()
                        .success(true)
                        .message("No purchases found")
                        .data(List.of())
                        .pagination(PurchaseSearchResponseDto.PaginationMetadata.builder()
                                .currentPage(pageNumber)
                                .totalPages(0)
                                .totalElements(0L)
                                .pageSize(pageSize)
                                .hasNext(false)
                                .hasPrevious(false)
                                .build())
                        .build();
            }

            List<PurchaseSearchResponseDto.PurchaseData> purchaseDataList = purchasePage.getContent().stream()
                    .map(purchase -> {
                        BigDecimal totalAmount = purchase.getDetails().stream()
                                .map(detail -> detail.getPrice().multiply(new BigDecimal(detail.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        return PurchaseSearchResponseDto.PurchaseData.builder()
                                .id(purchase.getId())
                                .number(purchase.getNumber())
                                .status(purchase.getStatus())
                                .supplierName(purchase.getSupplier().getName())
                                .warehouseName(purchase.getWarehouse().getName())
                                .username(purchase.getUser() != null ? purchase.getUser().getUsername() : null)
                                .currency(purchase.getCurrency())
                                .totalAmount(totalAmount)
                                .itemCount((long) purchase.getDetails().size())
                                .paymentName(purchase.getPayment() != null ? purchase.getPayment().getName() : null)
                                .createdAt(purchase.getCreatedAt())
                                .updatedAt(purchase.getUpdatedAt())
                                .build();
                    })
                    .collect(Collectors.toList());

            return PurchaseSearchResponseDto.builder()
                    .success(true)
                    .message("Purchases found successfully")
                    .data(purchaseDataList)
                    .pagination(PurchaseSearchResponseDto.PaginationMetadata.builder()
                            .currentPage(purchasePage.getNumber())
                            .totalPages(purchasePage.getTotalPages())
                            .totalElements(purchasePage.getTotalElements())
                            .pageSize(purchasePage.getSize())
                            .hasNext(purchasePage.hasNext())
                            .hasPrevious(purchasePage.hasPrevious())
                            .build())
                    .build();

        } catch (Exception e) {
            return PurchaseSearchResponseDto.builder()
                    .success(false)
                    .message("Error searching purchases: " + e.getMessage())
                    .data(null)
                    .pagination(null)
                    .build();
        }
    }

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

    private String validateAndIncreaseStock(Purchase purchase) {
        for (PurchaseDetail detail : purchase.getDetails()) {
            Long productId = detail.getProduct().getId();
            Long warehouseId = purchase.getWarehouse().getId();
            Integer quantityToAdd = detail.getQuantity();

            Optional<Stock> stockOpt = stockRepository.findByProductIdAndWarehouseId(productId, warehouseId);

            Stock stock;

            if (stockOpt.isEmpty()) {
                StockId stockId = new StockId(productId, warehouseId);

                stock = Stock.builder()
                        .id(stockId)
                        .product(Product.builder().id(productId).build())
                        .warehouse(Warehouse.builder().id(warehouseId).build())
                        .quantity(quantityToAdd)
                        .user(purchase.getUser())
                        .build();
            } else {
                stock = stockOpt.get();
                int newStockLevel = stock.getQuantity() + quantityToAdd;
                stock.setQuantity(newStockLevel);

                if (purchase.getUser() != null) {
                    stock.setUser(purchase.getUser());
                }
            }

            stockRepository.save(stock);
        }

        return null;
    }
}