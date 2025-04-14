package com.example.casino.service;

import com.example.casino.model.*;
import com.example.casino.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ActiveGameServiceTest {

    @Mock
    private ActiveGameRepository activeGameRepository;

    @Mock
    private BetHistoryRepository betHistoryRepository;

    @Mock
    private GameHistoryService gameHistoryService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ActiveGameService activeGameService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPlaceBet() {
        User user = new User();
        user.setId(1);
        user.setBalance(100.0);

        Game game = new Game();
        game.setId(1);
        game.setName("Ракетка");
        game.setMinBet(10.0);
        game.setMaxBet(100.0);

        ActiveGame activeGame = new ActiveGame();
        activeGame.setGameId(1);
        activeGame.setStartDate(LocalDateTime.now());
        activeGame.setEndDate(LocalDateTime.now().plusSeconds(60));
        activeGame.setFinished(false);
        activeGame.setParameters("{\"bets\":{}}");

        try {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(activeGameRepository.findById(1L)).thenReturn(Optional.of(activeGame));

        activeGameService.placeBet(1, 20.0, 2.0, "test@example.com");

        assertEquals(80.0, user.getBalance());
        verify(activeGameRepository, times(1)).save(any(ActiveGame.class));
        } catch (Exception e) {
            when(activeGameRepository.findById(1L)).thenReturn(Optional.of(activeGame));
        }
    }

    @Test
    public void testCancelBet() {
        User user = new User();
        user.setId(1);
        user.setBalance(80.0);

        Game game = new Game();
        game.setId(1);
        game.setName("Ракетка");
        game.setMinBet(10.0);
        game.setMaxBet(100.0);

        ActiveGame activeGame = new ActiveGame();
        activeGame.setGameId(1);
        activeGame.setStartDate(LocalDateTime.now());
        activeGame.setEndDate(LocalDateTime.now().plusSeconds(60));
        activeGame.setFinished(false);
        activeGame.setParameters("{\"bets\":{\"test@example.com\":[20.0,2.0]}}");

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(activeGameRepository.findById(1L)).thenReturn(Optional.of(activeGame));

        activeGameService.cancelBet(1, "test@example.com");

        assertEquals(80.0, user.getBalance());
        verify(activeGameRepository, times(1)).save(any(ActiveGame.class));

    }

    @Test
    public void testProcessRouletteGame() {
        ActiveGame activeGame = new ActiveGame();
        activeGame.setGameId(2);
        activeGame.setStartDate(LocalDateTime.now());
        activeGame.setEndDate(LocalDateTime.now().minusSeconds(10));
        activeGame.setFinished(false);
        activeGame.setParameters("{\"bets\":{\"user1@example.com\":[10.0,0],\"user2@example.com\":[20.0,0]}}");

        User user1 = new User();
        user1.setId(1);
        user1.setEmail("user1@example.com");
        user1.setBalance(100.0);

        User user2 = new User();
        user2.setId(2);
        user2.setEmail("user2@example.com");
        user2.setBalance(100.0);

        when(activeGameRepository.findByFinished(false)).thenReturn(List.of(activeGame));
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(user2));

        activeGameService.processActiveGames();

    }
}
