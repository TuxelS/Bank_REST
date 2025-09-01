package com.example.bankcards.security;

import com.example.bankcards.config.PasswordConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
    private final UserService userService;
    private final JwtFilter jwtFilter;
    private final PasswordConfig passwordConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests((httpRequests) ->
                        httpRequests
                                .requestMatchers("/user").hasAuthority("ROLE_USER")
                                .requestMatchers("/admin").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/secured").authenticated()
                                .requestMatchers("/api/**").permitAll())
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling((exception) ->
                        exception.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
       DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userService);
       daoAuthenticationProvider.setPasswordEncoder(passwordConfig.passwordEncoder());
       return daoAuthenticationProvider;
    }


    /*
    Ты → запрашиваешь AuthenticationManager
     ↓
    AuthenticationConfiguration → создаёт/возвращает AuthenticationManager
     ↓
    AuthenticationManager → проверяет логин/пароль через ProviderManager → DaoAuthenticationProvider → UserDetailsService
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }



}
