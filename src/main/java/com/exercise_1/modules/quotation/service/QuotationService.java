package com.exercise_1.modules.quotation.service;

import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.repository.UserRepository;
import com.exercise_1.modules.catalog.entity.Payment;
import com.exercise_1.modules.catalog.entity.Product;
import com.exercise_1.modules.catalog.entity.Warehouse;
import com.exercise_1.modules.catalog.repository.PaymentRepository;
import com.exercise_1.modules.catalog.repository.ProductRepository;
import com.exercise_1.modules.catalog.repository.WarehouseRepository;
import com.exercise_1.modules.order.entity.Order;
import com.exercise_1.modules.order.entity.OrderDetail;
import com.exercise_1.modules.order.entity.OrderStatus;
import com.exercise_1.modules.order.repository.OrderRepository;
import com.exercise_1.modules.partner.entity.Customer;
import com.exercise_1.modules.partner.repository.CustomerRepository;
import com.exercise_1.modules.quotation.dto.*;
import com.exercise_1.modules.quotation.entity.Quotation;
import com.exercise_1.modules.quotation.entity.QuotationDetail;
import com.exercise_1.modules.quotation.entity.QuotationStatus;
import com.exercise_1.modules.quotation.repository.QuotationRepository;
import com.exercise_1.modules.reservation.entity.Reservation;
import com.exercise_1.modules.reservation.entity.ReservationStatus;
import com.exercise_1.modules.reservation.repository.ReservationRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public QuotationResponseDto create(QuotationCreateDto dto) {

        if (!customerRepository.existsById(dto.getCustomerId())) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("Customer not found")
                    .data(null)
                    .build();
        }

        if (!warehouseRepository.existsById(dto.getWarehouseId())) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("Warehouse not found")
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        for (QuotationCreateDto.QuotationDetailDto detail : dto.getDetails()) {
            if (!productRepository.existsById(detail.getProductId())) {
                return QuotationResponseDto.builder()
                        .success(false)
                        .message("Product not found")
                        .data(null)
                        .build();
            }
        }

        try {
            Quotation quotation = Quotation.builder()
                    .number(null)
                    .customer(Customer.builder().id(dto.getCustomerId()).build())
                    .warehouse(Warehouse.builder().id(dto.getWarehouseId()).build())
                    .currency(dto.getCurrency())
                    .status(QuotationStatus.DRAFT)
                    .notes(dto.getNotes())
                    .user(dto.getUserId() != null ?
                            User.builder().id(dto.getUserId()).build() : null)
                    .build();

            List<QuotationDetail> details = dto.getDetails().stream()
                    .map(detailDto -> QuotationDetail.builder()
                            .quotation(quotation)
                            .product(Product.builder().id(detailDto.getProductId()).build())
                            .quantity(detailDto.getQuantity())
                            .price(detailDto.getPrice())
                            .notes(detailDto.getNotes())
                            .user(dto.getUserId() != null ?
                                    User.builder().id(dto.getUserId()).build() : null)
                            .build())
                    .collect(Collectors.toList());

            quotation.setDetails(details);

            Quotation savedQuotation = quotationRepository.save(quotation);

            savedQuotation.setNumber(savedQuotation.getId().toString());
            savedQuotation = quotationRepository.save(savedQuotation);

            return QuotationResponseDto.builder()
                    .success(true)
                    .message("Quotation created successfully")
                    .data(QuotationResponseDto.QuotationData.builder()
                            .id(savedQuotation.getId())
                            .number(savedQuotation.getNumber())
                            .status(savedQuotation.getStatus())
                            .build())
                    .build();

        } catch (Exception e) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("Error creating quotation: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Transactional
    public QuotationResponseDto confirm(Long quotationId, QuotationConfirmDto dto) {

        Quotation quotation = quotationRepository.findById(quotationId).orElse(null);
        if (quotation == null) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("Quotation not found")
                    .data(null)
                    .build();
        }

        if (quotation.getStatus() != QuotationStatus.DRAFT) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("Only DRAFT quotations can be confirmed. Current status: " + quotation.getStatus())
                    .data(null)
                    .build();
        }

        if (dto.getPaymentId() != null && !paymentRepository.existsById(dto.getPaymentId())) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("Payment not found")
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        try {
            Order order = createOrderFromQuotation(quotation, dto);
            Order savedOrder = orderRepository.save(order);

            savedOrder.setNumber(savedOrder.getId().toString());
            savedOrder = orderRepository.save(savedOrder);

            createReservationsForOrder(savedOrder, dto.getUserId());

            if (dto.getUserId() != null) {
                quotation.setUser(User.builder().id(dto.getUserId()).build());
            }
            quotation.setStatus(QuotationStatus.CONFIRMED);
            quotation.setNotes(dto.getConfirmNotes());
            Quotation savedQuotation = quotationRepository.save(quotation);

            return QuotationResponseDto.builder()
                    .success(true)
                    .message("Quotation confirmed successfully")
                    .data(QuotationResponseDto.QuotationData.builder()
                            .id(savedQuotation.getId())
                            .number(savedQuotation.getNumber())
                            .status(savedQuotation.getStatus())
                            .build())
                    .build();

        } catch (Exception e) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("Error confirming quotation: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Transactional
    public QuotationResponseDto cancel(Long quotationId, QuotationCancelDto dto) {

        Quotation quotation = quotationRepository.findById(quotationId).orElse(null);
        if (quotation == null) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("Quotation not found")
                    .data(null)
                    .build();
        }

        if (quotation.getStatus() != QuotationStatus.DRAFT) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("Only DRAFT quotations can be canceled. Current status: " + quotation.getStatus())
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        try {
            if (dto.getUserId() != null) {
                quotation.setUser(User.builder().id(dto.getUserId()).build());
            }

            quotation.setStatus(QuotationStatus.CANCELED);
            quotation.setNotes(dto.getCancelNotes());
            Quotation savedQuotation = quotationRepository.save(quotation);

            return QuotationResponseDto.builder()
                    .success(true)
                    .message("Quotation canceled successfully")
                    .data(QuotationResponseDto.QuotationData.builder()
                            .id(savedQuotation.getId())
                            .number(savedQuotation.getNumber())
                            .status(savedQuotation.getStatus())
                            .build())
                    .build();

        } catch (Exception e) {
            return QuotationResponseDto.builder()
                    .success(false)
                    .message("Error canceling quotation: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public QuotationSearchResponseDto search(String number, String status, String username,
                                             String dateFrom, String dateTo,
                                             Integer page, Integer size) {

        int paramCount = 0;
        if (number != null) paramCount++;
        if (status != null) paramCount++;
        if (username != null) paramCount++;
        if (dateFrom != null || dateTo != null) paramCount++;

        if (paramCount == 0) {
            return QuotationSearchResponseDto.builder()
                    .success(false)
                    .message("At least one search parameter is required")
                    .data(null)
                    .pagination(null)
                    .build();
        }

        if (paramCount > 1) {
            return QuotationSearchResponseDto.builder()
                    .success(false)
                    .message("Only one search parameter is allowed at a time")
                    .data(null)
                    .pagination(null)
                    .build();
        }

        if (page != null && page < 0) {
            return QuotationSearchResponseDto.builder()
                    .success(false)
                    .message("Page number must be greater than or equal to 0")
                    .data(null)
                    .pagination(null)
                    .build();
        }

        if (size != null && (size < 1 || size > 100)) {
            return QuotationSearchResponseDto.builder()
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
            Page<Quotation> quotationPage;

            if (number != null) {
                quotationPage = quotationRepository.findByNumberContainingIgnoreCase(number, pageable);
            } else if (status != null) {
                QuotationStatus quotationStatus;
                try {
                    quotationStatus = QuotationStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return QuotationSearchResponseDto.builder()
                            .success(false)
                            .message("Invalid status value.")
                            .data(null)
                            .pagination(null)
                            .build();
                }
                quotationPage = quotationRepository.findByStatus(quotationStatus, pageable);
            } else if (username != null) {
                quotationPage = quotationRepository.findByUserUsernameContainingIgnoreCase(username, pageable);
            } else {
                if (dateFrom == null || dateTo == null) {
                    return QuotationSearchResponseDto.builder()
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
                    return QuotationSearchResponseDto.builder()
                            .success(false)
                            .message("Invalid date format. Use ISO-8601 format (e.g., 2025-10-01T00:00:00Z)")
                            .data(null)
                            .pagination(null)
                            .build();
                }

                if (instantFrom.isAfter(instantTo)) {
                    return QuotationSearchResponseDto.builder()
                            .success(false)
                            .message("dateFrom must be before dateTo")
                            .data(null)
                            .pagination(null)
                            .build();
                }

                quotationPage = quotationRepository.findByCreatedAtBetween(instantFrom, instantTo, pageable);
            }

            if (quotationPage.isEmpty()) {
                return QuotationSearchResponseDto.builder()
                        .success(true)
                        .message("No quotations found")
                        .data(List.of())
                        .pagination(QuotationSearchResponseDto.PaginationMetadata.builder()
                                .currentPage(pageNumber)
                                .totalPages(0)
                                .totalElements(0L)
                                .pageSize(pageSize)
                                .hasNext(false)
                                .hasPrevious(false)
                                .build())
                        .build();
            }

            List<QuotationSearchResponseDto.QuotationData> quotationDataList = quotationPage.getContent().stream()
                    .map(quotation -> {
                        BigDecimal totalAmount = quotation.getDetails().stream()
                                .map(detail -> detail.getPrice().multiply(new BigDecimal(detail.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        return QuotationSearchResponseDto.QuotationData.builder()
                                .id(quotation.getId())
                                .number(quotation.getNumber())
                                .status(quotation.getStatus())
                                .username(quotation.getUser() != null ? quotation.getUser().getUsername() : null)
                                .totalAmount(totalAmount)
                                .itemCount((long) quotation.getDetails().size())
                                .createdAt(quotation.getCreatedAt())
                                .build();
                    })
                    .collect(Collectors.toList());

            return QuotationSearchResponseDto.builder()
                    .success(true)
                    .message("Quotations found successfully")
                    .data(quotationDataList)
                    .pagination(QuotationSearchResponseDto.PaginationMetadata.builder()
                            .currentPage(quotationPage.getNumber())
                            .totalPages(quotationPage.getTotalPages())
                            .totalElements(quotationPage.getTotalElements())
                            .pageSize(quotationPage.getSize())
                            .hasNext(quotationPage.hasNext())
                            .hasPrevious(quotationPage.hasPrevious())
                            .build())
                    .build();

        } catch (Exception e) {
            return QuotationSearchResponseDto.builder()
                    .success(false)
                    .message("Error searching quotations: " + e.getMessage())
                    .data(null)
                    .pagination(null)
                    .build();
        }
    }

    private Order createOrderFromQuotation(Quotation quotation, QuotationConfirmDto dto) {
        Order order = Order.builder()
                .number(null)
                .customer(quotation.getCustomer())
                .warehouse(quotation.getWarehouse())
                .currency(quotation.getCurrency())
                .payment(dto.getPaymentId() != null ?
                        Payment.builder().id(dto.getPaymentId()).build() : null)
                .quotation(quotation)
                .status(OrderStatus.DRAFT)
                .notes(null)
                .user(dto.getUserId() != null ?
                        User.builder().id(dto.getUserId()).build() : null)
                .build();

        List<OrderDetail> details = quotation.getDetails().stream()
                .map(quotationDetail -> OrderDetail.builder()
                        .order(order)
                        .product(quotationDetail.getProduct())
                        .quantity(quotationDetail.getQuantity())
                        .price(quotationDetail.getPrice())
                        .notes(quotationDetail.getNotes())
                        .user(dto.getUserId() != null ?
                                User.builder().id(dto.getUserId()).build() : null)
                        .build())
                .collect(Collectors.toList());

        order.setDetails(details);
        return order;
    }

    private void createReservationsForOrder(Order order, Long userId) {
        List<Reservation> reservations = order.getDetails().stream()
                .map(orderDetail -> Reservation.builder()
                        .order(order)
                        .product(orderDetail.getProduct())
                        .warehouse(order.getWarehouse())
                        .quantity(orderDetail.getQuantity())
                        .status(ReservationStatus.ACTIVE)
                        .user(userId != null ?
                                User.builder().id(userId).build() : null)
                        .build())
                .collect(Collectors.toList());

        reservationRepository.saveAll(reservations);
    }
}