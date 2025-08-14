package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "DTO для обновления информации о пользователе")
public class UserUpdateDto {

    @Schema(description = "Новое имя пользователя", example = "new_john_doe")
    private String username;

    @Schema(description = "Новый пароль пользователя", example = "new_secure_password")
    private String password;

    @Schema(description = "Новая роль пользователя", example = "USER")
    private Role role;
}
