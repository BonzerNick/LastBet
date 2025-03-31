package com.example.casino.service;

import com.example.casino.model.GameHistory;
import com.example.casino.repository.GameHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameHistoryService {

    private final GameHistoryRepository gameHistoryRepository;
    private final ObjectMapper objectMapper;

    public GameHistoryService(GameHistoryRepository gameHistoryRepository, ObjectMapper objectMapper) {
        this.gameHistoryRepository = gameHistoryRepository;
        this.objectMapper = objectMapper;
    }

    public void saveGameHistory(Integer gameId, String result, Integer balance, String parameters) {
        // Создание объекта GameHistory
        GameHistory gameHistory = new GameHistory();
        gameHistory.setGameId(Long.valueOf(gameId));
        gameHistory.setBalance(balance); // Ставка
        gameHistory.setResult(result); // Выигрыш
        gameHistory.setParameters(parameters);
        gameHistory.setDate(LocalDateTime.now()); // Текущее время

        // Сохранение в базу
        gameHistoryRepository.save(gameHistory);
    }
}