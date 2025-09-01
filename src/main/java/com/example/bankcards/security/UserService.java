package com.example.bankcards.security;

import com.example.bankcards.config.PasswordConfig;
import com.example.bankcards.dto.RegistrationUserDTO;
import com.example.bankcards.service.RoleService;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserExistsException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordConfig passwordConfig;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException(String.format("User %s not found", username)));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName())))
                .build();
    }

    @Transactional
    public void createUser(RegistrationUserDTO userDTO){
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new UserExistsException("User already exists");
        }
        User user = User.builder()
                .username(userDTO.getUsername())
                .password(passwordConfig.passwordEncoder().encode(userDTO.getPassword()))
                .role(roleService.findByName("ROLE_USER").get())
                .build();
        userRepository.save(user);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
