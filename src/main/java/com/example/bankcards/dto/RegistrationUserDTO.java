package com.example.bankcards.dto;

import lombok.Data;

@Data
public class RegistrationUserDTO {
    private String username;
    private String password;
    private String confirmPassword;
}
