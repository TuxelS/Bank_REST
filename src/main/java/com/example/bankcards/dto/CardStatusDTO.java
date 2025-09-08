package com.example.bankcards.dto;

import com.example.bankcards.enumeration.CardStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardStatusDTO {
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    CardStatus cardStatus;
}
