package com.example.bankcards.controller.user;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardStatusDTO;
import com.example.bankcards.enumeration.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/cards")
@RequiredArgsConstructor
public class PersonalCardController {
    private final CardService cardService;
    private final UserService userService;

//    GET /api/user/cards
//    GET /api/user/cards?search=1234
//    GET /api/user/cards?status=BLOCKED
//    GET /api/user/cards?search=1234&status=ACTIVE&page=0&size=5
    @GetMapping
    public ResponseEntity<Page<CardDTO>> getUserCard(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Authentication authentication) {
        Page<CardDTO> cards = cardService.getUserCardPaginable(search,
                status, page, size, authentication.getName());
        return ResponseEntity.ok()
                .body(cards);
    }
}
