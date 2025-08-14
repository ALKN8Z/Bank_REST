package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию пользователя")
public class AuthRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Schema(
            description = "Имя пользователя для входа в систему",
            example = "ALKN8Z",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Schema(
            description = "Пароль пользователя",
            example = "P@ssw0rd123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}
