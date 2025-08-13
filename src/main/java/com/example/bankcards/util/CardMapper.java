package com.example.bankcards.util;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardMapper {
    private final CardNumberUtils cardNumberUtils;

    public CardDto toCardDto(Card card) {
        return CardDto.builder()
                .id(card.getId())
                .number(cardNumberUtils.maskCardNumber(cardNumberUtils.decryptCardNumber(card.getNumber())))
                .cardStatus(card.getCardStatus())
                .expiryDate(card.getExpiryDate())
                .ownerUsername(card.getOwner().getUsername())
                .balance(card.getBalance())
                .build();
    }

    public CardResponse toCardResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .number(cardNumberUtils.maskCardNumber(cardNumberUtils.decryptCardNumber(card.getNumber())))
                .cardStatus(card.getCardStatus())
                .expiryDate(card.getExpiryDate())
                .balance(card.getBalance())
                .build();
    }

}
