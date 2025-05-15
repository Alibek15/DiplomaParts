package com.example.logger.registerReq;

import com.example.logger.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private String name;
    private String description;

    public RoleDTO(Role role) {
        this.name = role.name();
        this.description = getDescriptionByRole(role);
    }

    private String getDescriptionByRole(Role role) {
        return switch (role) {
            case USER -> "Ordinary user of the system.";
            case COMPANY -> "Company representative.";
            case ADMIN -> "System administrator with full access.";
        };
    }
}
