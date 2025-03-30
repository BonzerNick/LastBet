package com.example.casino.controller;

import com.example.casino.service.ActiveGameService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/active_game")
public class ActiveGameController {

    private final ActiveGameService activeGameService;

    public ActiveGameController(ActiveGameService activeGameService) {
        this.activeGameService = activeGameService;
    }

    // Постановка ставки
    @PostMapping("/bet")
    public ResponseEntity<String> placeBet(
            @RequestParam Integer id,
            @RequestParam Double bet,
            @RequestParam(required = false) Double coef,
            @AuthenticationPrincipal UserDetails userDetails){ // Получаем авторизованного пользователя
        try {
            String email = userDetails.getUsername();
            activeGameService.placeBet(id, bet, coef, email);
            return ResponseEntity.ok("Ставка успешно принята");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Отмена ставки
    @DeleteMapping("/bet")
    public ResponseEntity<String> cancelBet(
            @RequestParam Integer id,
            @AuthenticationPrincipal UserDetails userDetails){ // Получаем авторизованного пользователя)
        try {
            String email = userDetails.getUsername();
            activeGameService.cancelBet(id, email);
            return ResponseEntity.ok("Ставка успешно отменена");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}