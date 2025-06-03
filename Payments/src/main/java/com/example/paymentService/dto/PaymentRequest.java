package com.example.paymentService.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Double amount;
    private String userId;
    private String description;
}
