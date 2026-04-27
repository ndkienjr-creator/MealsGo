package com.dacsan.controller;

import com.dacsan.dto.request.CreatePaymentRequest;
import com.dacsan.dto.response.CreatePaymentResponse;
import com.dacsan.entity.Payment;
import com.dacsan.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public CreatePaymentResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/order/{orderId}")
    public Payment getPaymentByOrder(@PathVariable Long orderId) {
        return paymentService.getLatestPaymentByOrderId(orderId);
    }

    @GetMapping("/vnpay/callback")
    public String vnpayCallback(
            @RequestParam("vnp_TxnRef") String transactionRef,
            @RequestParam("vnp_ResponseCode") String responseCode
    ) {
        paymentService.handleVnpayCallback(transactionRef, responseCode, "VNPAY_CALLBACK");
        return "VNPAY callback received";
    }

    @GetMapping("/momo/callback")
    public String momoCallback(
            @RequestParam("orderId") String transactionRef,
            @RequestParam("resultCode") String resultCode
    ) {
        paymentService.handleMomoCallback(transactionRef, resultCode, "MOMO_CALLBACK");
        return "MOMO callback received";
    }
}