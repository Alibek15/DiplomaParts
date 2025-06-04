package com.example.logger.mapper;


import com.example.logger.entity.User;
import com.example.logger.dto.UserDto;
import com.example.logger.dto.CompanyDto;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public static CompanyDto toCompanyDto(User user) {
        CompanyDto dto = new CompanyDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setCompanyName(user.getCompanyName());
        dto.setWebsite(user.getWebsite());
        dto.setIndustry(user.getIndustry());
        dto.setCompanySize(user.getCompanySize());
        dto.setPhoneNumber(user.getPhoneNumber());
        if (user.getRegistrationDate() != null)
            dto.setRegistrationDate(user.getRegistrationDate().toString());
        return dto;
    }
}
