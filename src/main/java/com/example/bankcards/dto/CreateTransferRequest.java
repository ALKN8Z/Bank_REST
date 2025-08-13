package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransferRequest {
    @NotNull(message = "Укажите с какой карты должен быть произведен перевод")
    private Long fromCardId;

    @NotNull(message = "Укажите на какую карту должен быть произведен перевод")
    private Long toCardId;

    @NotNull(message = "Укажите сумму перевода")
    private BigDecimal amount;
}
