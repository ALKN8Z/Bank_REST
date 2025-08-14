package com.example.bankcards.dto;
import com.example.bankcards.entity.TransferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Builder
@Schema(description = "DTO для информации о переводе между картами")
public class TransferDto {
    @Schema(description = "Информация о карте отправителя", requiredMode = Schema.RequiredMode.REQUIRED)
    private CardResponse fromCard;

    @Schema(description = "Информация о карте получателя", requiredMode = Schema.RequiredMode.REQUIRED)
    private CardResponse toCard;

    @Schema(description = "Информация о владельце перевода", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDto owner;

    @Schema(description = "Сумма перевода", example = "1500.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Дата и время создания перевода", example = "2025-08-15T14:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "Статус перевода", example = "COMPLETED", requiredMode = Schema.RequiredMode.REQUIRED)
    private TransferStatus transferStatus;
}
