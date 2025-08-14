package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
public class CreateTransferRequest {
    @NotNull(message = "Укажите с какой карты должен быть произведен перевод")
    private Long fromCardId;

    @NotNull(message = "Укажите на какую карту должен быть произведен перевод")
    private Long toCardId;

    @NotNull(message = "Укажите сумму перевода")
    private BigDecimal amount;

    public CreateTransferRequest() {
    }

    public CreateTransferRequest(Long fromCardId, Long toCardId, BigDecimal amount) {
        this.fromCardId = fromCardId;
        this.toCardId = toCardId;
        this.amount = amount;
    }
}
