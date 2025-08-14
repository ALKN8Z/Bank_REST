package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@Schema(description = "DTO для отображения ошибок валидации при запросах")
public class ValidationErrorResponse {

    @Schema(description = "HTTP статус ошибки", example = "400")
    private int statusError;

    @Schema(description = "Список ошибок по полям", example = "{\"username\": \"Имя пользователя не может быть пустым\", \"password\": \"Пароль не может быть пустым\"}")
    private Map<String, String> errors;

    @Schema(description = "Время возникновения ошибки", example = "2025-08-15T12:34:56")
    private LocalDateTime timestamp;
}
