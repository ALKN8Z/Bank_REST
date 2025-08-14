package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "DTO пользователя")
public class UserDto {

    @Schema(description = "ID пользователя", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "Имя пользователя", example = "ALKN8Z", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Пароль пользователя", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
