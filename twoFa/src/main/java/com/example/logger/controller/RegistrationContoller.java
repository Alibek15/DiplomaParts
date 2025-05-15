package com.example.logger.controller;

import com.example.logger.registerReq.RegisterRequest;
import com.example.logger.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationContoller {
    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        registrationService.register(request);
        return new ResponseEntity<>("Registration successful. Please check your email to verify your account.", HttpStatus.CREATED);
    }

    @GetMapping("/confirm")
    public ResponseEntity<Map<String, String>> confirmAccount(@RequestParam("code") String code) {
        String jwt = registrationService.confirmAccount(code);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Account verified successfully");
        response.put("token", jwt);
        return ResponseEntity.ok(response);
    }
}
