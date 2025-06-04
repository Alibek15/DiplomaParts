package com.example.logger.service;

import com.example.logger.entity.TwoFactor;
import com.example.logger.entity.User;
import com.example.logger.repository.TwoFactorRepository;
import com.example.logger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwoFactorService {
    private final TwoFactorRepository twoFactorRepository;
    private final UserRepository userRepository;

    /**
     * Создание записи TwoFactor для регистрации
     */
    @Transactional
    public TwoFactor createTwoFactorEntry(User user, String verificationCode) {
        if (verificationCode == null || verificationCode.isBlank())
            throw new IllegalArgumentException("verificationCode is required");


        twoFactorRepository.findByUser_UserId(user.getUserId())
                .ifPresent(twoFactorRepository::delete);

        TwoFactor tf = TwoFactor.builder()
                .user(user)
                .twoFaCode(generateTwoFaCode())
                .verificationCode(verificationCode)
                .createdAt(new Date())
                .updatedAt(new Date())
                .twoFaExpiry(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .build();

        return twoFactorRepository.save(tf);
    }


    /**
     * Генерация 6-значного кода для 2FA
     */
    private String generateTwoFaCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    /**
     * Валидация verification code при подтверждении регистрации
     */
    @Transactional(readOnly = true)
    public boolean isValidVerificationCode(String code) {
        Optional<TwoFactor> twoFactorOpt = twoFactorRepository.findByVerificationCode(code);

        if (twoFactorOpt.isEmpty()) {
            log.warn("Verification code not found: {}", code);
            return false;
        }

        TwoFactor twoFactor = twoFactorOpt.get();


        if (twoFactor.getTwoFaExpiry().before(new Date())) {
            log.warn("Verification code expired: {}", code);
            return false;
        }

        return true;
    }
}