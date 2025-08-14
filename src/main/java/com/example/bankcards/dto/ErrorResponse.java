package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Структура ответа с ошибкой")
public class ErrorResponse {
    @Schema(description = "HTTP статус ошибки", example = "404", requiredMode = Schema.RequiredMode.REQUIRED)
    private int errorStatus;
    @Schema(description = "Сообщение об ошибке", example = "Пользователь не найден", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
    @Schema(description = "Время возникновения ошибки", example = "2025-08-15T14:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime timestamp;
}
