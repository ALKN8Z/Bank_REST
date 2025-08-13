package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.UpdateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardMapper;
import com.example.bankcards.util.CardNumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardNumberUtils cardNumberUtils;
    private final CardMapper cardMapper;

    @Value("${app.security.card-expiration-years:2}")
    private int cardExpirationYears;

    @Override
    @Transactional
    public CardDto createCard(CreateCardRequest request) {
        Optional<User> user = userRepository.findById(request.getOwnerId());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id - " + request.getOwnerId() + " не найден");
        } else {
            String cardNumber = cardNumberUtils.generateCardNumber();
            String encryptedCardNumber = cardNumberUtils.encryptCardNumber(cardNumber);
            Card card = Card.builder()
                    .owner(user.get())
                    .number(encryptedCardNumber)
                    .cardStatus(CardStatus.ACTIVE)
                    .balance(request.getBalance())
                    .expiryDate(LocalDateTime.now().plusYears(cardExpirationYears))
                    .build();

            return cardMapper.toCardDto(cardRepository.save(card));
        }

    }

    @Override
    public Page<CardDto> getAllCards(Pageable pageable, String cardStatus, String ownerUsername) {
        User owner = null;
        if (ownerUsername != null) {
            owner = userRepository.findByUsername(ownerUsername).orElseThrow(
                    () -> new UserNotFoundException("Пользователь с именем " + ownerUsername + " не найден"));
        }
        Page<Card> cards;

        if (owner != null && cardStatus != null) {
            cards = cardRepository.findAllByOwnerAndCardStatus(owner, CardStatus.valueOf(cardStatus.toUpperCase()), pageable);
        } else if (owner != null) {
            cards = cardRepository.findAllByOwner(owner, pageable);
        } else if (cardStatus != null) {
            cards = cardRepository.findAllByCardStatus(CardStatus.valueOf(cardStatus.toUpperCase()), pageable);
        } else {
            cards = cardRepository.findAll(pageable);
        }

        return cards.map(cardMapper::toCardDto);
    }

    @Override
    public Page<CardDto> getMyCards(Pageable pageable, String cardStatus, String ownerUsername) {
        User owner = userRepository.findByUsername(ownerUsername).orElseThrow(
                () -> new UserNotFoundException("Пользователь " + ownerUsername + " не найден")
        );
        Page<Card> cards;

        if (cardStatus != null) {
            cards = cardRepository.findAllByOwnerAndCardStatus(owner, CardStatus.valueOf(cardStatus), pageable);
        } else{
            cards = cardRepository.findAllByOwner(owner, pageable);
        }

        return cards.map(cardMapper::toCardDto);
    }

    @Override
    public CardDto getCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                ()  -> new CardNotFoundException("Карта с id - " + id + " не найдена")
        );

        checkAccessToCard(card);

        return cardMapper.toCardDto(card);
    }

    @Override
    @Transactional
    public CardDto blockUserCard(Long cardId, String ownerUsername) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new CardNotFoundException("Карта с id - " + cardId + " не найдена")
        );

        if (!card.getOwner().getUsername().equals(ownerUsername)) {
            throw new AccessDeniedException("Вы не имеете доступа к этой карте");
        }
        card.setCardStatus(CardStatus.BLOCKED);
        return cardMapper.toCardDto(cardRepository.save(card));
    }

    @Override
    @Transactional
    public CardDto updateCard(UpdateCardRequest request, Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new CardNotFoundException("Карта с id - " + id + " не найдена")
        );

        card.setCardStatus(request.getCardStatus());
        card.setExpiryDate(request.getExpiryDate());

        return cardMapper.toCardDto(cardRepository.save(card));
    }

    @Override
    @Transactional
    public void deleteCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new CardNotFoundException("Карта с id - " + id + " не найдена")
        );
        cardRepository.delete(card);
    }

    @Override
    public BigDecimal getCardBalance(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new CardNotFoundException("Карта с id - " + id + " не найдена")
        );
        checkAccessToCard(card);
        return card.getBalance();
    }


    public void checkAccessToCard(Card card){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("Пользователь с именем" + username + " не найден")
        );

        if (!user.getRole().equals(Role.ADMIN) || !username.equals(card.getOwner().getUsername())) {
            throw new AccessDeniedException("Вы не имеете доступа к этой карте");
        }
    }
}
