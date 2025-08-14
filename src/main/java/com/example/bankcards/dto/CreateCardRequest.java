package com.example.bankcards.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;


@Data
@Schema(description = "Запрос на создание новой карты")
public class CreateCardRequest {

    @NotNull(message = "Id владельца не может быть null")
    @Schema(description = "Идентификатор владельца карты", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ownerId;

    @NotNull(message = "Укажите начальный баланс карты")
    @Schema(description = "Начальный баланс карты", example = "1000.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal balance;

}
