package com.example.bankcards.service;

import com.example.bankcards.config.PasswordConfig;
import com.example.bankcards.dto.RegistrationUserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserExistsException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordConfig passwordConfig;

    @Transactional
    public void createUser(RegistrationUserDTO userDTO){
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new UserExistsException("User already exists");
        }
        User user = User.builder()
                .username(userDTO.getUsername())
                .password(passwordConfig.passwordEncoder().encode(userDTO.getPassword()))
                .role(roleService.findByName("ROLE_USER").get())
                .isActive(true)
                .build();
        userRepository.save(user);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll(Boolean isActive) {
        if (isActive == null) {
            return userRepository.findAll();
        } else {
            return userRepository.findByIsActive(isActive);
        }
    }

    public boolean userIsActive(String username) {
        return userRepository.findByUsername(username).get().isActive();
    }

    @Transactional
    public User toBlockUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.get();
        user.setActive(false);
        return userRepository.save(user);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
}
