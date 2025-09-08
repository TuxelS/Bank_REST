package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.RegistrationUserDTO;
import com.example.bankcards.exception.ApplicationError;
import com.example.bankcards.exception.UserExistsException;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    // если isActive = null, тогда выдаст просто всех юзеров, активных, заблоченных и тд
    // не вижу смысла в дто запихивать, тк это эндпоинт для роли администратора.
    @GetMapping()
    public ResponseEntity<?> getUsers(@RequestParam(name = "isActive", required = false) Boolean isActive) {
            return ResponseEntity.ok()
                    .body(userService.findAll(isActive));
    }

    // метод для блокировки чела
    @PostMapping("/{id}/to-block")
    public ResponseEntity<?> toBlockUserById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok()
                .body(userService.toBlockUserById(id));
    }

}
