package com.example.casino.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SlotsResponseDto {

    private List<List<Integer>> results; // Три ряда по 3 числа
    private double winnings; // Выигрыш пользователя

    public SlotsResponseDto(List<List<Integer>> results, double winnings) {
        this.results = results;
        this.winnings = winnings;
    }

}