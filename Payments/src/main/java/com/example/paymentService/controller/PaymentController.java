package com.example.paymentService.controller;

import com.example.paymentService.dto.PaymentRequest;
import com.example.paymentService.dto.PaymentResponse;
import com.example.paymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/start")
    public ResponseEntity<PaymentResponse> startPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.startPayment(request));
    }
}
