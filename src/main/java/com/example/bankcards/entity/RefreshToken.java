package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Table(name = "refresh_tokens")
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date createdAt;
    private Date expiredDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // может быть множество токенов с разных устройств.

    public RefreshToken(String token){
        this.token = token;
    }

}
