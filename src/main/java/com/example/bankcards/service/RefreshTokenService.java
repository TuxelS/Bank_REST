package com.example.bankcards.service;

import com.example.bankcards.security.UserService;
import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.RefreshTokenRepository;
import com.example.bankcards.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Transactional
    public void saveRefreshToken(String refreshToken) {
        String username = jwtTokenUtils.getUsernameFromRefreshToken(refreshToken);
        Date issuedAt = jwtTokenUtils.getIssuedDateFromRefreshToken(refreshToken);
        Date expirationDate = jwtTokenUtils.getExpirationDateFromRefreshToken(refreshToken);
        Optional<User> user = userService.findUserByUsername(username);
        // создание записи для каждого устройства
        RefreshToken refreshTokenObject = new RefreshToken(refreshToken);
        refreshTokenObject.setUser(user.get());
        refreshTokenObject.setCreatedAt(issuedAt);
        refreshTokenObject.setExpiredDate(expirationDate);
        refreshTokenRepository.save(refreshTokenObject);

    }

    public Optional<RefreshToken> findByToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }
}
