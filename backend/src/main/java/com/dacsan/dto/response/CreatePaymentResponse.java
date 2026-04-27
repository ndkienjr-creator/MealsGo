package com.dacsan.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePaymentResponse {
    private Long paymentId;
    private Long orderId;
    private String provider;
    private String transactionRef;
    private String checkoutUrl;
    private String status;
}