package com.example.casino.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TwoFactorService {

    private final SecureRandom random = new SecureRandom();

    // Метод для генерации 6-значного кода
    public String generateCode() {
        int code = 100000 + random.nextInt(900000); // Генерируем случайное число от 100000 до 999999
        return String.valueOf(code);
    }

    // Проверка истечения кода
    public boolean isCodeExpired(Instant expiration) {
        return Instant.now().isAfter(expiration);
    }

    // Генерация времени истечения
    public Instant generateCodeExpiration() {
        return Instant.now().plus(5, ChronoUnit.MINUTES); // Код действует 5 минут
    }
}
