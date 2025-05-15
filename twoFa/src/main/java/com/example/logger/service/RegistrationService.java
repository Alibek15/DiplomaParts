package com.example.logger.service;

import com.example.logger.entity.AccountStatus;
import com.example.logger.entity.Role;
import com.example.logger.entity.User;
import com.example.logger.messaging.MessagePublisher;
import com.example.logger.messaging.RegistrationEvent;
import com.example.logger.registerReq.RegisterRequest;
import com.example.logger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessagePublisher messagePublisher;
    private final JWTService jwtService;

    public String confirmAccount(String code) {
        Optional<User> optionalUser = userRepository.findByVerificationCode(code);

        if (optionalUser.isEmpty()) {
            throw new ServiceException("Invalid verification code");
        }

        User user = optionalUser.get();
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setVerificationCode(null);
        userRepository.save(user);


        return jwtService.generateToken(user);
    }
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

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getEmail()) // Username = email
                .password(passwordEncoder.encode(request.getPassword()))
                .role(selectedRole) // Ставим проверенную роль
                .registrationDate(new Date())
                .accountStatus(AccountStatus.NOT_CONFIRMED)
                .verificationCode(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);


        RegistrationEvent event = new RegistrationEvent(user.getEmail(), user.getVerificationCode());
        messagePublisher.sendRegistrationEvent(event);
    }
}
