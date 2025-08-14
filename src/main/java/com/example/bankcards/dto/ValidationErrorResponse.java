package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    private int statusError;
    private Map<String, String> errors;
    private LocalDateTime timestamp;
}
