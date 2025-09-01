package com.example.bankcards.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.lifetime}")
    private Duration tokenAccessLifetime;

    @Value("${jwt.refresh.lifetime}")
    private Duration tokenRefreshLifetime;

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .get();
        claims.put("role", role);
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + tokenAccessLifetime.toMillis());
        return Jwts.builder()
                .claims()
                    .subject(userDetails.getUsername())
                    .issuedAt(issuedDate)
                    .expiration(expiredDate)
                    .add(claims)
                .and()
                .signWith(getSigningKey())
                .compact();

    }

    public String generateRefreshToken(UserDetails userDetails){
        Date issuedDate = new Date();
        Date expiredDate =  new Date(issuedDate.getTime() + tokenRefreshLifetime.toMillis());
        return Jwts.builder()
                .claims()
                    .subject(userDetails.getUsername())
                    .issuedAt(issuedDate)
                    .expiration(expiredDate)
                .and()
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsername(String token){
        return getAllClaimsFromAccessToken(token).getSubject();
    }

    public String getRole(String token){
        return getAllClaimsFromAccessToken(token).get("role", String.class);
    }

    private Claims getAllClaimsFromAccessToken(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromRefreshToken(String token){
        return getAllClaimsFromRefreshToken(token).getSubject();
    }

    public Date getIssuedDateFromRefreshToken(String token){
        return getAllClaimsFromRefreshToken(token).getIssuedAt();
    }

    public Date getExpirationDateFromRefreshToken(String token){
        return getAllClaimsFromRefreshToken(token).getExpiration();
    }

    private Claims getAllClaimsFromRefreshToken(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValidatedRefreshToken(String token){
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            // проверка просрочен или нет
            if (claimsJws.getPayload().getExpiration().before(new Date())) {
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;  // Ошибка парсинга/подписи/формата
        }

    }


}
