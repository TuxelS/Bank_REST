package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationUserDTO {
    @NotNull(message = "username is required")
    private String username;
    @NotNull(message = "password is required")
    private String password;
    @NotNull(message = "confirm password is required")
    private String confirmPassword;
}
