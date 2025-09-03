package com.example.bankcards.entity;

import com.example.bankcards.enumeration.CardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String cardNumberEncrypted;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @Column(nullable = false)
    private LocalDate expirationDate;
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus cardStatus = CardStatus.ACTIVE;

}
