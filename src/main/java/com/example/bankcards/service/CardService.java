package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CardStatusDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enumeration.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardEncryptor;
import com.example.bankcards.util.CardMapper;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.CardSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {
    private final UserService userService;
    private final CardRepository cardRepository;
    private final CardEncryptor cardEncryptor;
    private final CardMapper cardMapper;

    @Value("${card.unmasked-symbols}")
    private Integer unmaskedCountOfSymbols;

    public List<CardDTO> getUserCards(Long userId) {
        List<Card> cards = (userId == null)
                ? cardRepository.findAll()
                : cardRepository.findByOwner_Id(userId);

        return cards.stream()
                .map(cardMapper::toDTO)
                .toList();
    }

    public Optional<Card> getCardById(Long cardId) {
        return cardRepository.findById(cardId);
    }

    @Transactional
    public Card createCard(Long userId) throws Exception {
        Card card = new Card();
        String cardNumber = CardNumberGenerator.generateValidCardNumber();
        String encryptedCardNumber = cardEncryptor.encrypt(cardNumber);
        log.info("Процесс создания карты, номер карты = {}, зашифрованная карта = {}", cardNumber, encryptedCardNumber);
        card.setLastFourSymbols(cardNumber.substring(cardNumber.length() - unmaskedCountOfSymbols));
        card.setCardNumberEncrypted(encryptedCardNumber);
        Optional<User> user = userService.findById(userId);
        user.ifPresent(card::setOwner);
        Card savedCard = cardRepository.save(card);
        return savedCard;
    }

    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    @Transactional
    public void updateCardStatus(Long cardId, CardStatusDTO status) {
        Optional<Card> card = cardRepository.findById(cardId);
        card.get().setCardStatus(status.getCardStatus());
        cardRepository.save(card.get());
    }


    public Page<CardDTO> getUserCardPaginable(String search, String status,
                                              int page, int size, String name) {
        Pageable pageable = PageRequest.of(page, size);
        // добавляем условия к запросу
        Specification<Card> spec = CardSpecifications.hasOwner(name);
        if (status != null) {
            spec = spec.and(CardSpecifications.hasStatus(status));
        }
        if (search != null) {
            // т.к. номер является зашифрованным в БД, есть только последние 4 цифры
            spec = spec.and(CardSpecifications.hasLastFour(search));
        }
        Page<Card> cards = cardRepository.findAll(spec,pageable);

        Page<CardDTO> dtoPage = cards.map(cardMapper::toDTO);
        return dtoPage;
    }
}
