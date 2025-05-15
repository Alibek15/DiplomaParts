package com.example.logger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    /**
     * Отправка письма с кодом двухфакторной аутентификации (2FA)
     *
     * @param to   Email получателя
     * @param code Код подтверждения
     */
    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your 2FA Verification Code");
        message.setText("Your verification code is: " + code);
        mailSender.send(message);
    }

    /**
     * Отправка письма с ссылкой для подтверждения аккаунта
     *
     * @param to               Email получателя
     * @param verificationCode Уникальный код подтверждения
     */
    public void sendVerificationLink(String to, String verificationCode) {
        String link = "http://localhost:8080/api/auth/confirm?code=" + verificationCode;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Account Verification");
        message.setText(
                "Thank you for registering.\n\n" +
                        "Please click the link below to activate your account:\n" +
                        link + "\n\n" +
                        "If you did not request this, please ignore this email."
        );
        mailSender.send(message);
    }
}
