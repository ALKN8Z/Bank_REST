package com.example.bankcards.controller;


import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Методы для входа и регистрации пользователей")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Вход в систему",
            description = "Авторизует пользователя и возвращает JWT токен. " +
                    "Токен также добавляется в заголовок `Authorization`.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешная авторизация",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Неверный логин или пароль")
            }
    )
    public ResponseEntity<AuthResponse> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные для входа (логин и пароль)",
            required = true,
            content = @Content(schema = @Schema(implementation = AuthRequest.class)))@RequestBody @Valid AuthRequest authRequest) {

        String token = authService.login(authRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(new AuthResponse(token));
    }

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя и возвращает JWT токен. " +
                    "Токен также добавляется в заголовок `Authorization`.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные регистрации или пользователь уже существует")
            }
    )
    public ResponseEntity<AuthResponse> register( @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные для регистрации (логин и пароль)",
            required = true,
            content = @Content(schema = @Schema(implementation = AuthRequest.class)))@RequestBody @Valid AuthRequest authRequest) {
        String token = authService.register(authRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(new AuthResponse(token));
    }
}
