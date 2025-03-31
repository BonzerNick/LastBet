package com.example.casino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BetHistoryResponseDto {
    private Integer id;
    private String gameName; // Название игры вместо gameId
    private double bet;
    private double win;
    private LocalDateTime date;
}
