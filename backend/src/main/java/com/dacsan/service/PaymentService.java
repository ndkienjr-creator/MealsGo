package com.dacsan.service;

import com.dacsan.dto.request.CreatePaymentRequest;
import com.dacsan.dto.response.CreatePaymentResponse;
import com.dacsan.entity.*;
import com.dacsan.repository.OrderRepository;
import com.dacsan.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentProvider provider = PaymentProvider.valueOf(request.getProvider().toUpperCase());

        BigDecimal shippingFee = order.getShippingFee() == null ? BigDecimal.ZERO : order.getShippingFee();
        BigDecimal amount = order.getTotalAmount().add(shippingFee);

        String transactionRef = "PAY_" + order.getId() + "_" + System.currentTimeMillis();
        String description = "Thanh toan don " + order.getOrderNumber();

        Payment payment = Payment.builder()
                .order(order)
                .provider(provider)
                .amount(amount)
                .description(description)
                .transactionRef(transactionRef)
                .status(PaymentStatus.PENDING)
                .checkoutUrl("TEMP_CHECKOUT_URL")
                .requestPayload("TEMP_REQUEST")
                .build();

        payment = paymentRepository.save(payment);

        return CreatePaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(order.getId())
                .provider(payment.getProvider().name())
                .transactionRef(payment.getTransactionRef())
                .checkoutUrl(payment.getCheckoutUrl())
                .status(payment.getStatus().name())
                .build();
    }

    public Payment getLatestPaymentByOrderId(Long orderId) {
        return paymentRepository.findTopByOrderIdOrderByIdDesc(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Transactional
    public void handleVnpayCallback(String transactionRef, String responseCode, String rawPayload) {
        Payment payment = paymentRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setResponsePayload(rawPayload);

        if ("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
    }

    @Transactional
    public void handleMomoCallback(String transactionRef, String resultCode, String rawPayload) {
        Payment payment = paymentRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setResponsePayload(rawPayload);

        if ("0".equals(resultCode)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
    }
}

//test commit
//this is the anouncement when the integrated 