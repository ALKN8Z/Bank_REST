package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "DTO для обновления информации о карте")
public class UpdateCardRequest {
    @NotNull(message = "Укажите новый срок действия карты")
    @Schema(description = "Новый срок действия карты", example = "2026-12-31T23:59:59", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiryDate;

    @NotNull(message = "Укажите новый статус карты")
    @Schema(description = "Новый статус карты", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    private CardStatus cardStatus;
}
