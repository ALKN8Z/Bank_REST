package com.example.bankcards.controller;

import com.example.bankcards.dto.CreateTransferRequest;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Переводы", description = "Методы для создания и получения переводов")
public class TransferController {

    private final TransferService transferService;

    @PostMapping()
    @Operation(
            summary = "Создать перевод",
            description = "Создает новый перевод между картами. " +
                    "Пользователь должен быть авторизован.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Перевод успешно создан",
                            content = @Content(schema = @Schema(implementation = TransferDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные перевода"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет доступа для выполнения перевода")
            }
    )
    public ResponseEntity<TransferDto> transfer(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные для создания перевода",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateTransferRequest.class)))@RequestBody @Valid CreateTransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transferService.createTransfer(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    @Operation(
            summary = "Получить все переводы (только для администратора)",
            description = "Возвращает постраничный список всех переводов в системе. " +
                    "Доступно только для пользователей с ролью ADMIN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список переводов",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "403", description = "Нет прав доступа")
            }
    )
    public ResponseEntity<Page<TransferDto>> getAllTransfers(Pageable pageable) {
        return ResponseEntity.ok(transferService.getAllTransfers(pageable));
    }

    @GetMapping("/my")
    @Operation(
            summary = "Получить мои переводы",
            description = "Возвращает постраничный список переводов, которые совершил авторизованный пользователь.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список переводов пользователя",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
            }
    )
    public ResponseEntity<Page<TransferDto>> getMyTransfers(Pageable pageable) {
        return ResponseEntity.ok(transferService.getMyTransfers(pageable));
    }
}
