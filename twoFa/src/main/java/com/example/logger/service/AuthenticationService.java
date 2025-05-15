package com.example.logger.service;


import com.example.logger.entity.User;
import com.example.logger.messaging.MessagePublisher;
import com.example.logger.messaging.TwoFactorAuthEvent;
import com.example.logger.registerReq.LoginRequest;
import com.example.logger.registerReq.TwoFactorConfirmRequest;
import com.example.logger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessagePublisher messagePublisher;
    private final JWTService jwtService;

    public void login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            throw new ServiceException("Invalid email or password");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ServiceException("Invalid email or password");
        }

        if (user.getAccountStatus() != com.example.logger.entity.AccountStatus.ACTIVE) {
            throw new ServiceException("Account not confirmed");
        }

        String twoFactorCode = generateTwoFactorCode();
        Date expiry = new Date(System.currentTimeMillis() + 5 * 60 * 1000); // 5 минут

        user.setTwoFaCode(twoFactorCode);
        user.setTwoFaExpiry(expiry);
        userRepository.save(user);

        messagePublisher.sendTwoFactorAuthEvent(new TwoFactorAuthEvent(user.getEmail(), twoFactorCode));
    }

    public String confirmTwoFactor(TwoFactorConfirmRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            throw new ServiceException("Invalid email");
        }

        User user = optionalUser.get();

        if (user.getTwoFaCode() == null || user.getTwoFaExpiry() == null) {
            throw new ServiceException("Two-factor authentication not initialized");
        }

        if (new Date().after(user.getTwoFaExpiry())) {
            throw new ServiceException("Two-factor authentication code expired");
        }

        if (!user.getTwoFaCode().equals(request.getCode())) {
            throw new ServiceException("Invalid two-factor authentication code");
        }

        // Очищаем код после успешной аутентификации
        user.setTwoFaCode(null);
        user.setTwoFaExpiry(null);
        userRepository.save(user);

        // Генерируем JWT токен
        return jwtService.generateToken(user);
    }

    private String generateTwoFactorCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000); // 6-значный код
    }
}
