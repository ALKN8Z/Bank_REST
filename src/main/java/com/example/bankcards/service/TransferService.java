package com.example.bankcards.service;

import com.example.bankcards.dto.CreateTransferRequest;
import com.example.bankcards.dto.TransferDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface TransferService {
    TransferDto createTransfer(CreateTransferRequest request);
    Page<TransferDto> getAllTransfers(Pageable pageable);
    Page<TransferDto> getMyTransfers(Pageable pageable);
}
