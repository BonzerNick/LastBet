package com.example.casino.dto;

import java.time.LocalDateTime;

public class ClientTokenDto {
    private Long id;
    private Long userId;
    private String token;
    private LocalDateTime iat;
    private LocalDateTime exp;

    // Getters and Setters
}