package com.example.payment.service;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse processPayment(
            PaymentRequest request
    );

    PaymentResponse getPaymentByOrderId(
            Long orderId
    );
}