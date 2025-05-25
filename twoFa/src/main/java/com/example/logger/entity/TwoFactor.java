package com.example.logger.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "two_factor", schema = "myapp")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TwoFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "two_factor_id")
    private Long twoFactorId;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "two_fa_code", nullable = false)
    private String twoFaCode;

    @Column(name = "two_fa_expiry", nullable = false)
    private Date twoFaExpiry;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @Column(name = "verification_code", nullable = false)
    private String verificationCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

//    public void setUser(User user) {
//        this.user = user;
//        if (user != null && user.getTwoFactor() != this) {
//            user.setTwoFactor(this);
//        }
//    }
    @PrePersist
    public void prePersist() {
        if (verificationCode == null || verificationCode.isBlank())
            throw new IllegalStateException("verificationCode cannot be null/empty");
        if (user == null)
            throw new IllegalStateException("user cannot be null");
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        if (verificationCode == null || verificationCode.isBlank())
            throw new IllegalStateException("verificationCode cannot be null/empty");
        if (user == null)
            throw new IllegalStateException("user cannot be null");
        updatedAt = new Date();
    }

}