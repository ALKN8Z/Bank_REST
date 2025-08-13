package com.example.bankcards.util;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.TransferStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TransferMapper {
    private final CardMapper cardMapper;
    private final UserMapper userMapper;

    public TransferDto toTransferDto(Transfer transfer) {
        return TransferDto.builder()
                .fromCard(cardMapper.toCardResponse(transfer.getFromCard()))
                .toCard(cardMapper.toCardResponse(transfer.getToCard()))
                .amount(transfer.getAmount())
                .createdAt(LocalDateTime.now())
                .transferStatus(TransferStatus.COMPLETED)
                .owner(userMapper.toUserDto(transfer.getOwner()))
                .build();
    }
}
