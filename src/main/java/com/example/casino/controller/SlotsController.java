package com.example.casino.controller;

import com.example.casino.dto.SlotsResponseDto;
import com.example.casino.service.SlotsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/games")
public class SlotsController {

    private final SlotsService slotsService;

    public SlotsController(SlotsService slotsService) {
        this.slotsService = slotsService;
    }

    @Operation(
            summary = "Игра в слоты",
            description = "Позволяет авторизованному пользователю сделать ставку и сыграть в игровую слот-машину."
    )
    @PostMapping("/slots")
    public ResponseEntity<?> playSlots(
            @NotNull(message = "Bet amount cannot be null")
            @Min(value = 1, message = "Bet amount must be greater than 0")
            @RequestBody int bet,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String email = userDetails.getUsername();
            SlotsResponseDto response = slotsService.playSlots(bet, email);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}