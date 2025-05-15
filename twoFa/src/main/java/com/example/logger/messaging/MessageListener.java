package com.example.logger.messaging;


import com.example.logger.config.RabbitMQConfig;
import com.example.logger.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.REGISTRATION_QUEUE)
    public void handleRegistrationMessage(String message) {
        try {
            log.info("Received RegistrationEvent message: {}", message);
            RegistrationEvent event = objectMapper.readValue(message, RegistrationEvent.class);
            emailService.sendVerificationLink(event.getEmail(), event.getVerificationCode());
        } catch (Exception e) {
            log.error("Failed to process RegistrationEvent message", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.TWO_FACTOR_QUEUE)
    public void handleTwoFactorAuthMessage(String message) {
        try {
            log.info("Received TwoFactorAuthEvent message: {}", message);
            TwoFactorAuthEvent event = objectMapper.readValue(message, TwoFactorAuthEvent.class);
            emailService.sendVerificationCode(event.getEmail(), event.getTwoFactorCode());
        } catch (Exception e) {
            log.error("Failed to process TwoFactorAuthEvent message", e);
        }
    }
}
