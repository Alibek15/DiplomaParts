package com.example.logger.service;

import com.example.logger.entity.AccountStatus;
import com.example.logger.entity.TwoFactor;
import com.example.logger.entity.User;
import com.example.logger.messaging.MessagePublisher;
import com.example.logger.messaging.TwoFactorAuthEvent;
import com.example.logger.registerReq.LoginRequest;
import com.example.logger.registerReq.TwoFactorConfirmRequest;
import com.example.logger.repository.TwoFactorRepository;
import com.example.logger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j


public class AuthenticationService {
    private final UserRepository userRepo;
    private final TwoFactorRepository tfRepo;
    private final PasswordEncoder encoder;
    private final MessagePublisher publisher;
    private final JWTService jwtService;

    /** Шаг 1: login → генерируем и сохраняем 2FA */
    @Transactional
    public void login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ServiceException("Invalid email or password"));
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new ServiceException("Invalid email or password");
        }

        // 1) удалить старый TwoFactor (если он был)
        tfRepo.findByUser(user)
                .ifPresent(oldTf -> tfRepo.delete(oldTf));

        // 2) создать новый TwoFactor
        TwoFactor tf = new TwoFactor();
        String code = generateNumericCode();
        tf.setTwoFaCode(code);
        tf.setVerificationCode(UUID.randomUUID().toString());
        tf.setTwoFaExpiry(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)));

        // 3) связать и сохранить
        user.setTwoFactor(tf);
        userRepo.save(user);

        // 4) отправить код
        publisher.sendTwoFactorAuthEvent(new TwoFactorAuthEvent(
                user.getEmail(), code));
    }

    /** Шаг 2: confirm-2fa → активируем и удаляем запись 2FA */
    @Transactional
    public String confirmTwoFactor(TwoFactorConfirmRequest req) {
        TwoFactor tf = tfRepo.findByTwoFaCode(req.getCode())
                .orElseThrow(() -> new ServiceException("Invalid 2FA code"));

        Date expiry = tf.getTwoFaExpiry();
        if (expiry.before(new Date())) {
            throw new ServiceException("2FA code expired");
        }

        User user = tf.getUser();
        user.setAccountStatus(AccountStatus.ACTIVE);

        // отвязываем и удаляем TwoFactor
        user.removeTwoFactor();
        // благодаря cascade=ALL запись удалится автоматически при сохранении user,
        // но можно и явно:
        // tfRepo.delete(tf);

        userRepo.save(user);

        // генерируем JWT
        return jwtService.generateToken(user);
    }

    private String generateNumericCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }
}