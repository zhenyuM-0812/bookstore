package com.example.payment.service;

import com.example.payment.client.OrderClient;
import com.example.payment.client.dto.OrderSummaryResponse;
import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.entity.Payment;
import com.example.payment.entity.PaymentStatus;
import com.example.payment.exception.DuplicatePaymentException;
import com.example.payment.exception.InvalidOrderStatusException;
import com.example.payment.exception.ResourceNotFoundException;
import com.example.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl
        implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;


    private PaymentResponse toResponse(
            Payment payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .build();
    }


    @Override
    @Transactional
    public PaymentResponse processPayment(
            PaymentRequest request) {

        Long orderId = request.getOrderId();

        OrderSummaryResponse order =
                orderClient.getOrderById(orderId);

        if (paymentRepository.existsByOrderId(orderId)) {

            throw new DuplicatePaymentException(
                    "Order "
                            + orderId
                            + " has already been paid"
            );
        }

        if (!"PENDING".equals(order.getStatus())) {

            throw new InvalidOrderStatusException(
                    "Order "
                            + orderId
                            + " cannot be paid because its status is "
                            + order.getStatus()
            );
        }

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getTotalPrice());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        Payment savedPayment =
                paymentRepository.save(payment);

        return toResponse(savedPayment);
    }


    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(
            Long orderId) {

        orderClient.getOrderById(orderId);

        Payment payment =
                paymentRepository
                        .findByOrderId(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment for order "
                                                + orderId
                                                + " does not exist"
                                )
                        );

        return toResponse(payment);
    }
}