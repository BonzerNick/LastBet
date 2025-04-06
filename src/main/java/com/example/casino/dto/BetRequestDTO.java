package com.example.casino.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class BetRequestDTO {
    @NotNull(message = "ID активной игры не может быть null")
    @Min(value = 1, message = "ID активной игры должен быть положительным числом")
    private Integer gameId;

    @NotNull(message = "Сумма ставки не может быть null")
    @Min(value = 0, message = "Сумма ставки не может быть отрицательной")
    private Double bet;

    @Min(value = 1, message = "Коэффициент выигрыша должен быть положительным числом")
    private Double coef;

}