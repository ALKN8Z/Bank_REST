package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CardResponse {
    private Long id;
    private String number;
    private LocalDateTime expiryDate;
    private BigDecimal balance;
    private CardStatus cardStatus;
}
