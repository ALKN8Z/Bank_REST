package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с JWT токеном после успешной аутентификации или регистрации")
public class AuthResponse {
    @Schema(
            description = "JWT токен, используемый для авторизации последующих запросов",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String token;
}
