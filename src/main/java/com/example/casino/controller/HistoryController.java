package com.example.casino.controller;

import com.example.casino.dto.BetHistoryResponseDto;
import com.example.casino.model.Transaction;
import com.example.casino.service.HistoryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService HistoryService;

    public HistoryController(HistoryService HistoryService) {
        this.HistoryService = HistoryService;
    }

    // Эндпоинт для получения истории ставок текущего пользователя
    @GetMapping("/bets")
    public List<BetHistoryResponseDto> getUserBetHistory(@AuthenticationPrincipal UserDetails userDetails) {
        // Получаем `email` пользователя из `UserDetails` (или любое другое уникальное поле)
        String email = userDetails.getUsername();

        // Логика извлечения `userId` на основе email
        Integer userId = HistoryService.getUserIdByEmail(email); // Предполагаем, что service может извлекать userId по email

        // Возвращаем историю ставок пользователя
        return HistoryService.getUserBetHistory(userId);
    }

    // Эндпоинт для получения истории транзакций текущего пользователя
    @GetMapping("/transactions")
    public List<Transaction> getUserTransactionHistory(@AuthenticationPrincipal UserDetails userDetails) {
        // Получаем `email` пользователя из `UserDetails`
        String email = userDetails.getUsername();

        // Логика извлечения `userId` на основе email
        Integer userId = HistoryService.getUserIdByEmail(email); // Предполагаем, что service может извлекать userId по email

        // Возвращаем историю транзакций пользователя
        return HistoryService.getUserTransactionHistory(userId);
    }
}

