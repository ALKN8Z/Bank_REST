package com.example.bankcards.controller;

import com.example.bankcards.dto.CreateTransferRequest;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.service.TransferService;
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
public class TransferController {

    private final TransferService transferService;

    @PostMapping()
    public ResponseEntity<TransferDto> transfer(@RequestBody @Valid CreateTransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transferService.createTransfer(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<TransferDto>> getAllTransfers(Pageable pageable) {
        return ResponseEntity.ok(transferService.getAllTransfers(pageable));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<TransferDto>> getMyTransfers(Pageable pageable) {
        return ResponseEntity.ok(transferService.getMyTransfers(pageable));
    }
}
