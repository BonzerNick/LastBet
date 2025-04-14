package com.example.casino.controller;

import com.example.casino.dto.BetHistoryResponseDto;
import com.example.casino.model.GameHistory;
import com.example.casino.model.Transaction;
import com.example.casino.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@Validated
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Operation(
            summary = "Получение истории ставок текущего пользователя",
            description = "Позволяет получить список ставок, сделанных авторизованным пользователем."
    )
    @GetMapping("/bets")
    public List<BetHistoryResponseDto> getUserBetHistory(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Integer userId = historyService.getUserIdByEmail(email);
        return historyService.getUserBetHistory(userId);
    }

    @Operation(
            summary = "Получение истории транзакций текущего пользователя",
            description = "Возвращает список транзакций, выполненных авторизованным пользователем."
    )
    @GetMapping("/transactions")
    public List<Transaction> getUserTransactionHistory(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Integer userId = historyService.getUserIdByEmail(email);
        return historyService.getUserTransactionHistory(userId);
    }

    @Operation(
            summary = "Получение истории ставок по ID пользователя (для Администратора)",
            description = "Доступно только администраторам. Позволяет получить ставки пользователя по его ID."
    )
    @GetMapping("/bets/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<BetHistoryResponseDto> getBetHistoryByUserId(
            @PathVariable @Min(value = 1, message = "User ID must be greater than 0") Integer userId
    ) {
        return historyService.getUserBetHistory(userId);
    }

    @Operation(
            summary = "Получение истории транзакций пользователя по ID (для Администратора)",
            description = "Позволяет администратору получить историю транзакций конкретного пользователя по его ID."
    )
    @GetMapping("/transactions/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Transaction> getTransactionHistoryByUserId(
            @PathVariable @Min(value = 1, message = "User ID must be greater than 0") Integer userId
    ) {
        return historyService.getUserTransactionHistory(userId);
    }

    @Operation(
            summary = "Получение полной истории игр (для Администратора)",
            description = "Позволяет администратору получить полный список всех сыгранных игр."
    )
    @GetMapping("/games")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<GameHistory> getAllGameHistory() {
        return historyService.getAllGameHistory();
    }
}

