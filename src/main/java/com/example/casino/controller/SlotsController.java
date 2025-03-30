package com.example.casino.controller;

import com.example.casino.dto.SlotsResponseDto;
import com.example.casino.service.SlotsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
public class SlotsController {

    private final SlotsService slotsService;

    public SlotsController(SlotsService slotsService) {
        this.slotsService = slotsService;
    }

    @PostMapping("/slots")
    public ResponseEntity<?> playSlots(
            @RequestBody int bet, // Принимаем только число
            @AuthenticationPrincipal UserDetails userDetails // Получаем авторизованного пользователя
    ) {
        try {
            // Извлекаем имя пользователя из авторизованной информации
            String email = userDetails.getUsername();

            // Передаем запрос в сервисный слой
            SlotsResponseDto response = slotsService.playSlots(bet, email);

            // Возвращаем результат пользователю (HTTP 200 OK)
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Возвращаем ошибку с пояснением (HTTP 400 Bad Request)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}