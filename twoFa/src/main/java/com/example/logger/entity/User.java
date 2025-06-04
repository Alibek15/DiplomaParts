package com.example.logger.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USERS", schema = "myapp")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "USERNAME", nullable = false, unique = true)
    @NotBlank(message = "Username is required")
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    @NotBlank(message = "Password is required")
    @JsonIgnore
    private String password;

    @Column(name = "EMAIL", nullable = false, unique = true)
    @NotBlank(message = "Email is required")
    private String email;

    @Column(name = "FIRST_NAME", nullable = false)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Column(name = "ROLE", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    private Role role;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "REGISTRATION_DATE", nullable = false)
    private Date registrationDate;

    @Column(name = "PHOTO_URL")
    private String photoUrl;

    @Column(name = "ACCOUNT_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    // ✅ Только для COMPANY, иначе null
    @Column(name = "COMPANY_NAME")
    private String companyName;

    @Column(name = "WEBSITE")
    private String website;

    @Column(name = "INDUSTRY")
    private String industry; // Enum можно отдельно, но строка сейчас

    @Column(name = "COMPANY_SIZE")
    private String companySize;



    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,      // ← каскадируем persist, remove и т.д.
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private TwoFactor twoFactor;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public void setRole(final Role role) {
        this.role = role;
    }
    public void removeTwoFactor() {
        if (this.twoFactor != null) {
            this.twoFactor.setUser(null);
            this.twoFactor = null;
        }
    }

    public void setRegistrationDate(final Date registrationDate) {
        this.registrationDate = registrationDate;
    }


    public void setTwoFactor(TwoFactor twoFactor) {
        // отвязываем старую, если надо
        if (this.twoFactor != null) {
            this.twoFactor.setUser(null);
        }
        this.twoFactor = twoFactor;
        if (twoFactor != null && twoFactor.getUser() != this) {
            twoFactor.setUser(this);
        }
    }

    public TwoFactor getTwoFactor() {
        return twoFactor;
    }
}