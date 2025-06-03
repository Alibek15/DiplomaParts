package com.example.paymentService.service;

import com.example.paymentService.dto.PaymentRequest;
import com.example.paymentService.dto.PaymentResponse;
import com.example.paymentService.config.HalykProps;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final RestTemplate restTemplate;
    private final HalykProps halykProps;

    // Получение токена
    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("scope", "payment");
        form.add("client_id", halykProps.getClientId());
        form.add("client_secret", halykProps.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);

        ResponseEntity<Map> resp = restTemplate.postForEntity(halykProps.getOauthUrl(), req, Map.class);
        System.out.println("Ответ Homebank на токен: " + resp.getBody());
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null && resp.getBody().containsKey("access_token")) {
            return resp.getBody().get("access_token").toString();
        }
        throw new RuntimeException("Не удалось получить access_token от Homebank");
    }

    public PaymentResponse startPayment(PaymentRequest request) {
        String token = getAccessToken();

        // Собираем тело инвойса
        Map<String, Object> body = new HashMap<>();
        body.put("shop_id", halykProps.getShopId());
        body.put("account_id", halykProps.getAccountId());
        body.put("invoice_id", String.valueOf(System.currentTimeMillis() % 1000000000000000L));
        body.put("amount", request.getAmount());
        body.put("currency", "KZT");
        body.put("description", request.getDescription());
        body.put("expire_period", "1d");
        body.put("language", "rus");
        body.put("post_link", halykProps.getSuccessUrl());
        body.put("failure_post_link", halykProps.getFailureUrl());


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                halykProps.getBaseUrl() + "/api/invoice", entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().containsKey("invoice_url")) {
            return new PaymentResponse(response.getBody().get("invoice_url").toString());
        }
        throw new RuntimeException("Ошибка при создании инвойса: " + response.getBody());
    }
}