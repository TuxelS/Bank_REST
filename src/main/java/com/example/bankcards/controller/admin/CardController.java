package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.CardStatusDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/admin/cards")
public class CardController {
    private final UserService userService;
    private final CardService cardService;

    @GetMapping
    public ResponseEntity<?> getUserCards(@RequestParam(name = "userId", required = false) Long userId) {
        return ResponseEntity.ok()
                .body(cardService.getUserCards(userId));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<?> getCard(@RequestParam(name = "cardId") Long cardId) {
        return ResponseEntity.ok()
                .body(cardService.getCardById(cardId).get());
    }


    @PostMapping("/{userId}")
    public ResponseEntity<?> createCard(@PathVariable("userId") Long userId) throws Exception {
        return ResponseEntity.ok()
                .body(cardService.createCard(userId));
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<?> updateCardStatus(@PathVariable("cardId") Long cardId,
                                              @RequestBody CardStatusDTO status) {
        cardService.updateCardStatus(cardId, status);
        return ResponseEntity.ok()
                .body("Status updated successfully");
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable("cardId") Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.ok()
                .body("Card deleted successfully");
    }

}
