package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Запрос на создание перевода между картами")
public class CreateTransferRequest {
    @Schema(description = "ID карты отправителя", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Укажите с какой карты должен быть произведен перевод")
    private Long fromCardId;

    @Schema(description = "ID карты получателя", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Укажите на какую карту должен быть произведен перевод")
    private Long toCardId;

    @NotNull(message = "Укажите сумму перевода")
    @Schema(description = "Сумма перевода", example = "500.75", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    public CreateTransferRequest() {
    }

    public CreateTransferRequest(Long fromCardId, Long toCardId, BigDecimal amount) {
        this.fromCardId = fromCardId;
        this.toCardId = toCardId;
        this.amount = amount;
    }
}
