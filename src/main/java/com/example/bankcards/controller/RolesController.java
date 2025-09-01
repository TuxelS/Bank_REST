package com.example.bankcards.controller;


import com.example.bankcards.security.UserService;
import com.example.bankcards.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RolesController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/admin")
    public String admin(){
        return "u a admin";
    }

    @GetMapping("/user")
    public String user(){
        return "u a user";
    }

    @GetMapping("/secured")
    public String secured(){
        return "its secured response";
    }
}
