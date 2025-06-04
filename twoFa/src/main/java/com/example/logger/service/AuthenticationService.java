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


    @Transactional
    public void login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ServiceException("Invalid email or password"));
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new ServiceException("Invalid email or password");
        }


        tfRepo.findByUser(user)
                .ifPresent(oldTf -> tfRepo.delete(oldTf));


        TwoFactor tf = new TwoFactor();
        String code = generateNumericCode();
        tf.setTwoFaCode(code);
        tf.setVerificationCode(UUID.randomUUID().toString());
        tf.setTwoFaExpiry(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)));


        user.setTwoFactor(tf);
        userRepo.save(user);


        publisher.sendTwoFactorAuthEvent(new TwoFactorAuthEvent(
                user.getEmail(), code));
    }


    @Transactional
    public String confirmTwoFactor(TwoFactorConfirmRequest req) {
        log.info("CONFIRM 2FA STARTED: " + req.getCode());
        TwoFactor tf = tfRepo.findByTwoFaCode(req.getCode())
                .orElseThrow(() -> new ServiceException("Invalid 2FA code"));

        Date expiry = tf.getTwoFaExpiry();
        if (expiry.before(new Date())) {
            throw new ServiceException("2FA code expired");
        }

        User user = tf.getUser();
        user.setAccountStatus(AccountStatus.ACTIVE);


        user.removeTwoFactor();


        userRepo.save(user);


        System.out.println("JWT:" + jwtService.generateToken(user));
        return jwtService.generateToken(user);
    }

    private String generateNumericCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }
}