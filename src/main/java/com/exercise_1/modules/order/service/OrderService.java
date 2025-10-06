package com.exercise_1.modules.order.service;

import com.exercise_1.modules.auth.entity.User;
import com.exercise_1.modules.auth.repository.UserRepository;
import com.exercise_1.modules.catalog.entity.Payment;
import com.exercise_1.modules.catalog.entity.Product;
import com.exercise_1.modules.catalog.entity.Warehouse;
import com.exercise_1.modules.catalog.repository.PaymentRepository;
import com.exercise_1.modules.catalog.repository.ProductRepository;
import com.exercise_1.modules.catalog.repository.WarehouseRepository;
import com.exercise_1.modules.order.dto.*;
import com.exercise_1.modules.order.entity.Order;
import com.exercise_1.modules.order.entity.OrderDetail;
import com.exercise_1.modules.order.entity.OrderStatus;
import com.exercise_1.modules.order.repository.OrderRepository;
import com.exercise_1.modules.partner.entity.Customer;
import com.exercise_1.modules.partner.repository.CustomerRepository;
import com.exercise_1.modules.quotation.entity.Quotation;
import com.exercise_1.modules.quotation.repository.QuotationRepository;
import com.exercise_1.modules.reservation.entity.Reservation;
import com.exercise_1.modules.reservation.entity.ReservationStatus;
import com.exercise_1.modules.reservation.repository.ReservationRepository;
import com.exercise_1.modules.sale.entity.Sale;
import com.exercise_1.modules.sale.entity.SaleDetail;
import com.exercise_1.modules.sale.entity.SaleStatus;
import com.exercise_1.modules.sale.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final QuotationRepository quotationRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponseDto create(OrderCreateDto dto) {

        if (!customerRepository.existsById(dto.getCustomerId())) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("Customer not found")
                    .data(null)
                    .build();
        }

        if (!warehouseRepository.existsById(dto.getWarehouseId())) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("Warehouse not found")
                    .data(null)
                    .build();
        }

        if (dto.getPaymentId() != null && !paymentRepository.existsById(dto.getPaymentId())) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("Payment not found")
                    .data(null)
                    .build();
        }

        if (dto.getQuotationId() != null && !quotationRepository.existsById(dto.getQuotationId())) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("Quotation not found")
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        for (OrderCreateDto.OrderDetailDto detail : dto.getDetails()) {
            if (!productRepository.existsById(detail.getProductId())) {
                return OrderResponseDto.builder()
                        .success(false)
                        .message("Product not found")
                        .data(null)
                        .build();
            }
        }

        try {
            Order order = Order.builder()
                    .number(null)
                    .customer(Customer.builder().id(dto.getCustomerId()).build())
                    .warehouse(Warehouse.builder().id(dto.getWarehouseId()).build())
                    .currency(dto.getCurrency())
                    .payment(dto.getPaymentId() != null ?
                            Payment.builder().id(dto.getPaymentId()).build() : null)
                    .quotation(dto.getQuotationId() != null ?
                            Quotation.builder().id(dto.getQuotationId()).build() : null)
                    .status(OrderStatus.DRAFT)
                    .notes(dto.getNotes())
                    .user(dto.getUserId() != null ?
                            User.builder().id(dto.getUserId()).build() : null)
                    .build();

            List<OrderDetail> details = dto.getDetails().stream()
                    .map(detailDto -> OrderDetail.builder()
                            .order(order)
                            .product(Product.builder().id(detailDto.getProductId()).build())
                            .quantity(detailDto.getQuantity())
                            .price(detailDto.getPrice())
                            .notes(detailDto.getNotes())
                            .user(dto.getUserId() != null ?
                                    User.builder().id(dto.getUserId()).build() : null)
                            .build())
                    .collect(Collectors.toList());

            order.setDetails(details);

            Order savedOrder = orderRepository.save(order);

            savedOrder.setNumber(savedOrder.getId().toString());
            savedOrder = orderRepository.save(savedOrder);

            createReservationsForOrder(savedOrder, dto.getUserId());

            return OrderResponseDto.builder()
                    .success(true)
                    .message("Order created successfully")
                    .data(OrderResponseDto.OrderData.builder()
                            .id(savedOrder.getId())
                            .number(savedOrder.getNumber())
                            .status(savedOrder.getStatus())
                            .build())
                    .build();

        } catch (Exception e) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("Error creating order: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Transactional
    public OrderResponseDto cancel(Long orderId, OrderCancelDto dto) {

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("Order not found")
                    .data(null)
                    .build();
        }

        if (order.getStatus() != OrderStatus.DRAFT) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("Only DRAFT orders can be canceled. Current status: " + order.getStatus())
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        try {
            cancelReservationsForOrder(orderId);

            if (dto.getUserId() != null) {
                order.setUser(User.builder().id(dto.getUserId()).build());
            }
            order.setStatus(OrderStatus.CANCELED);
            order.setNotes(dto.getCancelNotes());
            Order savedOrder = orderRepository.save(order);

            return OrderResponseDto.builder()
                    .success(true)
                    .message("Order canceled successfully")
                    .data(OrderResponseDto.OrderData.builder()
                            .id(savedOrder.getId())
                            .number(savedOrder.getNumber())
                            .status(savedOrder.getStatus())
                            .build())
                    .build();

        } catch (Exception e) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("Error canceling order: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Transactional
    public OrderResponseDto confirm(Long orderId, OrderConfirmDto dto) {

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("Order not found")
                    .data(null)
                    .build();
        }

        if (order.getStatus() != OrderStatus.DRAFT) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("Only DRAFT orders can be confirmed. Current status: " + order.getStatus())
                    .data(null)
                    .build();
        }

        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("User not found")
                    .data(null)
                    .build();
        }

        try {
            Sale sale = createSaleFromOrder(order, dto.getUserId(), dto.getConfirmNotes());
            Sale savedSale = saleRepository.save(sale);

            savedSale.setNumber(savedSale.getId().toString());
            savedSale = saleRepository.save(savedSale);

            if (dto.getUserId() != null) {
                order.setUser(User.builder().id(dto.getUserId()).build());
            }
            order.setStatus(OrderStatus.CONFIRMED);
            order.setNotes(dto.getConfirmNotes());
            Order savedOrder = orderRepository.save(order);

            return OrderResponseDto.builder()
                    .success(true)
                    .message("Order confirmed successfully")
                    .data(OrderResponseDto.OrderData.builder()
                            .id(savedOrder.getId())
                            .number(savedOrder.getNumber())
                            .status(savedOrder.getStatus())
                            .build())
                    .build();

        } catch (Exception e) {
            return OrderResponseDto.builder()
                    .success(false)
                    .message("Error confirming order: " + e.getMessage())
                    .data(null)
                    .build();
        }
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

    private void cancelReservationsForOrder(Long orderId) {
        List<Reservation> activeReservations = reservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.ACTIVE);

        for (Reservation reservation : activeReservations) {
            reservation.setStatus(ReservationStatus.CANCELED);
        }

        if (!activeReservations.isEmpty()) {
            reservationRepository.saveAll(activeReservations);
        }
    }

    private Sale createSaleFromOrder(Order order, Long userId, String notes) {
        Sale sale = Sale.builder()
                .number(null)
                .customer(order.getCustomer())
                .warehouse(order.getWarehouse())
                .currency(order.getCurrency())
                .payment(order.getPayment())
                .status(SaleStatus.DRAFT)
                .order(order)
                .user(userId != null ?
                        User.builder().id(userId).build() : null)
                .build();

        List<SaleDetail> saleDetails = order.getDetails().stream()
                .map(orderDetail -> SaleDetail.builder()
                        .sale(sale)
                        .product(orderDetail.getProduct())
                        .quantity(orderDetail.getQuantity())
                        .price(orderDetail.getPrice())
                        .user(userId != null ?
                                User.builder().id(userId).build() : null)
                        .build())
                .collect(Collectors.toList());

        sale.setDetails(saleDetails);
        return sale;
    }
}