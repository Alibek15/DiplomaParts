package com.example.logger.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMQConfig {

    public static final String REGISTRATION_QUEUE = "registration_queue";
    public static final String TWO_FACTOR_QUEUE = "two_factor_queue";

    @Bean
    public Queue registrationQueue() {
        return new Queue(REGISTRATION_QUEUE, true); // durable = true
    }

    @Bean
    public Queue twoFactorQueue() {
        return new Queue(TWO_FACTOR_QUEUE, true); // durable = true
    }
}