package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateCardRequest {
    @NotNull(message = "Укажите новый срок действия карты")
    private LocalDateTime expiryDate;

    @NotNull(message = "Укажите новый статус карты")
    private CardStatus cardStatus;
}
