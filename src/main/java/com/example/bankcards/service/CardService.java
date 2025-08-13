package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.UpdateCardRequest;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CardService {
    CardDto createCard(CreateCardRequest request);
    Page<CardDto> getAllCards(Pageable pageable, String cardStatus, String ownerUsername);
    Page<CardDto> getMyCards(Pageable pageable, String cardStatus, String ownerUsername);
    CardDto getCard(Long id);
    CardDto blockUserCard(Long cardId, String ownerUsername);
    CardDto updateCard(UpdateCardRequest request, Long id);
    void deleteCard(Long id);
    BigDecimal getCardBalance(Long id);
}
