package com.example.bankcards.util;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardMapper {
    private final CardEncryptor cardEncryptor;

    public CardDTO toDTO(Card card)  {
        CardDTO cardDTO = new CardDTO();
        cardDTO.setId(card.getId());
        cardDTO.setLastFourSymbols(card.getLastFourSymbols());
        String decryptedCard = cardEncryptor.decrypt(card.getCardNumberEncrypted());
        cardDTO.setMaskedCard(cardEncryptor.toMaskCardNumber(decryptedCard));
        cardDTO.setOwner(card.getOwner());
        cardDTO.setCardStatus(card.getCardStatus());
        cardDTO.setBalance(card.getBalance());
        cardDTO.setExpirationDate(card.getExpirationDate());
        return cardDTO;
    }

}
