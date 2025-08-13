package com.example.bankcards.dto;
import com.example.bankcards.entity.TransferStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Builder
public class TransferDto {
    private CardResponse fromCard;
    private CardResponse toCard;
    private UserDto owner;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private TransferStatus transferStatus;
}
