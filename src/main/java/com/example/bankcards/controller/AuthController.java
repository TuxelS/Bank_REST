package com.example.bankcards.controller;

import com.example.bankcards.dto.JwtRequestDTO;
import com.example.bankcards.dto.JwtResponseDTO;
import com.example.bankcards.dto.RegistrationUserDTO;
import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.exception.ApplicationError;
import com.example.bankcards.exception.UserExistsException;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.RefreshTokenService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @Value("${app.security.cookies.secure}")
    private boolean cookieSecure;

    @Value("${app.security.cookies.same-site}")
    private String cookieSameSite;

    @Value("${app.security.cookies.path}")
    private String cookiePath;

    @Value("${jwt.refresh.lifetime}")
    private Duration tokenRefreshLifetime;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequestDTO jwtRequestDTO){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequestDTO.getUsername(),
                    jwtRequestDTO.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ApplicationError(HttpStatus.UNAUTHORIZED.value(),
                    "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }
        try {
        // ПОПЫТКА получить UserDetails - выбросит DisabledException если заблокирован
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(jwtRequestDTO.getUsername());
        String accessToken = jwtTokenUtils.generateAccessToken(userDetails);
        String refreshToken = jwtTokenUtils.generateRefreshToken(userDetails);

        // Сохраняем refresh-токен в БД
        refreshTokenService.saveRefreshToken(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, addRefreshTokenToCookie(refreshToken).toString())
                .body(new JwtResponseDTO(accessToken));
        } catch (DisabledException e) {
            return new ResponseEntity<>(new ApplicationError(HttpStatus.FORBIDDEN.value(),
                    "Аккаунт заблокирован"), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken){
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApplicationError(HttpStatus.UNAUTHORIZED.value(), "Refresh token is missing"));
        }

        try {
            if (!jwtTokenUtils.isValidatedRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApplicationError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired refresh token"));
            }

            String username = jwtTokenUtils.getUsernameFromRefreshToken(refreshToken);
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApplicationError(HttpStatus.UNAUTHORIZED.value(), "User not found"));
            }

            Optional<RefreshToken> tokenFromDb = refreshTokenService.findByToken(refreshToken);
            if (tokenFromDb.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApplicationError(HttpStatus.UNAUTHORIZED.value(), "Refresh token not found in database"));
            }

            String newAccessToken = jwtTokenUtils.generateAccessToken(userDetails);

            return ResponseEntity.ok()
                    .body(new JwtResponseDTO(newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApplicationError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error during token refresh: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDTO registrationUserDTO) {
        if (!registrationUserDTO.getConfirmPassword().equals(registrationUserDTO.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new ApplicationError(HttpStatus.BAD_REQUEST.value(), "Passwords are not equal"));
        }
        try {
            userService.createUser(registrationUserDTO);
            return ResponseEntity.ok()
                    .body("User created successfully");
        } catch (UserExistsException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
    private ResponseCookie addRefreshTokenToCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(cookieSecure)   // для http - false, https - true
                .path(cookiePath)   // Делает куки видимым только для этого пути
                .maxAge(tokenRefreshLifetime)
                .sameSite(cookieSameSite) // Защищает от CSRF-атак
                .build();
    }
}
