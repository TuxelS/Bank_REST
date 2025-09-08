package com.example.bankcards.dto;

import com.example.bankcards.entity.User;
import com.example.bankcards.enumeration.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private Long id;
    private String maskedCard;
    private User owner;
    private String lastFourSymbols;
    private LocalDate expirationDate;
    private BigDecimal balance;
    private CardStatus cardStatus;
}
