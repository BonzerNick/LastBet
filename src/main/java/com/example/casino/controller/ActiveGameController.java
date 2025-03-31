package com.example.casino.controller;

import com.example.casino.service.ActiveGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(
            summary = "Постановка ставки",
            description = "Позволяет авторизованному пользователю поставить ставку с указанными параметрами"
    )
    @PostMapping("/bet")
    public ResponseEntity<String> placeBet(
            @Parameter(description = "ID активной игры", required = true)
            @RequestParam Integer id,
            @Parameter(description = "Сумма ставки", required = true)
            @RequestParam Double bet,
            @Parameter(description = "Коэффициент выигрыша")
            @RequestParam(required = false) Double coef,
            @AuthenticationPrincipal
            @Parameter(hidden = true) UserDetails userDetails
    ){
        try {
            String email = userDetails.getUsername();
            activeGameService.placeBet(id, bet, coef, email);
            return ResponseEntity.ok("Ставка успешно принята");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
            summary = "Отмена ставки",
            description = "Позволяет авторизованному пользователю отменить свою ставку"
    )
    @DeleteMapping("/bet")
    public ResponseEntity<String> cancelBet(
            @Parameter(description = "ID ставки", required = true)
            @RequestParam Integer id,
            @AuthenticationPrincipal
            @Parameter(hidden = true) UserDetails userDetails
    ){
        try {
            String email = userDetails.getUsername();
            activeGameService.cancelBet(id, email);
            return ResponseEntity.ok("Ставка успешно отменена");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}