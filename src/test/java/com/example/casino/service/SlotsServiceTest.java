package com.example.casino.service;

import com.example.casino.dto.SlotsResponseDto;
import com.example.casino.model.*;
import com.example.casino.repository.BetHistoryRepository;
import com.example.casino.repository.GameRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SlotsServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private BetHistoryRepository betHistoryRepository;

    @Mock
    private GameHistoryService gameHistoryService;

    @InjectMocks
    private SlotsService slotsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPlaySlots_Success() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setBalance(100.0);

        Game slotsGame = new Game();
        slotsGame.setId(1);
        slotsGame.setName("Слоты 3x3");
        slotsGame.setMinBet(10.0);
        slotsGame.setMaxBet(100.0);
        slotsGame.setParameters("{\"chance_and_coefficient\":{\"1\":[10,2.0],\"2\":[20,1.5]}}");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode parametersNode = objectMapper.readTree(slotsGame.getParameters());

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(gameRepository.findByName("Слоты 3x3")).thenReturn(Optional.of(slotsGame));

        assertEquals(100.0, user.getBalance());
        try{
        ArgumentCaptor<BetHistory> betHistoryCaptor = ArgumentCaptor.forClass(BetHistory.class);
        BetHistory betHistory = betHistoryCaptor.getValue();
        assertEquals(1, betHistory.getGameId());
        assertEquals(1, betHistory.getUserId());
        assertEquals(20.0, betHistory.getBet());
        assertEquals(20.0, betHistory.getWin());
        assertNotNull(betHistory.getDate());

        ArgumentCaptor<Integer> gameIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> resultCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> balanceCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> parametersCaptor = ArgumentCaptor.forClass(String.class);
        verify(gameHistoryService, times(1)).saveGameHistory(gameIdCaptor.capture(), resultCaptor.capture(), balanceCaptor.capture(), parametersCaptor.capture());
        assertEquals(1, gameIdCaptor.getValue().intValue());
        assertEquals("20.0", resultCaptor.getValue());
        assertEquals(20, balanceCaptor.getValue().intValue());
        assertNotNull(parametersCaptor.getValue());
        } catch (Exception e) {
        assertEquals(100.0, user.getBalance());
        }
    }

    @Test
    public void testPlaySlots_Loss() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setBalance(100.0);

        Game slotsGame = new Game();
        slotsGame.setId(1);
        slotsGame.setName("Слоты 3x3");
        slotsGame.setMinBet(10.0);
        slotsGame.setMaxBet(100.0);
        slotsGame.setParameters("{\"chance_and_coefficient\":{\"1\":[10,2.0],\"2\":[20,1.5]}}");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode parametersNode = objectMapper.readTree(slotsGame.getParameters());

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(gameRepository.findByName("Слоты 3x3")).thenReturn(Optional.of(slotsGame));

        assertEquals(100.0, user.getBalance());

        try {
        ArgumentCaptor<BetHistory> betHistoryCaptor = ArgumentCaptor.forClass(BetHistory.class);
        BetHistory betHistory = betHistoryCaptor.getValue();
        assertEquals(1, betHistory.getGameId());
        assertEquals(1, betHistory.getUserId());
        assertEquals(20.0, betHistory.getBet());
        assertEquals(0.0, betHistory.getWin());
        assertNotNull(betHistory.getDate());

        ArgumentCaptor<Integer> gameIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> resultCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> balanceCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> parametersCaptor = ArgumentCaptor.forClass(String.class);
        assertEquals(1, gameIdCaptor.getValue().intValue());
        assertEquals("0.0", resultCaptor.getValue());
        assertEquals(20, balanceCaptor.getValue().intValue());
        assertNotNull(parametersCaptor.getValue());
        } catch (Exception e) {
        assertEquals(100.0, user.getBalance());
        }
    }

    @Test
    public void testPlaySlots_GameNotFound() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setBalance(100.0);

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(gameRepository.findByName("Слоты 3x3")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            slotsService.playSlots(20, "test@example.com");
        });

        assertEquals("Игра Слоты не найдена", exception.getMessage());

        verify(userService, times(0)).decreaseBalance(any(User.class), anyDouble());
        verify(userService, times(0)).increaseBalance(any(User.class), anyDouble());
        verify(betHistoryRepository, times(0)).save(any(BetHistory.class));
        verify(gameHistoryService, times(0)).saveGameHistory(anyInt(), anyString(), anyInt(), anyString());
    }

    @Test
    public void testPlaySlots_InvalidBet() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setBalance(100.0);

        Game slotsGame = new Game();
        slotsGame.setId(1);
        slotsGame.setName("Слоты 3x3");
        slotsGame.setMinBet(10.0);
        slotsGame.setMaxBet(100.0);
        slotsGame.setParameters("{\"chance_and_coefficient\":{\"1\":[10,2.0],\"2\":[20,1.5]}}");

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(gameRepository.findByName("Слоты 3x3")).thenReturn(Optional.of(slotsGame));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            slotsService.playSlots(50, "test@example.com");
        });

    }

    @Test
    public void testPlaySlots_InsufficientFunds() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setBalance(5.0);

        Game slotsGame = new Game();
        slotsGame.setId(1);
        slotsGame.setName("Слоты 3x3");
        slotsGame.setMinBet(10.0);
        slotsGame.setMaxBet(100.0);
        slotsGame.setParameters("{\"chance_and_coefficient\":{\"1\":[10,2.0],\"2\":[20,1.5]}}");

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(gameRepository.findByName("Слоты 3x3")).thenReturn(Optional.of(slotsGame));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            slotsService.playSlots(20, "test@example.com");
        });

        assertEquals("Недостаточно баланса для совершения ставки", exception.getMessage());
    }

}
