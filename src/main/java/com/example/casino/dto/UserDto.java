package com.example.casino.dto;

import java.time.LocalDateTime;

public class UserDto {
    private Integer id;
    private String email;
    private double balance;
    private String phoneNumber;
    private Boolean is2fa;
    private LocalDateTime lastVisit;
    private String language;

    // Getters and Setters
}