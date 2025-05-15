package com.example.logger.messaging;

import com.example.logger.config.RabbitMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void sendRegistrationEvent(RegistrationEvent event) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(event);
            log.info("Sending RegistrationEvent to queue [{}]: {}", RabbitMQConfig.REGISTRATION_QUEUE, jsonMessage);
            rabbitTemplate.convertAndSend(RabbitMQConfig.REGISTRATION_QUEUE, jsonMessage);
        } catch (Exception e) {
            log.error("Failed to send RegistrationEvent to RabbitMQ", e);
        }
    }

    public void sendTwoFactorAuthEvent(TwoFactorAuthEvent event) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(event); // сериализация в JSON
            log.info("Sending TwoFactorAuthEvent to queue [{}]: {}", RabbitMQConfig.TWO_FACTOR_QUEUE, jsonMessage);
            rabbitTemplate.convertAndSend(RabbitMQConfig.TWO_FACTOR_QUEUE, jsonMessage);
        } catch (Exception e) {
            log.error("Failed to send TwoFactorAuthEvent to RabbitMQ", e);
        }
    }
}
