package com.example.bankcards.controller;


import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.UpdateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardNumberUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Карты", description = "Методы управления банковскими картами")
public class CardController {
    private final CardService cardService;


    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Создать карту",
            description = "Создает новую банковскую карту. Доступно только для администратора.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Карта успешно создана",
                            content = @Content(schema = @Schema(implementation = CardDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
            }
    )
    public ResponseEntity<CardDto> createCard(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                          description = "Данные для создания карты",
                                                          required = true,
                                                          content = @Content(schema = @Schema(implementation = CreateCardRequest.class))
                                                  )@RequestBody @Valid CreateCardRequest request,
                                              UriComponentsBuilder uriBuilder) {
        CardDto cardDto = cardService.createCard(request);
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/cards/{cardId}")
                        .build(Map.of("cardId", cardDto.getId())))
                .body(cardDto);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Получить список всех карт",
            description = "Возвращает постраничный список карт. Доступно только для администратора."
    )
    public ResponseEntity<Page<CardDto>> getAllCards(
            Pageable pageable,
            @Parameter(description = "Статус карты", example = "ACTIVE") @RequestParam(name = "status", required = false) String cardStatus,
            @Parameter(description = "Имя владельца карты") @RequestParam(name = "username", required = false) String ownerUsername) {

        return ResponseEntity.ok(cardService.getAllCards(pageable, cardStatus, ownerUsername));
    }

    @GetMapping("/my")
    @Operation(
            summary = "Получить свои карты",
            description = "Возвращает постраничный список карт текущего пользователя."
    )
    public ResponseEntity<Page<CardDto>> getMyCards(
            Pageable pageable,
            @Parameter(description = "Статус карты", example = "BLOCKED") @RequestParam(name = "status", required = false) String cardStatus,
            Authentication authentication) {

        return ResponseEntity.ok(cardService.getMyCards(pageable, cardStatus, authentication.getName()));

    }

    @GetMapping("/{cardId}")
    @Operation(
            summary = "Получить карту по ID",
            description = "Возвращает информацию о карте по её идентификатору.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Карта найдена",
                            content = @Content(schema = @Schema(implementation = CardDto.class))),
                    @ApiResponse(responseCode = "404", description = "Карта не найдена")
            }
    )
    public ResponseEntity<CardDto> getCard(@Parameter(description = "ID карты", example = "10") @PathVariable(name = "cardId") Long cardId) {
        return ResponseEntity.ok(cardService.getCard(cardId));
    }

    @GetMapping("/{cardId}/balance")
    @Operation(
            summary = "Получить баланс карты",
            description = "Возвращает текущий баланс по карте."
    )
    public ResponseEntity<BigDecimal> getCardBalance(@Parameter(description = "ID карты", example = "15") @PathVariable(name = "cardId") Long cardId) {
        return ResponseEntity.ok(cardService.getCardBalance(cardId));
    }

    @PatchMapping("/{cardId}/block")
    @Operation(
            summary = "Заблокировать карту",
            description = "Блокирует карту пользователя."
    )
    public ResponseEntity<CardDto> blockCard(@Parameter(description = "ID карты", example = "12") @PathVariable(name = "cardId") Long cardId,
                                             Authentication authentication) {
        return ResponseEntity.ok(cardService.blockUserCard(cardId, authentication.getName()));
    }

    @PatchMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Обновить карту",
            description = "Обновляет данные карты. Доступно только для администратора."
    )
    public ResponseEntity<CardDto> updateCard(@Parameter(description = "ID карты", example = "7") @PathVariable(name = "cardId") Long cardId,
                                              @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                      description = "Данные для обновления карты",
                                                      required = true,
                                                      content = @Content(schema = @Schema(implementation = UpdateCardRequest.class))
                                              )@RequestBody @Valid UpdateCardRequest request) {
        return ResponseEntity.ok(cardService.updateCard(request, cardId));
    }

    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Удалить карту",
            description = "Удаляет карту по её идентификатору. Доступно только для администратора."
    )
    public ResponseEntity<Void> deleteCard(@Parameter(description = "ID карты", example = "9") @PathVariable(name = "cardId") Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

}
