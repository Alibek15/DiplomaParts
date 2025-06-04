package com.example.logger.repository;

import com.example.logger.entity.TwoFactor;
import com.example.logger.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;




public interface TwoFactorRepository extends JpaRepository<TwoFactor, Long> {

    Optional<TwoFactor> findByVerificationCode(String verificationCode);
    Optional<TwoFactor> findByTwoFaCode(String twoFaCode);
    Optional<TwoFactor> findByUser(User user);


    Optional<TwoFactor> findByUser_UserId(Long userId);
    void deleteByUser_UserId(Long userId);


    @Modifying
    @Transactional
    @Query("DELETE FROM TwoFactor t WHERE t.verificationCode = :code")
    void deleteByVerificationCode(@Param("code") String code);

    @Modifying
    @Transactional
    @Query("DELETE FROM TwoFactor t WHERE t.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}