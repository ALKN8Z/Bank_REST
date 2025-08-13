package com.example.bankcards.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;



@Data
public class CreateCardRequest {

    @NotNull(message = "Id владельца не может быть null")
    private Long ownerId;

    @NotNull(message = "Укажите начальный баланс карты")
    private BigDecimal balance;

}
