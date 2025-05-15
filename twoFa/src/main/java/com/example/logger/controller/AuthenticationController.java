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

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Validated @RequestBody LoginRequest request) {
        authenticationService.login(request);
        return ResponseEntity.ok("Two-factor authentication code sent to your email");
    }

    @PostMapping("/confirm-2fa")
    public ResponseEntity<Map<String, String>> confirmTwoFactor(@Validated @RequestBody TwoFactorConfirmRequest request) {
        String token = authenticationService.confirmTwoFactor(request);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
