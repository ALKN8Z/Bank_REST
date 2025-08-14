package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Schema(description = "Ответ с информацией о пользователе")
public class UserResponse {

    @Schema(description = "ID пользователя", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "Имя пользователя", example = "ALKN8Z", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Пароль пользователя (закодированный)", example = "hashed_password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "Роль пользователя", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    private Role role;

    @Schema(description = "Список карт пользователя")
    private Set<CardResponse> cards;
}
