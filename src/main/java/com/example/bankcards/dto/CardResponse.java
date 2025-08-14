package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Ответ с информацией о карте")
public class CardResponse {
    @Schema(description = "Уникальный идентификатор карты", example = "1")
    private Long id;
    @Schema(description = "Номер карты в формате **** **** **** ****", example = "1234 5678 9012 3456")
    private String number;
    @Schema(description = "Дата истечения срока действия карты", example = "2028-12-31T23:59:59")
    private LocalDateTime expiryDate;
    @Schema(description = "Текущий баланс карты", example = "1500.75")
    private BigDecimal balance;
    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus cardStatus;
}
