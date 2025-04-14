package com.example.casino.service;

import com.example.casino.model.GameHistory;
import com.example.casino.repository.GameHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class GameHistoryServiceTest {

    @Mock
    private GameHistoryRepository gameHistoryRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GameHistoryService gameHistoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveGameHistory() {
        Integer gameId = 1;
        String result = "Выигрыш";
        Integer balance = 100;
        String parameters = "{\"user_id\":1,\"results\":\"[[1,2,3],[4,5,6],[7,8,9]]\"}";

        gameHistoryService.saveGameHistory(gameId, result, balance, parameters);

        ArgumentCaptor<GameHistory> gameHistoryCaptor = ArgumentCaptor.forClass(GameHistory.class);
        verify(gameHistoryRepository, times(1)).save(gameHistoryCaptor.capture());

        GameHistory gameHistory = gameHistoryCaptor.getValue();
        assertEquals(Long.valueOf(gameId), gameHistory.getGameId());
        assertEquals(result, gameHistory.getResult());
        assertEquals(parameters, gameHistory.getParameters());
        assertNotNull(gameHistory.getDate());
    }
}
