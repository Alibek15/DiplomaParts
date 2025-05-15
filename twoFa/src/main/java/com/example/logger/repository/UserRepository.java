package com.example.logger.repository;

import com.example.logger.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Поиск пользователя по email.
     *
     * @param email Email пользователя
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * Поиск пользователя по коду подтверждения аккаунта.
     *
     * @param verificationCode Уникальный код подтверждения
     * @return Optional<User>
     */
    Optional<User> findByVerificationCode(String verificationCode);
}
