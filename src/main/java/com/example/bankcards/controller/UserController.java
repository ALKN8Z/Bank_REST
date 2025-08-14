package com.example.bankcards.controller;


import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.dto.UserUpdateDto;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Методы для работы с информацией о пользователях")
public class UserController {
    private final UserService userService;


    @GetMapping("/my-info")
    @Operation(
            summary = "Получить информацию о текущем пользователе",
            description = "Возвращает данные о пользователе, который сделал запрос. Требуется авторизация.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о пользователе",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
            }
    )
    public ResponseEntity<UserResponse> getMyUserInfo() {
        return ResponseEntity.ok(userService.getMyUserInfo());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(
            summary = "Получить список всех пользователей",
            description = "Возвращает постраничный список всех пользователей. Доступно только для администраторов.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список пользователей",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "403", description = "Нет прав доступа")
            }
    )
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{userId}")
    @Operation(
            summary = "Получить информацию о пользователе по ID",
            description = "Возвращает данные о пользователе по его идентификатору. Доступно только для администраторов.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о пользователе",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Нет прав доступа")
            }
    )
    public ResponseEntity<UserResponse> getUser(@Parameter(description = "ID пользователя", example = "5") @PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}")
    @Operation(
            summary = "Обновить данные пользователя",
            description = "Обновляет данные пользователя по его идентификатору. Доступно только для администраторов.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлён",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Нет прав доступа")
            })
    public ResponseEntity<UserResponse> updateUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                               description = "Данные для обновления пользователя",
                                                               required = true,
                                                               content = @Content(schema = @Schema(implementation = UserUpdateDto.class)))@RequestBody UserUpdateDto request,
                                                   @Parameter(description = "ID пользователя", example = "1") @PathVariable(name = "userId") Long userId) {

        return ResponseEntity.ok(userService.updateUser(request, userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по его идентификатору. Доступно только для администраторов.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Пользователь успешно удалён"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Нет прав доступа")
            }
    )
    public ResponseEntity<Void> deleteUser(@Parameter(description = "ID пользователя", example = "9") @PathVariable(name = "userId") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
