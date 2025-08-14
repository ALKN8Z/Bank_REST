package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int errorStatus;
    private String message;
    private LocalDateTime timestamp;
}
