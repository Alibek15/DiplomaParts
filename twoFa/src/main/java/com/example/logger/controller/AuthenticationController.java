package com.example.logger.controller;


import com.example.logger.registerReq.LoginRequest;
import com.example.logger.registerReq.TwoFactorConfirmRequest;
import com.example.logger.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest req) {
        authService.login(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm-2fa")
    public ResponseEntity<Map<String,String>> confirm2fa(@RequestBody TwoFactorConfirmRequest req) {
        String token = authService.confirmTwoFactor(req);
        System.out.println("JWT: " + token);
        return ResponseEntity.ok(Map.of("token", token));
    }
}