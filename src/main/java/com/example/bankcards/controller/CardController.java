package com.example.bankcards.controller;


import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.UpdateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardNumberUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;


    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> createCard(@RequestBody @Valid CreateCardRequest request,
                                              UriComponentsBuilder uriBuilder) {
        CardDto cardDto = cardService.createCard(request);
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/cards/{cardId}")
                        .build(Map.of("cardId", cardDto.getId())))
                .body(cardDto);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardDto>> getAllCards(
            Pageable pageable,
            @RequestParam(name = "status", required = false) String cardStatus,
            @RequestParam(name = "username", required = false) String ownerUsername) {

        return ResponseEntity.ok(cardService.getAllCards(pageable, cardStatus, ownerUsername));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<CardDto>> getMyCards(
            Pageable pageable,
            @RequestParam(name = "status", required = false) String cardStatus,
            Authentication authentication) {

        return ResponseEntity.ok(cardService.getMyCards(pageable, cardStatus, authentication.getName()));

    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardDto> getCard(@PathVariable(name = "cardId") Long cardId) {
        return ResponseEntity.ok(cardService.getCard(cardId));
    }

    @GetMapping("/{cardId}/balance")
    public ResponseEntity<BigDecimal> getCardBalance(@PathVariable(name = "cardId") Long cardId) {
        return ResponseEntity.ok(cardService.getCardBalance(cardId));
    }

    @PatchMapping("/{cardId}/block")
    public ResponseEntity<CardDto> blockCard(@PathVariable(name = "cardId") Long cardId,
                                             Authentication authentication) {
        return ResponseEntity.ok(cardService.blockUserCard(cardId, authentication.getName()));
    }

    @PatchMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardDto> updateCard(@PathVariable(name = "cardId") Long cardId,
                                              @RequestBody @Valid UpdateCardRequest request) {
        return ResponseEntity.ok(cardService.updateCard(request, cardId));
    }

    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable(name = "cardId") Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

}
