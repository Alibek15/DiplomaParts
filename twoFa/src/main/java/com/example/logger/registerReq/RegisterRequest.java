package com.example.logger.registerReq;

import com.example.logger.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Role is required")
    private String role;

    // Company-specific fields (optional for ROLE == USER)
    private String companyName;
    private String website;
    private String industry;
    private String companySize;
    private String phoneNumber;
    public boolean isCompany() {
        return "COMPANY".equalsIgnoreCase(role);
    }
}
