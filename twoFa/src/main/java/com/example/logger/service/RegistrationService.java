package com.example.logger.service;

import com.example.logger.entity.AccountStatus;
import com.example.logger.entity.Role;
import com.example.logger.entity.TwoFactor;
import com.example.logger.entity.User;
import com.example.logger.messaging.MessagePublisher;
import com.example.logger.messaging.RegistrationEvent;
import com.example.logger.registerReq.RegisterRequest;
import com.example.logger.repository.TwoFactorRepository;
import com.example.logger.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessagePublisher messagePublisher;
    private final JWTService jwtService;
    private final TwoFactorService twoFactorService;
    private final TwoFactorRepository twoFactorRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public String confirmAccount(String code) {
        TwoFactor tf = twoFactorRepository.findByVerificationCode(code)
                .orElseThrow(() -> new ServiceException("Invalid verification code"));

        User user = tf.getUser();

        user.setTwoFactor(null);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        twoFactorRepository.delete(tf);
        return jwtService.generateToken(user);
    }

    @Transactional
    public void register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ServiceException("Passwords do not match");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ServiceException("Email already registered");
        }

        Role selectedRole;
        try {
            selectedRole = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Invalid role. Allowed values: USER, COMPANY");
        }

        if (selectedRole != Role.USER && selectedRole != Role.COMPANY) {
            throw new ServiceException("Invalid role. Allowed values: USER, COMPANY");
        }

        if (selectedRole == Role.COMPANY) {
            if (request.getCompanyName() == null || request.getWebsite() == null ||
                    request.getIndustry() == null || request.getCompanySize() == null) {
                throw new ServiceException("All company fields must be filled for COMPANY role");
            }
        }

        if (request.getPhoneNumber() == null) {
            throw new ServiceException("Phone number is required");
        }


        String verificationCode = UUID.randomUUID().toString();
        log.info("Generated verification code: {} for email: {}", verificationCode, request.getEmail());


        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(selectedRole)
                .registrationDate(new Date())
                .accountStatus(AccountStatus.NOT_CONFIRMED)
                .companyName(selectedRole == Role.COMPANY ? request.getCompanyName() : null)
                .website(selectedRole == Role.COMPANY ? request.getWebsite() : null)
                .industry(selectedRole == Role.COMPANY ? request.getIndustry() : null)
                .companySize(selectedRole == Role.COMPANY ? request.getCompanySize() : null)
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created with ID: {} and email: {}", savedUser.getUserId(), savedUser.getEmail());


        try {
            TwoFactor twoFactor = twoFactorService.createTwoFactorEntry(savedUser, verificationCode);
            log.info("TwoFactor record created with ID: {} for user: {}", twoFactor.getTwoFactorId(), savedUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to create TwoFactor entry for user: {}", savedUser.getEmail(), e);
            throw new ServiceException("Failed to create verification record");
        }


        try {
            RegistrationEvent event = new RegistrationEvent(savedUser.getEmail(), verificationCode);
            messagePublisher.sendRegistrationEvent(event);
            log.info("Registration event sent to RabbitMQ for user: {}", savedUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send registration event for user: {}", savedUser.getEmail(), e);

        }
    }
}