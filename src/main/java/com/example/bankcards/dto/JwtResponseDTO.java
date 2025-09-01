package com.example.bankcards.dto;

import lombok.Data;

@Data
public class JwtResponseDTO {
    private String accessToken;
    public JwtResponseDTO(String accessToken){
        this.accessToken = accessToken;
    }
}
